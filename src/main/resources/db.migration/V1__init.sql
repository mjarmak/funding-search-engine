CREATE TABLE calls (
    id bigserial PRIMARY KEY,

    identifier varchar(64) NOT NULL UNIQUE,
    title varchar(255) NOT NULL,

    action_type varchar(255),

    description varchar(25000),
    description_display varchar(255),

    mission_details varchar(25000),
    destination_details varchar(25000),

    open_date timestamp,
    submission_deadline_date timestamp,
    submission_deadline_date_2 timestamp,

    budget_min numeric(12,2),
    budget_max numeric(12,2),

    submissionProcedure smallint,

    type_of_mga varchar(255),
    type_of_mga_description varchar(255),

    project_number smallint,

    path_id varchar(50),
    reference varchar(150),

    created_at timestamp DEFAULT now(),
    updated_at timestamp DEFAULT now(),
    version integer DEFAULT 0
);

CREATE INDEX calls_identifier_idx ON calls(identifier);
