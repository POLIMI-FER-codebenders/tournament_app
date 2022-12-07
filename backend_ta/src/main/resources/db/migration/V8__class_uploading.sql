CREATE TABLE game_class
(
    id        BIGINT AUTO_INCREMENT NOT NULL,
    filename  VARCHAR(255) NULL,
    author_id BIGINT NOT NULL,
    data      BLOB NULL,
    CONSTRAINT pk_game_class PRIMARY KEY (id)
);

CREATE TABLE round_class_choice
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    tournament_id BIGINT NOT NULL,
    round         INT    NOT NULL,
    class         BIGINT NOT NULL,
    CONSTRAINT pk_round_class_choice PRIMARY KEY (id)
);

ALTER TABLE round_class_choice
    ADD CONSTRAINT uc_25074871457836a757c351eda UNIQUE (tournament_id, round);

ALTER TABLE game_class
    ADD CONSTRAINT FK_GAME_CLASS_ON_AUTHOR FOREIGN KEY (author_id) REFERENCES player (id);

ALTER TABLE round_class_choice
    ADD CONSTRAINT FK_ROUND_CLASS_CHOICE_ON_CLASS FOREIGN KEY (class) REFERENCES game_class (id);

ALTER TABLE round_class_choice
    ADD CONSTRAINT FK_ROUND_CLASS_CHOICE_ON_TOURNAMENT FOREIGN KEY (tournament_id) REFERENCES tournament (id);

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