# Arquitetura

O Capítulo Zero é um **monólito modular**: uma única aplicação Spring Boot, implantada como um único artefato,
dividida em módulos de domínio com fronteiras verificadas por teste (Spring Modulith). Os módulos conversam por
uma interface pública (`XxxApi`) quando precisam de resposta imediata e por **eventos de domínio** quando
apenas reagem a um fato. Eventos relevantes para fora do processo são publicados no Kafka a partir de uma outbox.

Não há integrações externas. Pagamento (Pix) e logística são simulados dentro da aplicação, mas pelos mesmos
caminhos de um sistema real: webhook assinado com HMAC, idempotência, expiração e compensação.

## Visão geral

```mermaid
flowchart LR
    subgraph clientes["Clientes"]
        loja["Loja web (React)"]
        admin["Painel admin (React)"]
    end

    subgraph api["capitulo-zero-api · Spring Boot 4.1 · Java 21"]
        web["REST /api/v1 · OpenAPI · JWT"]
        subgraph modulos["Módulos (Spring Modulith)"]
            identidade["identidade"]
            catalogo["catalogo"]
            estoque["estoque"]
            carrinho["carrinho"]
            pedidos["pedidos"]
            pagamentos["pagamentos"]
            envio["envio"]
            notificacoes["notificacoes"]
        end
        outbox["Outbox (event publication registry)"]
        jobs["Jobs agendados (ShedLock)"]
    end

    subgraph infra["Infraestrutura local (docker compose)"]
        pg[("PostgreSQL 17<br/>1 schema por módulo")]
        redis[("Redis<br/>cache · carrinho · idempotência")]
        kafka[["Kafka (KRaft)"]]
        mailpit["Mailpit (SMTP)"]
        lgtm["Grafana LGTM (OTel)"]
    end

    loja --> web
    admin --> web
    web --> modulos
    modulos --> pg
    modulos --> redis
    outbox --> kafka
    kafka --> notificacoes
    notificacoes --> mailpit
    api -. OTLP .-> lgtm
```

## Módulos e dependências

```mermaid
flowchart TB
    pedidos --> identidade
    pedidos --> carrinho
    pedidos --> estoque
    pedidos --> envio
    pedidos --> pagamentos
    carrinho --> catalogo
    carrinho --> estoque
    estoque --> catalogo
    envio --> catalogo
    notificacoes -. Kafka (JSON) .-> pedidos
```

As setas apontam de quem **depende** para quem **é usado**. A dependência pode ser uma chamada à `XxxApi`
ou o consumo de um evento publicado pelo outro módulo. Todos podem usar `compartilhado`.
A seta tracejada não é uma dependência de código: `notificacoes` só conhece o JSON dos tópicos.

| Módulo | Dados (schema) | Expõe (`XxxApi`) | Publica | Consome |
|---|---|---|---|---|
| identidade | contas, enderecos, refresh_tokens | buscar conta, buscar endereço | `ContaCriada` ⇢ Kafka | n/a |
| catalogo | livros, autores, categorias | resumos de livros | `LivroCadastrado`, `PrecoAlterado`, `LivroDesativado` | n/a |
| estoque | itens_estoque, movimentos, reservas | reservar, confirmar, liberar, devolver, disponibilidade | `ReservaExpirada`, `EstoqueBaixo` ⇢ Kafka | `LivroCadastrado` |
| carrinho | cupons, usos_cupom (+ carrinho no Redis) | preparar checkout, registrar uso de cupom, limpar | n/a | n/a |
| envio | remessas, eventos_remessa | cotar frete, criar e cancelar remessa | `RemessaPostada`, `RemessaEntregue` | n/a |
| pagamentos | cobrancas, webhooks_recebidos | criar e cancelar cobrança, estornar | `PagamentoAprovado`, `PagamentoRecusado`, `PagamentoExpirado`, `PagamentoEstornado` | n/a |
| pedidos | pedidos, itens, historico | n/a | `PedidoCriado`, `PedidoPago`, `PedidoEnviado`, `PedidoEntregue`, `PedidoCancelado` ⇢ Kafka | eventos de pagamentos, estoque e envio |
| notificacoes | notificacoes, mensagens_processadas | n/a | n/a | tópicos Kafka |
| demo (perfil `demo`) | n/a | n/a | n/a | n/a. Popula a loja chamando a própria API REST |

O contrato completo dos eventos e tópicos está em [`eventos.md`](eventos.md).

## Checkout (saga orquestrada pelo módulo pedidos)

```mermaid
sequenceDiagram
    autonumber
    participant C as Cliente
    participant P as pedidos
    participant Ca as carrinho
    participant I as identidade
    participant En as envio
    participant E as estoque
    participant Pg as pagamentos
    participant K as Kafka

    C->>P: POST /api/v1/pedidos (Idempotency-Key)
    P->>Ca: prepararCheckout(conta)
    P->>I: buscarEndereco(conta, enderecoId)
    P->>En: cotar(cep, itens, valorProdutos)
    P->>E: reservar(pedidoId, itens, 35 min)
    P->>Ca: registrarUsoCupom(...)
    P->>Pg: criarCobrancaPix(pedidoId, total)
    P-->>K: PedidoCriado (outbox)
    P-->>C: 201 + dados do Pix
    Note over P,Ca: após o commit, o carrinho é limpo

    Pg-->>P: PagamentoAprovado (webhook assinado)
    P->>E: confirmar(pedidoId)
    P->>En: criarRemessa(...)
    P-->>K: PedidoPago
```

Tudo entre os passos 2 e 8 acontece em **uma transação** no mesmo PostgreSQL: se a reserva falhar, nada é gravado.
Redis (limpeza do carrinho) e Kafka (via outbox) ficam fora da transação, por desenho.

## Máquina de estados do pedido

```mermaid
stateDiagram-v2
    [*] --> AGUARDANDO_PAGAMENTO: checkout
    AGUARDANDO_PAGAMENTO --> PAGO: PagamentoAprovado
    AGUARDANDO_PAGAMENTO --> CANCELADO: cliente cancela / pagamento expira / pagamento recusado / reserva expira / admin
    PAGO --> ENVIADO: RemessaPostada
    PAGO --> CANCELADO: admin cancela (estorno + devolução ao estoque)
    ENVIADO --> ENTREGUE: RemessaEntregue
    ENTREGUE --> [*]
    CANCELADO --> [*]
```

Se um `PagamentoAprovado` chegar para um pedido já `CANCELADO`, o pedido continua cancelado e o pagamento
é estornado automaticamente.

## Decisões

As decisões estão registradas em [`adr/`](adr/):

- [0001](adr/0001-monolito-modular.md): monólito modular com Spring Modulith
- [0002](adr/0002-eventos-outbox-kafka.md): eventos com outbox e externalização para Kafka
- [0003](adr/0003-saga-sem-motor-bpmn.md): saga do checkout sem motor BPMN
- [0004](adr/0004-jwt-proprio.md): autenticação com JWT emitido pela própria aplicação
- [0005](adr/0005-integracoes-simuladas.md): pagamento e envio simulados
- [0006](adr/0006-redis-dados-efemeros.md): Redis apenas para dados efêmeros
