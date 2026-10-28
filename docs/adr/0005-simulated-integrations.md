# ADR 0005: Simulated payment and shipping

- Status: accepted
- Date: 2026-10-27

## Context

The project is public on GitHub and must run with just `docker compose up`, without accounts at payment gateways
or carriers and without API keys.

## Decision

- **Payments:** the module generates a simulated Pix charge (txid, copy-and-paste code, expiration). The
  confirmation arrives through an HTTP webhook signed with HMAC-SHA256, exactly as it would from a real PSP.
  An internal PSP simulator, enabled only with `cz.payments.simulator.enabled=true`, builds and signs that webhook
  and calls the application's own endpoint.
- **Shipping:** the shipping cost is calculated from a configurable table of zip code ranges and weight.
  Shipments get a fictitious tracking code and move forward in status by an administrator action or by an
  optional logistics simulator.

## Consequences

- The production path (webhook, signature, idempotency, expiration, refund) is real and tested. Only the origin of
  the event is simulated.
- Switching to a real PSP requires a new adapter that creates the charge and an adjustment of the webhook format,
  without changing `orders`.
