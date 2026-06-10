 --liquibase formatted sql

--changeset membership-platform:002-seed-plans
INSERT INTO membership_plan (id, name, description, price, duration_type, is_active, created_at) VALUES
(1, 'Monthly',   '1-month membership',              99.00,  'MONTHLY', TRUE, NOW()),
(2, 'Quarterly', '3-month membership — save 16%',   249.00, 'QUARTERLY', TRUE, NOW()),
(3, 'Yearly',    '12-month membership — save 33%',  799.00, 'YEARLY', TRUE, NOW());

--changeset membership-platform:002-seed-tiers
INSERT INTO membership_tier (id, name, description, tier_level, criteria_logic, is_active, created_at) VALUES
(1, 'Silver',   'Entry-level membership tier',              1, 'OR', TRUE, NOW()),
(2, 'Gold',     'Mid-tier membership with enhanced benefits', 2, 'OR', TRUE, NOW()),
(3, 'Platinum', 'Top-tier membership with premium benefits',  3, 'OR', TRUE, NOW());

--changeset membership-platform:002-seed-benefits
INSERT INTO benefit (id, name, description, benefit_type, is_active, created_at) VALUES
(1, 'Free Delivery',          'Free delivery on all eligible orders',          'FREE_DELIVERY',    TRUE, NOW()),
(2, '10% Category Discount',  'Extra 10% off on selected categories',          'CATEGORY_DISCOUNT',TRUE, NOW()),
(3, '15% Category Discount',  'Extra 15% off on selected categories',          'CATEGORY_DISCOUNT',TRUE, NOW()),
(4, 'Exclusive Deals',        'Access to member-only deals and flash sales',   'EXCLUSIVE_DEAL',   TRUE, NOW()),
(5, 'Priority Support',       'Dedicated support with faster response times',  'PRIORITY_SUPPORT', TRUE, NOW()),
(6, 'Early Access',           'Early access to sales and new product launches','EARLY_ACCESS',     TRUE, NOW());

--changeset membership-platform:002-seed-tier-criteria
INSERT INTO tier_eligibility_criteria (membership_tier_id, criteria_type, operator, criteria_value, evaluation_window, is_active, created_at) VALUES
(1, 'MIN_ORDER_COUNT', 'GTE', '0',         'ALL_TIME', TRUE, NOW()),
(2, 'MIN_ORDER_COUNT', 'GTE', '10',        'MONTHLY',  TRUE, NOW()),
(2, 'MIN_ORDER_VALUE', 'GTE', '5000.00',   'MONTHLY',  TRUE, NOW()),
(3, 'MIN_ORDER_VALUE', 'GTE', '15000.00',  'MONTHLY',  TRUE, NOW()),
(3, 'USER_COHORT',     'EQ',  'VIP',        'ALL_TIME', TRUE, NOW()),
(3, 'USER_COHORT',     'EQ',  'PREMIUM',   'ALL_TIME', TRUE, NOW());

--changeset membership-platform:002-seed-tier-benefit-configs
INSERT INTO tier_benefit_config (membership_tier_id, benefit_id, discount_percentage, is_applicable, created_at) VALUES
(1, 1, NULL,  TRUE, NOW()),
(2, 1, NULL,  TRUE, NOW()),
(2, 2, 10.00, TRUE, NOW()),
(2, 6, NULL,  TRUE, NOW()),
(3, 1, NULL,  TRUE, NOW()),
(3, 3, 15.00, TRUE, NOW()),
(3, 4, NULL,  TRUE, NOW()),
(3, 5, NULL,  TRUE, NOW()),
(3, 6, NULL,  TRUE, NOW());

--changeset membership-platform:002-seed-users
INSERT INTO users (id, name, email, phone_number, cohort, is_active, created_at) VALUES
(1, 'Rahul Sharma', 'rahul@example.com', '9876543210', 'REGULAR', TRUE, NOW()),
(2, 'Priya Patel',  'priya@example.com', '9876543211', 'VIP',     TRUE, NOW()),
(3, 'Amit Kumar',   'amit@example.com',  '9876543212', 'NEW',     TRUE, NOW());

--changeset membership-platform:002-reset-sequences
-- Advance H2 identity sequences past the explicitly-seeded IDs so
-- auto-generated inserts never collide with seed data.
ALTER TABLE users                  ALTER COLUMN id RESTART WITH 100;
ALTER TABLE membership_plan        ALTER COLUMN id RESTART WITH 100;
ALTER TABLE membership_tier        ALTER COLUMN id RESTART WITH 100;
ALTER TABLE benefit                ALTER COLUMN id RESTART WITH 100;
ALTER TABLE tier_eligibility_criteria ALTER COLUMN id RESTART WITH 100;
ALTER TABLE tier_benefit_config    ALTER COLUMN id RESTART WITH 100;
ALTER TABLE user_membership        ALTER COLUMN id RESTART WITH 100;
ALTER TABLE membership_change_history ALTER COLUMN id RESTART WITH 100;
