ALTER TABLE subscription ADD COLUMN trial_end_date timestamp;
ALTER TABLE subscription ADD COLUMN end_date timestamp;
ALTER TABLE subscription ADD COLUMN checkout_session_id varchar(255);
ALTER TABLE subscription ADD COLUMN stripe_id varchar(50);
ALTER TABLE subscription ADD COLUMN status smallint NOT NULL DEFAULT 0;
ALTER TABLE subscription ADD COLUMN next_type smallint;

DROP TABLE payment;
