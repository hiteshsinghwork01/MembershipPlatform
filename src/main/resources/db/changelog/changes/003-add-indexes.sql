--liquibase formatted sql

--changeset membership-platform:003-add-missing-indexes
CREATE INDEX idx_tec_tier_id ON tier_eligibility_criteria (membership_tier_id);
