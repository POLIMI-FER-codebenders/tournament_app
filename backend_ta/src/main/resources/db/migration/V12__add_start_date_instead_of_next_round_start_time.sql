ALTER TABLE tournament
    ADD start_date datetime NULL;

ALTER TABLE tournament
DROP
COLUMN next_round_start_time;

ALTER TABLE tournament
    MODIFY status ENUM('TEAMS_JOINING', 'SELECTING_CLASSES', 'SCHEDULING', 'IN_PROGRESS', 'ENDED')