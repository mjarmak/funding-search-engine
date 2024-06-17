SELECT
    *
FROM
    pg_stat_activity
WHERE
        pid <> pg_backend_pid()
  AND datname = 'funding_prod'
;




SELECT length(text) AS text_size
FROM long_text
ORDER BY length(text) DESC
    LIMIT 1;


UPDATE schema_version SET checksum = '-1632940647' WHERE version = 1;
