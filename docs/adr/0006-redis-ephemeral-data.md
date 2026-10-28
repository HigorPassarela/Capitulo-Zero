# ADR 0006: Redis only for ephemeral data

- Status: accepted
- Date: 2026-10-27

## Context

Redis is in the stack. It is tempting to store data that looks "fast" in it, but losing Redis must not corrupt
the business state.

## Decision

Redis stores only:

- the catalog read cache (book details, category list);
- the shopping cart (expiring after 30 days without use);
- checkout idempotency responses (24 hours);
- login rate limit counters;
- ShedLock locks for scheduled jobs.

Orders, reservations, charges, coupons and coupon usages live in PostgreSQL.

## Consequences

- A `FLUSHALL` on Redis makes the customer lose the cart and empties the cache, but no order, payment or stock
  level is affected.
- Every new use of Redis must fit one of the categories above or get its own ADR.
