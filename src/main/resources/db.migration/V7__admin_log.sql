CREATE TABLE admin_log_book (
                          id bigserial PRIMARY KEY,
                          type smallint NOT NULL,
                          log_text varchar(255) NOT NULL,
                          created_at timestamp DEFAULT now()
);
