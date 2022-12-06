ALTER TABLE player
    MODIFY email VARCHAR (255);

ALTER TABLE server
    ALTER is_active SET DEFAULT 1;

ALTER TABLE player
    ALTER is_admin SET DEFAULT 0;

ALTER TABLE tournament
DROP
COLUMN match_type;

ALTER TABLE tournament
DROP
COLUMN status;

ALTER TABLE tournament
DROP
COLUMN type;

ALTER TABLE tournament
    ADD match_type VARCHAR(255) NOT NULL;

ALTER TABLE team
    MODIFY name VARCHAR (255);

ALTER TABLE tournament
    MODIFY name VARCHAR (255);

ALTER TABLE player
    MODIFY password VARCHAR (255);

ALTER TABLE team
DROP
COLUMN policy;

ALTER TABLE team
    ADD policy VARCHAR(255) NOT NULL;

ALTER TABLE player
DROP
COLUMN `role`;

ALTER TABLE player
    ADD `role` VARCHAR(255) NULL;

ALTER TABLE game
DROP
COLUMN status;

ALTER TABLE game
    ADD status VARCHAR(255) NOT NULL;

ALTER TABLE invitation
DROP
COLUMN status;

ALTER TABLE invitation
    ADD status VARCHAR(255) NOT NULL;

ALTER TABLE tournament
    ADD status VARCHAR(255) NOT NULL;

ALTER TABLE tournament
    ADD type VARCHAR(255) NOT NULL;

ALTER TABLE cd_player
    MODIFY username VARCHAR (255);

ALTER TABLE player
    MODIFY username VARCHAR (255);