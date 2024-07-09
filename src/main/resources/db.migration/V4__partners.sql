ALTER TABLE long_text
    ALTER COLUMN call_id DROP NOT NULL;
ALTER TABLE long_text
    ADD COLUMN project_id bigint;

CREATE TABLE address
(
    id        bigserial PRIMARY KEY,
    street    varchar(255),
    post_code varchar(31),
    city      varchar(255),
    country   smallint
);

CREATE TABLE location_coordinates
(
    id bigserial PRIMARY KEY,
    x  varchar(30) NOT NULL,
    y  varchar(30) NOT NULL
);

CREATE TABLE projects
(
    id                     bigserial PRIMARY KEY,
    reference_id           bigint       NOT NULL UNIQUE,
    rcn                    varchar(63)  NOT NULL,
    acronym                varchar(127) NOT NULL,
    title                  varchar(511) NOT NULL,
    legal_basis            varchar(15)  NOT NULL,
    status                 smallint     NOT NULL,
    funding_scheme         smallint     NOT NULL,

    sign_date              date         NOT NULL,
    start_date             date         NOT NULL,
    end_date               date         NOT NULL,

    call_id                bigint,
    master_call_identifier varchar(64)  NOT NULL,

    funding_organisation   numeric(12, 2),
    funding_eu             numeric(12, 2),

    created_at             timestamp DEFAULT now(),
    updated_at             timestamp DEFAULT now(),
    version                integer   DEFAULT 0
);

CREATE INDEX projects_reference_id_idx ON projects (reference_id);
CREATE INDEX projects_call_id_idx ON projects (call_id);
ALTER TABLE projects
    ADD CONSTRAINT fk_projects_call_id FOREIGN KEY (call_id) REFERENCES calls (id);

CREATE TABLE organisations
(
    id                      bigserial PRIMARY KEY,
    reference_id            bigint       NOT NULL UNIQUE,
    rcn                     varchar(63)  NOT NULL,
    name                    varchar(255) NOT NULL,
    short_name              varchar(255),
    location_coordinates_id bigint,
    address_id              bigint,
    vat_number              varchar(63),
    nuts_code               varchar(15),
    sme                     smallint     NOT NULL,
    type                    smallint,

    created_at              timestamp DEFAULT now(),
    updated_at              timestamp DEFAULT now(),
    version                 integer   DEFAULT 0
);

CREATE INDEX organisations_reference_id_idx ON organisations (reference_id);
ALTER TABLE organisations
    ADD CONSTRAINT fk_organisations_address_id FOREIGN KEY (address_id) REFERENCES address (id);
ALTER TABLE organisations
    ADD CONSTRAINT fk_organisations_location_coordinates_id FOREIGN KEY (location_coordinates_id) REFERENCES location_coordinates (id);

CREATE TABLE organisation_project_join
(
    id                   bigserial PRIMARY KEY,
    organisation_id      bigint   NOT NULL,
    project_id           bigint   NOT NULL,
    type                 smallint NOT NULL,

    funding_organisation numeric(12, 2),
    funding_eu           numeric(12, 2),

    created_at           timestamp DEFAULT now()
);

CREATE INDEX organisation_project_join_organisation_id_idx ON organisation_project_join (organisation_id);
CREATE INDEX organisation_project_join_project_id_idx ON organisation_project_join (project_id);
ALTER TABLE organisation_project_join
    ADD CONSTRAINT fk_organisation_project_join_organisation_id FOREIGN KEY (organisation_id) REFERENCES organisations (id);
ALTER TABLE organisation_project_join
    ADD CONSTRAINT fk_organisation_project_join_project_id FOREIGN KEY (project_id) REFERENCES projects (id);

CREATE TABLE organisation_contact_info
(
    id              bigserial PRIMARY KEY,
    organisation_id bigint       NOT NULL,
    type            smallint     NOT NULL,
    name            varchar(127) NOT NULL,
    value           varchar(255) NOT NULL,

    created_at      timestamp DEFAULT now(),
    updated_at      timestamp DEFAULT now(),
    version         integer   DEFAULT 0
);

CREATE INDEX organisation_contact_info_organisation_id_idx ON organisation_contact_info (organisation_id);
ALTER TABLE organisation_contact_info
    ADD CONSTRAINT fk_organisation_contact_info_organisation_id FOREIGN KEY (organisation_id) REFERENCES organisations (id);

