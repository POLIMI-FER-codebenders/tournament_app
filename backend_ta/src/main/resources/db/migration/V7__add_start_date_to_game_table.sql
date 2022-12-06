ALTER TABLE game
    ADD start_date datetime NULL;

ALTER TABLE server
    ALTER is_active SET DEFAULT 1;

ALTER TABLE player
    ALTER is_admin SET DEFAULT 0;