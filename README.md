# Membership Platform

Backend service for a subscription-based membership program with tiered benefits, configurable eligibility criteria, and automated renewal.

## Tech Stack

- Java 17, Spring Boot 3.2
- Spring Data JPA, H2 (in-memory), Liquibase
- Lombok, Jakarta Validation

## Running the App

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home \
  mvn spring-boot:run
```

App starts on `http://localhost:8080`.  
H2 console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:membershipdb`, no password).

## Seed Data

On startup, Liquibase seeds:

| Plans | Price |
|-------|-------|
| Monthly | ₹99 / month |
| Quarterly | ₹249 / 3 months |
| Yearly | ₹799 / year |

| Tier | Level | Eligibility |
|------|-------|-------------|
| Silver | 1 | ≥ 1 order (all time) |
| Gold | 2 | ≥ 10 orders OR ≥ ₹5,000 order value (monthly) |
| Platinum | 3 | ≥ ₹15,000 order value OR VIP cohort (monthly) |

Seed users: IDs 1 (Rahul, REGULAR), 2 (Priya, VIP), 3 (Amit, NEW).

---

## API Reference

All responses follow:
```json
{ "success": true, "data": { ... } }
{ "success": false, "message": "..." }
```

---

### Users

#### Register
```
POST /api/v1/users/register
```
```json
{
  "name": "Rahul Sharma",
  "email": "rahul@example.com",
  "phoneNumber": "9876543210",
  "cohort": "REGULAR"
}
```
`cohort` values: `NEW`, `REGULAR`, `VIP`, `PREMIUM`

#### Get User
```
GET /api/v1/users/{userId}
```

#### Deregister
```
DELETE /api/v1/users/{userId}/deregister
```

---

### Membership Plans

#### List Plans
```
GET /api/v1/plans
```

#### Create Plan
```
POST /api/v1/plans
```
```json
{
  "name": "Monthly",
  "description": "1-month membership",
  "price": 99.00,
  "durationType": "MONTHLY"
}
```
`durationType` values: `MONTHLY`, `QUARTERLY`, `YEARLY`

#### Update / Deactivate
```
PUT    /api/v1/plans/{planId}
DELETE /api/v1/plans/{planId}
```

---

### Membership Tiers

#### List Tiers
```
GET /api/v1/tiers
```

#### Create Tier
```
POST /api/v1/tiers
```
```json
{
  "name": "Gold",
  "description": "Mid-tier membership",
  "tierLevel": 2,
  "criteriaLogic": "OR"
}
```

#### Manage Benefits on a Tier
```
POST   /api/v1/tiers/{tierId}/benefits
PUT    /api/v1/tiers/{tierId}/benefits/{configId}
GET    /api/v1/tiers/{tierId}/benefits
DELETE /api/v1/tiers/{tierId}/benefits/{configId}
```
```json
{
  "benefitId": 2,
  "discountPercentage": 10.0
}
```

#### Manage Eligibility Criteria on a Tier
```
POST   /api/v1/tiers/{tierId}/criteria
PUT    /api/v1/tiers/{tierId}/criteria/{criteriaId}
GET    /api/v1/tiers/{tierId}/criteria
DELETE /api/v1/tiers/{tierId}/criteria/{criteriaId}
```
```json
{
  "criteriaType": "MIN_ORDER_COUNT",
  "operator": "GTE",
  "criteriaValue": "10",
  "evaluationWindow": "MONTHLY"
}
```
`criteriaType`: `MIN_ORDER_COUNT`, `MIN_ORDER_VALUE`, `USER_COHORT`, `TENURE_DAYS`  
`operator`: `GTE`, `LTE`, `EQ`  
`evaluationWindow`: `MONTHLY`, `ALL_TIME`

---

### Memberships

#### Subscribe
```
POST /api/v1/memberships/subscribe
```
```json
{
  "userId": 1,
  "planId": 1,
  "tierId": 1
}
```
> The eligibility engine automatically assigns the highest tier the user qualifies for, overriding the requested tier if a better match exists.

#### Get Active Membership
```
GET /api/v1/memberships/user/{userId}
```

#### Upgrade Tier
```
PUT /api/v1/memberships/{membershipId}/upgrade
```

#### Downgrade Tier
```
PUT /api/v1/memberships/{membershipId}/downgrade
```

#### Cancel
```
PUT /api/v1/memberships/{membershipId}/unsubscribe
```
```json
{ "reason": "No longer needed" }
```

#### Change History
```
GET /api/v1/memberships/{membershipId}/history
```

---

### Benefits

#### List / Create / Update / Deactivate
```
GET    /api/v1/benefits
POST   /api/v1/benefits
PUT    /api/v1/benefits/{benefitId}
DELETE /api/v1/benefits/{benefitId}
```
```json
{
  "name": "Free Delivery",
  "description": "Free delivery on all eligible orders",
  "benefitType": "FREE_DELIVERY"
}
```
`benefitType`: `FREE_DELIVERY`, `CATEGORY_DISCOUNT`, `EXCLUSIVE_DEAL`, `PRIORITY_SUPPORT`, `EARLY_ACCESS`

---

## Design Highlights

**Eligibility Engine** — `TierEligibilityEngine` evaluates configurable criteria per tier using a strategy pattern (`CriteriaEvaluator` interface + `CriteriaEvaluatorRegistry`). New criteria types are added by implementing one interface — no engine changes needed.

**Configurable Benefits** — `TierBenefitConfig` is a join entity between tiers and benefits, carrying per-tier metadata (discount %, validity window). Benefits and their tier assignments are fully runtime-configurable via API.

**Audit Trail** — Every subscribe, upgrade, downgrade, cancel, and auto-renewal is recorded in `MembershipChangeHistory`.

**Auto-renewal** — A scheduled job runs daily at 02:00, processing memberships expiring within 24 hours in batches of 500.
