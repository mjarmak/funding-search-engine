CREATE TABLE user_project_join (
                                id bigserial PRIMARY KEY,
                                user_id bigint NOT NULL,
                                project_id bigint NOT NULL,
                                type smallint NOT NULL,
                                created_at timestamp DEFAULT now()
);

CREATE TABLE user_organisation_join (
                                id bigserial PRIMARY KEY,
                                user_id bigint NOT NULL,
                                organisation_id bigint NOT NULL,
                                type smallint NOT NULL,
                                created_at timestamp DEFAULT now()
);
