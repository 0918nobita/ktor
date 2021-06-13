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

INSERT INTO tournament(
    title,
    application_period_from,
    application_period_to,
    holding_period_from,
    holding_period_to
) VALUES (
    "APEX CR Cup",
    "2021-06-01 15:00:00",
    "2021-06-10 23:59:00",
    "2021-06-12 19:00:00",
    "2021-06-12 22:00:00"
);

INSERT INTO tournament(
    title,
    application_period_from,
    application_period_to,
    holding_period_from,
    holding_period_to
) VALUES (
    "APEX RG FES",
    "2021-06-13 18:00:00",
    "2021-06-13 20:00:00",
    "2021-06-01 17:00:00",
    "2021-06-10 23:59:00"
);
