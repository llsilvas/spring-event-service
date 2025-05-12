CREATE TABLE organizers
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id           VARCHAR(36)  NOT NULL UNIQUE,
    organization_name VARCHAR(255) NOT NULL,
    status            VARCHAR(20)  NOT NULL,
    contact_email     VARCHAR(255) NOT NULL,
    contact_phone     VARCHAR(30)  NOT NULL,
    document_number   VARCHAR(30)  NOT NULL,
    created_by        VARCHAR(36),
    updated_by        VARCHAR(36),
    created_at        DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE events
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(255) NOT NULL,
    description    TEXT,
    location       VARCHAR(255) NOT NULL,
    start_datetime DATETIME     NOT NULL,
    end_datetime   DATETIME     NOT NULL,
    status         VARCHAR(20)  NOT NULL,
    organizer_id   BIGINT       NOT NULL,
    created_by     VARCHAR(36),
    updated_by     VARCHAR(36),
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_events_organizer FOREIGN KEY (organizer_id) REFERENCES organizers (id)
);

CREATE TABLE ticket_types
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    name               VARCHAR(100)   NOT NULL,
    price              DECIMAL(10, 2) NOT NULL,
    quantity_available INT            NOT NULL,
    event_id           BIGINT         NOT NULL,
    created_by         VARCHAR(36),
    updated_by         VARCHAR(36),
    created_at         DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at         DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_ticket_types_event FOREIGN KEY (event_id) REFERENCES events (id)
);
