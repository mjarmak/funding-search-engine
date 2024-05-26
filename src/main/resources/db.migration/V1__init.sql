CREATE TABLE calls (
    id bigserial PRIMARY KEY,

    identifier varchar(64) NOT NULL,
    title varchar(255) NOT NULL,
    description varchar(25000),
    action_type smallint,
    submission_deadline_date timestamp DEFAULT now(),
    submission_deadline2_date timestamp DEFAULT now(),
    open_date timestamp DEFAULT now(),
    budget varchar(255),

    project_number smallint,


    created_at timestamp DEFAULT now(),
    updated_at timestamp DEFAULT now(),
    version integer DEFAULT 0
);
