CREATE TABLE cd_game_class
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    cd_class_id   INT    NOT NULL,
    id_game_class BIGINT NOT NULL,
    id_server     BIGINT NOT NULL,
    CONSTRAINT pk_cd_game_class PRIMARY KEY (id)
);

ALTER TABLE cd_game_class
    ADD CONSTRAINT uc_c877eb4198f785ba06c926737 UNIQUE (id_game_class, id_server);

ALTER TABLE cd_game_class
    ADD CONSTRAINT FK_CD_GAME_CLASS_ON_ID_GAME_CLASS FOREIGN KEY (id_game_class) REFERENCES game_class (id);

ALTER TABLE cd_game_class
    ADD CONSTRAINT FK_CD_GAME_CLASS_ON_ID_SERVER FOREIGN KEY (id_server) REFERENCES server (id);