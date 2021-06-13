DROP SCHEMA IF EXISTS e_vent;
CREATE SCHEMA e_vent;
USE e_vent;

DROP TABLE IF EXISTS tournament;
CREATE TABLE tournament (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title TEXT NOT NULL,
    application_period_from DATETIME NOT NULL,
    application_period_to   DATETIME NOT NULL,
    holding_period_from     DATETIME NOT NULL,
    holding_period_to       DATETIME NOT NULL
) DEFAULT CHARACTER SET=utf8;
