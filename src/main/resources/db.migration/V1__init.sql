CREATE TABLE calls (
    id bigserial PRIMARY KEY,

    identifier varchar(64) NOT NULL UNIQUE,
    title varchar(255) NOT NULL,

    action_type varchar(255),

    description varchar(25000),
    description_display varchar(255),

    mission_details varchar(25000),
    destination_details varchar(25000),

    start_date timestamp,
    end_date timestamp,
    end_date_2 timestamp,

    budget_min numeric(12,2),
    budget_max numeric(12,2),

    submissionProcedure smallint,

    type_of_mga_description varchar(255),

    project_number smallint,

    url_type smallint,
    url_id varchar(150),

    created_at timestamp DEFAULT now(),
    updated_at timestamp DEFAULT now(),
    version integer DEFAULT 0
);

CREATE INDEX calls_identifier_idx ON calls(identifier);

CREATE TABLE long_text (
    id bigserial PRIMARY KEY,
    call_id bigint NOT NULL,
    type smallint NOT NULL,
    text varchar(25000) NOT NULL,
    created_at timestamp DEFAULT now(),
    updated_at timestamp DEFAULT now()
);

CREATE TABLE subscription (
    id bigserial PRIMARY KEY,
    admin_user_id bigint NOT NULL UNIQUE,
    type smallint NOT NULL,
    created_at timestamp DEFAULT now(),
    updated_at timestamp DEFAULT now()
);

CREATE TABLE payment (
    id bigserial PRIMARY KEY,
    subscription_id bigint NOT NULL,
    amount numeric(12,2) NOT NULL,
    startDate timestamp NOT NULL,
    endDate timestamp NOT NULL,
    currency varchar(3) NOT NULL,
    status smallint NOT NULL,
    created_at timestamp DEFAULT now(),
    updated_at timestamp DEFAULT now()
);

CREATE TABLE user_data (
    id bigserial PRIMARY KEY,
    subject_id varchar(36) NOT NULL UNIQUE,
    email varchar(255) NOT NULL UNIQUE,
    first_name varchar(50) NOT NULL,
    last_name varchar(50) NOT NULL,
    user_name varchar(50) NOT NULL,
    created_at timestamp DEFAULT now(),
    updated_at timestamp DEFAULT now()
);

CREATE INDEX user_data_subject_id_idx ON user_data(subject_id);

CREATE TABLE user_call_join (
    id bigserial PRIMARY KEY,
    user_id bigint NOT NULL,
    call_id bigint NOT NULL,
    type smallint NOT NULL,
    created_at timestamp DEFAULT now()
);

CREATE TABLE user_subscription_join (
    id bigserial PRIMARY KEY,
    user_id bigint NOT NULL,
    subscription_id bigint NOT NULL,
    type smallint NOT NULL,
);

CREATE TABLE log_book (
    id bigserial PRIMARY KEY,
    user_id bigint NOT NULL,
    type smallint NOT NULL,
    created_at timestamp DEFAULT now()
);
CREATE INDEX log_book_user_id_idx ON log_book(user_id);
CREATE INDEX log_book_action_type_idx ON log_book(action_type);
