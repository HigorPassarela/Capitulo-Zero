# ADR 0004: Authentication with JWT issued by the application itself

- Status: accepted
- Date: 2026-10-27

## Context

The store needs sign-up, login and roles (customer and administrator). An external identity provider
(Keycloak, Auth0) would add a heavy container or an external dependency to the project.

## Decision

- The `identity` module issues a JWT access token (HS256, 15 minutes) with `sub` = account id, `email`, `name`
  and `roles`.
- The refresh token is opaque and random, stored as a hash in PostgreSQL, rotated on every use, and the whole
  family is revoked if a token is reused.
- Validation uses the Spring Security resource server, configured in `shared`.
- Login attempts are limited per e-mail and IP using Redis.

## Consequences

- No extra service to run; the whole security flow is visible and testable in the code.
- Switching to an OIDC provider in the future only affects issuance. Validation stays JWT in the resource server.
