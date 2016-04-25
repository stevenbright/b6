--
-- Tables
--

CREATE TABLE entity_type (
  id                INTEGER PRIMARY KEY,
  name              CHAR(32) NOT NULL,
  CONSTRAINT uq_entity_type_name UNIQUE (name)
);
