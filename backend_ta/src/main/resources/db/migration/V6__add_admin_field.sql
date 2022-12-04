ALTER TABLE player
    ADD is_admin BIT(1) DEFAULT 0 NOT NULL;

ALTER TABLE server
    ALTER is_active SET DEFAULT 1;