CREATE TABLE transfers
(
    id              BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    account_from_id BIGINT,
    account_to_id   BIGINT,
    amount          DOUBLE PRECISION,
    currency        VARCHAR(255),
    timestamp       TIMESTAMP,
    CONSTRAINT pk_transfers PRIMARY KEY (id)
);

ALTER TABLE transfers
    ADD CONSTRAINT FK_TRANSFERS_ON_ACCOUNT_FROM FOREIGN KEY (account_from_id) REFERENCES accounts (id);

CREATE INDEX transfer_account_from ON transfers (account_from_id);

ALTER TABLE transfers
    ADD CONSTRAINT FK_TRANSFERS_ON_ACCOUNT_TO FOREIGN KEY (account_to_id) REFERENCES accounts (id);

CREATE INDEX transfer_account_to ON transfers (account_to_id);