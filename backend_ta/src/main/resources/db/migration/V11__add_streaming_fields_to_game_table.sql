ALTER TABLE game
    ADD last_scheduled_event_sending_time BIGINT NULL;

ALTER TABLE game
    ADD last_scheduled_event_timestamp BIGINT NULL;

ALTER TABLE game
    ADD last_sent_score_event_timestamp BIGINT DEFAULT 0 NULL;

ALTER TABLE game
    ADD streamed_attackers_score INT DEFAULT 0 NULL;

ALTER TABLE game
    ADD streamed_defenders_score INT DEFAULT 0 NULL;

ALTER TABLE game
    MODIFY last_sent_score_event_timestamp BIGINT NOT NULL;

ALTER TABLE game
    MODIFY streamed_attackers_score INT NOT NULL;

ALTER TABLE game
    MODIFY streamed_defenders_score INT NOT NULL;