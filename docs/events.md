# Event catalog

Contract of the domain events. Every event is a `record` in the root package of the module that publishes it and
starts with `eventId` (UUID) and `occurredAt` (Instant UTC). Money values are decimals with 2 places (BRL).

- **Internal:** consumed only inside the application, through `@ApplicationModuleListener`.
- **Kafka:** besides the internal consumption, it is externalized by the outbox to the given topic, serialized as
  JSON, with the given key as the message key.

Changed an event? Update this file in the same commit. An incompatible change to an externalized event requires
a new topic with a version suffix (for example `...order-created.v2`).

## identity

| Event | Type | Topic / key | Fields |
|---|---|---|---|
| `AccountCreated` | Kafka | `capitulozero.identity.account-created` / `accountId` | `accountId`, `name`, `email` |

## catalog

| Event | Type | Fields |
|---|---|---|
| `BookRegistered` | Internal | `bookId`, `isbn`, `title` |
| `PriceChanged` | Internal | `bookId`, `previousPrice`, `newPrice` |
| `BookDeactivated` | Internal | `bookId` |

## inventory

| Event | Type | Topic / key | Fields |
|---|---|---|---|
| `ReservationExpired` | Internal | n/a | `reference` (order id), `items[{bookId, quantity}]` |
| `LowStock` | Kafka | `capitulozero.inventory.low-stock` / `bookId` | `bookId`, `isbn`, `title`, `available`, `threshold` |

## shipping

| Event | Type | Fields |
|---|---|---|
| `ShipmentShipped` | Internal | `shipmentId`, `orderId`, `trackingCode`, `shippedAt` |
| `ShipmentDelivered` | Internal | `shipmentId`, `orderId`, `deliveredAt` |

## payments

| Event | Type | Fields |
|---|---|---|
| `PaymentApproved` | Internal | `chargeId`, `orderId`, `amount`, `approvedAt` |
| `PaymentDeclined` | Internal | `chargeId`, `orderId`, `reason` |
| `PaymentExpired` | Internal | `chargeId`, `orderId` |
| `PaymentRefunded` | Internal | `chargeId`, `orderId`, `amount` |

## orders

All of them use the key `orderId`, which keeps the events of the same order in order inside the partition.

| Event | Topic | Fields |
|---|---|---|
| `OrderCreated` | `capitulozero.orders.order-created` | `orderId`, `number`, `accountId`, `customerName`, `customerEmail`, `items[{title, quantity, unitPrice}]`, `subtotal`, `discount`, `shippingCost`, `total`, `pix{copyPasteCode, expiresAt}` |
| `OrderPaid` | `capitulozero.orders.order-paid` | `orderId`, `number`, `customerName`, `customerEmail`, `total`, `deliveryDays` |
| `OrderShipped` | `capitulozero.orders.order-shipped` | `orderId`, `number`, `customerName`, `customerEmail`, `trackingCode` |
| `OrderDelivered` | `capitulozero.orders.order-delivered` | `orderId`, `number`, `customerName`, `customerEmail` |
| `OrderCancelled` | `capitulozero.orders.order-cancelled` | `orderId`, `number`, `customerName`, `customerEmail`, `reason`, `refunded` |

The `reason` of `OrderCancelled` is one of: `CANCELLED_BY_CUSTOMER`, `PAYMENT_EXPIRED`, `PAYMENT_DECLINED`,
`RESERVATION_EXPIRED`, `CANCELLED_BY_ADMIN`.

## Kafka consumers

| Group | Topics | Module |
|---|---|---|
| `notifications` | all the topics above | notifications |

Each consumed topic gets retry topics (`-retry`, with an index suffix when there is a growing backoff)
and a DLT (`-dlt`), created by `@RetryableTopic`.
