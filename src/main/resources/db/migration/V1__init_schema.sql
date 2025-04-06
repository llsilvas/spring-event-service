-- V1__init_schema.sql - Gerado com base no Hibernate DDL

DROP TABLE IF EXISTS event_organizers;
DROP TABLE IF EXISTS ticket_types;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS users;

CREATE TABLE users
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    name       VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    CONSTRAINT PK_users PRIMARY KEY (id),
    CONSTRAINT UK_users_email UNIQUE (email)
) ENGINE = InnoDB;

CREATE TABLE events
(
    id             BIGINT                                   NOT NULL AUTO_INCREMENT,
    name           VARCHAR(255)                             NOT NULL,
    description    VARCHAR(1000),
    location       VARCHAR(255)                             NOT NULL,
    start_datetime DATETIME(6)                              NOT NULL,
    end_datetime   DATETIME(6)                              NOT NULL,
    status         ENUM ('DRAFT', 'PUBLISHED', 'CANCELLED') NOT NULL,
    created_by     BIGINT,
    updated_by     BIGINT,
    created_at     DATETIME(6),
    updated_at     DATETIME(6),
    CONSTRAINT PK_events PRIMARY KEY (id),
    CONSTRAINT FK_events_created_by FOREIGN KEY (created_by) REFERENCES users (id),
    CONSTRAINT FK_events_updated_by FOREIGN KEY (updated_by) REFERENCES users (id)
) ENGINE = InnoDB;

CREATE TABLE ticket_types
(
    id                 BIGINT         NOT NULL AUTO_INCREMENT,
    name               VARCHAR(255)   NOT NULL,
    price              DECIMAL(38, 2) NOT NULL,
    quantity_available INT            NOT NULL,
    event_id           BIGINT         NOT NULL,
    created_by         BIGINT,
    updated_by         BIGINT,
    created_at         DATETIME(6),
    updated_at         DATETIME(6),
    CONSTRAINT PK_ticket_types PRIMARY KEY (id),
    CONSTRAINT FK_ticket_types_event_id FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT FK_ticket_types_created_by FOREIGN KEY (created_by) REFERENCES users (id),
    CONSTRAINT FK_ticket_types_updated_by FOREIGN KEY (updated_by) REFERENCES users (id)
) ENGINE = InnoDB;

CREATE TABLE event_organizers
(
    id         BIGINT                         NOT NULL AUTO_INCREMENT,
    role       ENUM ('COLLABORATOR', 'OWNER') NOT NULL,
    event_id   BIGINT                         NOT NULL,
    user_id    BIGINT                         NOT NULL,
    created_by BIGINT,
    updated_by BIGINT,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    CONSTRAINT PK_event_organizers PRIMARY KEY (id),
    CONSTRAINT FK_event_organizers_event_id FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT FK_event_organizers_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT FK_event_organizers_created_by FOREIGN KEY (created_by) REFERENCES users (id),
    CONSTRAINT FK_event_organizers_updated_by FOREIGN KEY (updated_by) REFERENCES users (id)
) ENGINE = InnoDB;
