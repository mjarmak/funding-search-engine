ALTER TABLE projects ADD COLUMN framework_program smallint;
ALTER TABLE projects ALTER COLUMN master_call_identifier DROP NOT NULL;
