--
-- COPYRIGHT Ericsson 2022-2024
--
--
--
-- The copyright to the computer program(s) herein is the property of
--
-- Ericsson Inc. The programs may be used and/or copied only with written
--
-- permission from Ericsson Inc. or in accordance with the terms and
--
-- conditions stipulated in the agreement/contract under which the
--
-- program(s) have been supplied.
--
CREATE SEQUENCE subsystem_type_id_seq INCREMENT 1 MINVALUE 1 START WITH 20;

CREATE TABLE IF NOT EXISTS subsystem_type (
	subsystem_type_id int8 DEFAULT nextval('subsystem_type_id_seq') NOT NULL,
	type varchar(255) NULL,
	alias varchar(255),
	CONSTRAINT subsystem_type_pkey PRIMARY KEY (subsystem_type_id),
	CONSTRAINT uniquesubsystemtypeconstraint UNIQUE (type)
);

CREATE SEQUENCE subsystem_subtype_id_seq INCREMENT 1 MINVALUE 1 START WITH 1;

CREATE TABLE IF NOT EXISTS subsystem_subtype (
    subsystem_subtype_id bigint DEFAULT nextval('subsystem_type_id_seq'),
    name varchar(255) NOT NULL,
    alias varchar(255),
    CONSTRAINT subsystem_subtype_pk PRIMARY KEY (subsystem_subtype_id),
    CONSTRAINT uniquesubsystemsubtypenameconstraint UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS subsystem_type_subtype (
    subsystem_subtype_id bigint NOT NULL,
    subsystem_type_id bigint NOT NULL,
    CONSTRAINT subsystem_subtype_id_fk FOREIGN KEY (subsystem_subtype_id) REFERENCES subsystem_subtype(subsystem_subtype_id),
    CONSTRAINT subsystem_type_id_fk FOREIGN KEY (subsystem_type_id) REFERENCES subsystem_type(subsystem_type_id)
);

ALTER SEQUENCE IF EXISTS subsystem_subtype_id_seq RESTART WITH 1000;

INSERT INTO subsystem_type (subsystem_type_id, type, alias)
VALUES (1, 'DomainManager', 'Domain manager'),
(2, 'NFVO', 'NFVO'),
(5, 'PhysicalDevice', 'Physical device'),
(6, 'CmGateway', 'Cm gateway'),
(7, 'InventorySystem', 'Inventory system'),
(8, 'DomainOrchestrator', 'Domain orchestrator'),
(9, 'AuthenticationSystems', 'Authentication systems'),
(10, 'REST', 'REST');

INSERT INTO subsystem_subtype(subsystem_subtype_id, name, alias) VALUES (1, 'Oauth2ClientCredentials', 'Oauth2 client credentials');

INSERT INTO subsystem_type_subtype(subsystem_type_id, subsystem_subtype_id) VALUES (9, 1);

CREATE TABLE IF NOT EXISTS subsystem (
	subsystem_id int8 NOT NULL,
	health_check_time varchar(255) NULL,
	name varchar(255) NULL,
	operational_state int4 NULL,
	subsystem_type_id int8 NULL,
	url varchar(255) NULL,
	vendor varchar(255) NULL,
	adapter_link varchar(255) NULL,
	api_key UUID NOT NULL,
	subsystem_subtype_id bigint,
	CONSTRAINT subsystem_pkey PRIMARY KEY (subsystem_id),
	CONSTRAINT uniquesubsystemnameconstraint UNIQUE (name),
	CONSTRAINT fk21buurj2g088ttlvjwyib7dge FOREIGN KEY (subsystem_type_id) REFERENCES subsystem_type(subsystem_type_id),
	CONSTRAINT subsystem_subtype_fk FOREIGN KEY (subsystem_subtype_id) REFERENCES subsystem_subtype(subsystem_subtype_id),
	CONSTRAINT uniquesubsystemapikeyconstraint UNIQUE(api_key)
);

CREATE TABLE IF NOT EXISTS connection_properties (
	connection_props_id int8 NOT NULL,
	subsystem_id int8 NULL,
	CONSTRAINT connection_properties_pkey PRIMARY KEY (connection_props_id),
	CONSTRAINT fk1jqc7wja5ns1pxel1wp9dwdid FOREIGN KEY (subsystem_id) REFERENCES subsystem(subsystem_id)
);

CREATE TABLE IF NOT EXISTS subsystem_user (
	subsystem_user_id int8 NOT NULL,
	connection_props_id int8 NULL,
	CONSTRAINT subsystem_user_pkey PRIMARY KEY (subsystem_user_id),
	CONSTRAINT fkp3v3yi8mhifrp63ifdkp6godb FOREIGN KEY (connection_props_id) REFERENCES connection_properties(connection_props_id)
);

CREATE TABLE IF NOT EXISTS properties (
	property_id SERIAL,
	connection_props_id int8 NULL,
	key varchar(255) NULL,
	value text NULL,
	is_encrypted boolean NULL,
	CONSTRAINT pk_properties PRIMARY KEY (property_id),
	CONSTRAINT fk_connection_properties_properties FOREIGN KEY (connection_props_id) REFERENCES connection_properties(connection_props_id)
);

CREATE SEQUENCE hibernate_sequence
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START WITH 2
