ALTER TABLE game
    ADD start_date datetime NOT NULL;

ALTER TABLE game
    MODIFY status enum('CREATED','IN_PHASE_ONE','IN_PHASE_TWO','IN_PHASE_THREE','ENDED','FAILED') DEFAULT 'CREATED' NOT NULL
