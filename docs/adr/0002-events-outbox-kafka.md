# ADR 0002: Events with outbox and externalization to Kafka

- Status: accepted
- Date: 2026-10-27

## Context

Modules need to react to facts from other modules (payment approved, reservation expired, order created) without
direct coupling and without losing events if the application goes down between the commit and the processing.
We also want a consumer outside the transactional flow (e-mails) that could become a separate service later.

## Decision

- Domain events are published with `ApplicationEventPublisher` and recorded by the Spring Modulith Event
  Publication Registry (a PostgreSQL table), which works as a transactional outbox.
- Consumption between modules with `@ApplicationModuleListener`: asynchronous, after the commit, in its own
  transaction. Incomplete events are republished on restart.
- Events of external interest are marked with `@Externalized` and published to Kafka (KRaft mode, no ZooKeeper).
- The `notifications` module consumes only from Kafka, with its own records, without importing types from other
  modules.

## Alternatives considered

- **Publishing straight to Kafka inside the transaction:** loses events if the broker fails after the commit, or
  publishes events from transactions that rolled back.
- **In-memory events only:** simple, but loses events on a crash and does not demonstrate messaging.

## Consequences

- At-least-once delivery: every consumer must be idempotent.
- The topic contract is documented in `docs/events.md` and versioned by topic name.
