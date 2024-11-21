ALTER TABLE calls ADD COLUMN secret smallint DEFAULT 0;

ALTER TABLE calls ALTER COLUMN reference DROP NOT NULL;

ALTER TABLE subscription ADD COLUMN domain varchar(50);
