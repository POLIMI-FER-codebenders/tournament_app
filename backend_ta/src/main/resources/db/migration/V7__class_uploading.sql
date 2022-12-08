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