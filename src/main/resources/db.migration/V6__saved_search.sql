CREATE TABLE saved_search (
    id bigserial PRIMARY KEY,
    user_id bigint NOT NULL,
    name varchar(255) NOT NULL,
    value varchar(255) NOT NULL,
    notification smallint NOT NULL,
    updated_at timestamp DEFAULT now(),
    created_at timestamp DEFAULT now(),
    FOREIGN KEY (user_id) REFERENCES user_data(id)
);
