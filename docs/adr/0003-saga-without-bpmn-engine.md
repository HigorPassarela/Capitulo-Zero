# ADR 0003: Checkout saga without a BPMN engine

- Status: accepted
- Date: 2026-10-27

## Context

Checkout involves stock reservation, charging, waiting for an asynchronous confirmation, expiration and
compensation (releasing stock, refunding). We evaluated using a BPMN engine (Camunda).

## Decision

The saga is orchestrated by the `orders` module with a state machine in the `Order` aggregate, event listeners
and scheduled jobs for expiration. We do not use a BPMN engine.

## Reasons

- The flow has few steps, no human tasks and no need for visual modeling by business people.
- Camunda 7 Community Edition reached end of life in October 2025, with no new fixes.
- Camunda 8 in self-managed mode requires a license for production use and heavy infrastructure
  (Zeebe and search components) for a flow of this size.
- An explicit state machine in code is easier to test and shows the reasoning better in a portfolio.

## Consequences

- The valid transitions live in the domain (`Order`) and are covered by unit tests.
- If long-running flows with human tasks appear in the future, an open source fork of Camunda 7 (such as Operaton)
  can be reconsidered.
