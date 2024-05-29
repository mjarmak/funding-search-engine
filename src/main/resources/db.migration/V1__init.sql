CREATE TABLE calls (
    id bigserial PRIMARY KEY,

    identifier varchar(64) NOT NULL UNIQUE,
    title varchar(255) NOT NULL,
    description varchar(25000),
    description_display varchar(255),
    action_type smallint,
    submission_deadline_date date DEFAULT now(),
    submission_deadline2_date date DEFAULT now(),
    open_date date DEFAULT now(),
    budget varchar(255),

    project_number smallint,

    created_at timestamp DEFAULT now(),
    updated_at timestamp DEFAULT now(),
    version integer DEFAULT 0
);

CREATE INDEX calls_identifier_idx ON calls(identifier);
