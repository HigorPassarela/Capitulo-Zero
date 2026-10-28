# Implementation roadmap

The whole backend is described in 10 OpenSpec changes, in `openspec/changes/`. They must be implemented
**in this order**: each one depends on the previous ones and was written assuming they are already archived.

| # | Change | Delivers | Capabilities (specs) |
|---|---|---|---|
| 01 | `01-platform-foundation` | Maven project, docker compose, `shared` module, RFC 9457 errors, base security, outbox + Kafka, test harness, CI | `platform/api`, `platform/events` |
| 02 | `02-identity-and-access` | Sign-up, JWT login, refresh token with rotation, login rate limit, profile and addresses, initial admin | `identity/authentication`, `identity/accounts` |
| 03 | `03-book-catalog` | Books, authors, categories, catalog administration, typo-tolerant full-text search, cache | `catalog/books`, `catalog/search` |
| 04 | `04-inventory-and-reservations` | Stock per book, movements, reservations with expiration, safe concurrency, low stock alert | `inventory/stock`, `inventory/reservations` |
| 05 | `05-cart-and-coupons` | Cart in Redis, merge with the anonymous cart, discount coupons | `cart/items`, `cart/coupons` |
| 06 | `06-shipping-rates-and-shipments` | Shipping quote by zip code range and weight, shipments, tracking, logistics simulator | `shipping/rates`, `shipping/shipments` |
| 07 | `07-simulated-pix-payments` | Simulated Pix charge, webhook with HMAC and idempotency, PSP simulator, expiration, refund | `payments/pix-charges`, `payments/psp-webhook`, `payments/psp-simulator` |
| 08 | `08-orders-and-checkout` | Idempotent checkout, order saga, cancellations and compensations, queries | `orders/checkout`, `orders/lifecycle`, `orders/queries` |
| 09 | `09-email-notifications` | Kafka consumers, transactional e-mails through Mailpit, retries and DLT | `notifications/transactional-emails` |
| 10 | `10-observability-and-demo` | OpenTelemetry + Grafana, business metrics, demo data, E2E journey, final README | `platform/observability`, `platform/demo-data` |

## How to run it with Claude Code

Open Claude Code at the root of this repository and run:

```
/cz:proxima
```

It picks the first change that is not archived, implements every task, runs the full verification and archives
the change. Then it stops and shows a summary with a suggested commit message. Branches and commits are made by
the developer.

To go straight to the next change without stopping, use `/cz:proxima todas`.
Even in that mode, run `/clear` between changes if the session gets long, because each change is big.

To see what is left: `openspec list`. To see what has been merged: `openspec list --specs`.
