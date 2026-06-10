--liquibase formatted sql

--changeset membership-platform:001-membership-plan
CREATE TABLE membership_plan (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100)   NOT NULL,
    description     VARCHAR(255),
    price           DECIMAL(10, 2) NOT NULL,
    duration_type   VARCHAR(20)    NOT NULL,
    is_active       BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP      NOT NULL,
    updated_at      TIMESTAMP,
    CONSTRAINT uq_plan_name     UNIQUE (name),
    CONSTRAINT ck_plan_price    CHECK  (price > 0)
);
CREATE INDEX idx_plan_active_price ON membership_plan (is_active, price);

--changeset membership-platform:001-membership-tier
CREATE TABLE membership_tier (
    id              BIGINT       AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    description     VARCHAR(255),
    tier_level      INT          NOT NULL,
    criteria_logic  VARCHAR(10)  NOT NULL DEFAULT 'OR',
    is_active       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP    NOT NULL,
    updated_at      TIMESTAMP,
    CONSTRAINT uq_tier_name    UNIQUE (name),
    CONSTRAINT uq_tier_level   UNIQUE (tier_level),
    CONSTRAINT ck_tier_level   CHECK  (tier_level > 0)
);
CREATE INDEX idx_mt_active_level ON membership_tier (is_active, tier_level);

--changeset membership-platform:001-tier-eligibility-criteria
CREATE TABLE tier_eligibility_criteria (
    id                 BIGINT       AUTO_INCREMENT PRIMARY KEY,
    membership_tier_id BIGINT       NOT NULL,
    criteria_type      VARCHAR(30)  NOT NULL,
    operator           VARCHAR(10)  NOT NULL,
    criteria_value     VARCHAR(100) NOT NULL,
    evaluation_window  VARCHAR(20)  NOT NULL,
    is_active          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at         TIMESTAMP    NOT NULL,
    updated_at         TIMESTAMP,
    CONSTRAINT fk_criteria_tier      FOREIGN KEY (membership_tier_id) REFERENCES membership_tier (id),
    CONSTRAINT uq_criteria_tier_type UNIQUE (membership_tier_id, criteria_type, evaluation_window, criteria_value)
);

--changeset membership-platform:001-benefit
CREATE TABLE benefit (
    id           BIGINT       AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    description  VARCHAR(255),
    benefit_type VARCHAR(30)  NOT NULL,
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP    NOT NULL,
    updated_at   TIMESTAMP,
    CONSTRAINT uq_benefit_name    UNIQUE (name)
);
CREATE INDEX idx_benefit_active_type ON benefit (is_active, benefit_type);

--changeset membership-platform:001-tier-benefit-config
CREATE TABLE tier_benefit_config (
    id                  BIGINT         AUTO_INCREMENT PRIMARY KEY,
    membership_tier_id  BIGINT         NOT NULL,
    benefit_id          BIGINT         NOT NULL,
    discount_percentage DECIMAL(5, 2),
    metadata            TEXT,
    valid_from          TIMESTAMP,
    valid_until         TIMESTAMP,
    is_applicable       BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP      NOT NULL,
    updated_at          TIMESTAMP,
    CONSTRAINT fk_tbc_tier         FOREIGN KEY (membership_tier_id) REFERENCES membership_tier (id),
    CONSTRAINT fk_tbc_benefit      FOREIGN KEY (benefit_id)          REFERENCES benefit (id),
    CONSTRAINT uq_tbc_tier_benefit UNIQUE (membership_tier_id, benefit_id),
    CONSTRAINT ck_tbc_discount     CHECK  (discount_percentage IS NULL OR (discount_percentage >= 0 AND discount_percentage <= 100)),
    CONSTRAINT ck_tbc_valid_window CHECK  (valid_from IS NULL OR valid_until IS NULL OR valid_from < valid_until)
);

--changeset membership-platform:001-users
CREATE TABLE users (
    id           BIGINT       AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    email        VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15),
    cohort       VARCHAR(25),
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP    NOT NULL,
    updated_at   TIMESTAMP,
    CONSTRAINT uq_user_email   UNIQUE (email),
    CONSTRAINT uq_user_phone   UNIQUE (phone_number)
);

--changeset membership-platform:001-user-membership
CREATE TABLE user_membership (
    id                  BIGINT       AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT       NOT NULL,
    membership_plan_id  BIGINT       NOT NULL,
    membership_tier_id  BIGINT       NOT NULL,
    status              VARCHAR(25)  NOT NULL,
    start_date          TIMESTAMP    NOT NULL,
    expiry_date         TIMESTAMP    NOT NULL,
    auto_renew          BOOLEAN      NOT NULL DEFAULT TRUE,
    last_renewed_at     TIMESTAMP,
    cancelled_at        TIMESTAMP,
    cancellation_reason VARCHAR(255),
    payment_reference   VARCHAR(100),
    version             INT          NOT NULL DEFAULT 0,
    created_at          TIMESTAMP    NOT NULL,
    updated_at          TIMESTAMP,
    CONSTRAINT fk_um_user    FOREIGN KEY (user_id)            REFERENCES users (id),
    CONSTRAINT fk_um_plan    FOREIGN KEY (membership_plan_id) REFERENCES membership_plan (id),
    CONSTRAINT fk_um_tier    FOREIGN KEY (membership_tier_id) REFERENCES membership_tier (id),
    CONSTRAINT ck_um_dates   CHECK  (expiry_date > start_date),
    CONSTRAINT ck_um_version CHECK  (version >= 0)
);
CREATE INDEX idx_um_plan ON user_membership (membership_plan_id);
CREATE INDEX idx_um_tier ON user_membership (membership_tier_id);
CREATE INDEX idx_um_user_status ON user_membership (user_id, status);
CREATE INDEX idx_um_expiry ON user_membership (expiry_date);
CREATE INDEX idx_um_status_expiry ON user_membership (status, expiry_date);

--changeset membership-platform:001-membership-change-history
CREATE TABLE membership_change_history (
    id                 BIGINT       AUTO_INCREMENT PRIMARY KEY,
    user_membership_id BIGINT       NOT NULL,
    change_type        VARCHAR(20)  NOT NULL,
    from_tier_id       BIGINT,
    to_tier_id         BIGINT,
    reason             VARCHAR(255),
    changed_at         TIMESTAMP    NOT NULL,
    CONSTRAINT fk_mch_membership FOREIGN KEY (user_membership_id) REFERENCES user_membership (id),
    CONSTRAINT fk_mch_from_tier  FOREIGN KEY (from_tier_id)       REFERENCES membership_tier (id),
    CONSTRAINT fk_mch_to_tier    FOREIGN KEY (to_tier_id)         REFERENCES membership_tier (id)
);
CREATE INDEX idx_mch_membership_changed ON membership_change_history (user_membership_id, changed_at);

