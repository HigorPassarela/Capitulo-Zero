# Glossary

Canonical English names for the Capítulo Zero domain. The project was first specified in Portuguese; this table
keeps every artifact (code, API contract, events, docs, specs) using the same words. When a new concept appears,
add it here before using it.

Example store data (book titles, author names, customer names, coupon codes such as `BEMVINDO10`) stays as is:
it is content, not vocabulary.

## Modules

| Portuguese (original) | Module / package | Database schema |
|---|---|---|
| compartilhado | `shared` | n/a |
| identidade | `identity` | `identity` |
| catalogo | `catalog` | `catalog` |
| estoque | `inventory` | `inventory` |
| carrinho | `cart` | `cart` |
| envio | `shipping` | `shipping` |
| pagamentos | `payments` | `payments` |
| pedidos | `orders` | `orders` |
| notificacoes | `notifications` | `notifications` |
| demo | `demo` | n/a |

Module subpackages: `domain`, `application`, `web`, `infra`.

## Changes and capabilities

| # | Change | Capabilities |
|---|---|---|
| 01 | `01-platform-foundation` | `platform/api`, `platform/events` |
| 02 | `02-identity-and-access` | `identity/authentication`, `identity/accounts` |
| 03 | `03-book-catalog` | `catalog/books`, `catalog/search` |
| 04 | `04-inventory-and-reservations` | `inventory/stock`, `inventory/reservations` |
| 05 | `05-cart-and-coupons` | `cart/items`, `cart/coupons` |
| 06 | `06-shipping-rates-and-shipments` | `shipping/rates`, `shipping/shipments` |
| 07 | `07-simulated-pix-payments` | `payments/pix-charges`, `payments/psp-webhook`, `payments/psp-simulator` |
| 08 | `08-orders-and-checkout` | `orders/checkout`, `orders/lifecycle`, `orders/queries` |
| 09 | `09-email-notifications` | `notifications/transactional-emails` |
| 10 | `10-observability-and-demo` | `platform/observability`, `platform/demo-data` |

## Shared module and platform

| Portuguese | English |
|---|---|
| Dinheiro | `Money` |
| PaginaResposta {conteudo, pagina, tamanho, totalElementos, totalPaginas} | `PageResponse` {`content`, `page`, `size`, `totalElements`, `totalPages`} |
| ParametrosPaginacao; query `pagina`, `tamanho`, `ordenar` | `PageParams`; query `page`, `size`, `sort` |
| RelogioConfig | `ClockConfig` |
| ErroDeNegocio (`codigo`) | `BusinessException` (`code`) |
| RecursoNaoEncontrado / Conflito / TransicaoInvalida / RegraDeNegocio | `ResourceNotFoundException` / `ConflictException` / `InvalidTransitionException` / `BusinessRuleException` |
| TratadorDeErros | `GlobalExceptionHandler` |
| FiltroRequestId | `RequestIdFilter` |
| SegurancaConfig / RegrasDeAcesso / JwtConfig | `SecurityConfig` / `AccessRules` / `JwtConfig` |
| UsuarioAutenticado(contaId, email, nome, papeis) | `AuthenticatedUser(accountId, email, name, roles)` |
| @UsuarioLogado / UsuarioLogadoResolver | `@CurrentUser` / `CurrentUserArgumentResolver` |
| propriedades `cz.seguranca.*` | `SecurityProperties`, `cz.security.*` |
| ReprocessadorDeEventos, `cz.eventos.reprocessamento.*` | `IncompleteEventResubmitter`, `cz.events.resubmission.*` |
| problema `type` `https://capitulozero.dev/problemas/<codigo>` | `https://capitulozero.dev/problems/<code>` |
| `erros[{campo, mensagem}]` | `errors[{field, message}]` |
| eventoId / ocorridoEm | `eventId` / `occurredAt` |

Problem codes: `unauthenticated`, `access-denied`, `invalid-request`, `resource-not-found`,
`method-not-supported`, `internal-error`.

Environment variables: `CZ_JWT_SECRET`, `CZ_CORS_ORIGINS`, `CZ_API_DOCS_ENABLED`, `CZ_PSP_WEBHOOK_SECRET`,
`CZ_ADMIN_EMAIL`, `CZ_ADMIN_PASSWORD`.

## Test harness

| Portuguese | English |
|---|---|
| AplicacaoIT | `ApplicationIT` |
| ModularidadeTest | `ModularityTest` |
| ArquiteturaTest | `ArchitectureTest` |
| RelogioDeTeste | `MutableTestClock` |
| TokensDeTeste | `TestTokens` |
| JornadaDoPedidoIT | `OrderJourneyIT` |
