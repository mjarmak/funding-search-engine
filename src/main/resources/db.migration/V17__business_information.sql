ALTER TABLE user_data ADD COLUMN business_id bigint;
ALTER TABLE user_data ADD COLUMN stripe_id varchar(50);

CREATE TABLE payment (
    id bigserial PRIMARY KEY,
    invoice_id varchar(16),
    stripe_payment_id varchar(255),
    amount numeric(12,2),
    currency varchar(16),
    user_id bigint,
    business_id bigint,
    created_at timestamp DEFAULT now()
);

CREATE TABLE business (
    id bigserial PRIMARY KEY,
    name varchar(255),
    vat_number varchar(63),
    phone_number varchar(63),
    email varchar(255),
    address_id bigint,
    created_at timestamp DEFAULT now(),
    updated_at timestamp DEFAULT now()
);
