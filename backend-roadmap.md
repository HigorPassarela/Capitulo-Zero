# Roadmap de implementação

O backend inteiro está descrito em 10 changes do OpenSpec, em `openspec/changes/`. Elas devem ser implementadas
**nesta ordem**: cada uma depende das anteriores e foi escrita assumindo que elas já estão arquivadas.

| # | Change | Entrega | Capabilities (specs) |
|---|---|---|---|
| 01 | `01-fundacao-plataforma` | Projeto Maven, docker compose, módulo `compartilhado`, erros RFC 9457, segurança base, outbox + Kafka, harness de testes, CI | `plataforma/api`, `plataforma/eventos` |
| 02 | `02-identidade-e-acesso` | Cadastro, login JWT, refresh token com rotação, rate limit de login, perfil e endereços, admin inicial | `identidade/autenticacao`, `identidade/contas` |
| 03 | `03-catalogo-de-livros` | Livros, autores, categorias, administração do catálogo, busca full-text com tolerância a erro de digitação, cache | `catalogo/livros`, `catalogo/busca` |
| 04 | `04-estoque-e-reservas` | Saldo por livro, movimentos, reservas com expiração, concorrência segura, alerta de estoque baixo | `estoque/saldo`, `estoque/reservas` |
| 05 | `05-carrinho-e-cupons` | Carrinho no Redis, mesclagem com carrinho anônimo, cupons de desconto | `carrinho/itens`, `carrinho/cupons` |
| 06 | `06-frete-e-remessas` | Cotação de frete por faixa de CEP e peso, remessas, rastreio, simulador logístico | `envio/frete`, `envio/remessas` |
| 07 | `07-pagamentos-pix-simulado` | Cobrança Pix simulada, webhook com HMAC e idempotência, simulador de PSP, expiração, estorno | `pagamentos/cobrancas-pix`, `pagamentos/webhook-psp`, `pagamentos/simulador-psp` |
| 08 | `08-pedidos-e-checkout` | Checkout idempotente, saga do pedido, cancelamentos e compensações, consultas | `pedidos/checkout`, `pedidos/ciclo-de-vida`, `pedidos/consulta` |
| 09 | `09-notificacoes-por-email` | Consumidores Kafka, e-mails transacionais via Mailpit, retentativas e DLT | `notificacoes/emails-transacionais` |
| 10 | `10-observabilidade-e-demo` | OpenTelemetry + Grafana, métricas de negócio, dados de demonstração, jornada E2E, README final | `plataforma/observabilidade`, `plataforma/dados-demo` |

## Como executar com o Claude Code

Abra o Claude Code na raiz deste repositório e rode:

```
/cz:proxima
```

Ele pega a primeira change não arquivada, implementa todas as tarefas, roda a verificação completa,
arquiva a change e faz o commit. Depois disso, ele para e te mostra o resumo.

Para seguir direto para a próxima change sem parar, use `/cz:proxima todas`.
Mesmo nesse modo, recomendo rodar `/clear` entre as changes se a sessão ficar longa, porque cada change é grande.

Para ver o que falta: `openspec list`. Para ver o que já foi consolidado: `openspec list --specs`.
