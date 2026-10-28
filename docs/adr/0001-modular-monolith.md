# ADR 0001: Modular monolith with Spring Modulith

- Status: accepted
- Date: 2026-10-27

## Context

Capítulo Zero is a portfolio project built by one person, with eight domain areas (identity, catalog, inventory,
cart, shipping, payments, orders and notifications). We want to show clear domain boundaries, event-driven
communication and a saga, without the operational cost of several services: deployment, networking, distributed
consistency and observability of eight processes.

## Decision

A single Spring Boot application organized as a modular monolith with Spring Modulith:

- each module is a direct subpackage of `br.com.capitulozero`, with its public API in the root package and
  everything else internal;
- allowed dependencies are declared in `@ApplicationModule(allowedDependencies = ...)` and verified by a test;
- each module has its own PostgreSQL schema, without foreign keys between schemas;
- modules talk through a facade (`XxxApi`) or through domain events.

## Consequences

- A database transaction can span several modules during checkout, which greatly simplifies consistency.
- Extracting a module into a service in the future is feasible, because the code and data boundaries already exist.
  `notifications`, which only consumes Kafka, is the natural candidate.
- The discipline depends on the modularity and ArchUnit tests: they are a mandatory part of the build.
