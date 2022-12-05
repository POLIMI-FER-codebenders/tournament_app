CREATE TABLE server
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    address     VARCHAR(255)     NOT NULL,
    admin_token VARCHAR(255)     NOT NULL,
    is_active   BIT(1) DEFAULT 1 NOT NULL,
    CONSTRAINT pk_server PRIMARY KEY (id)
);

ALTER TABLE cd_player
    ADD id_server BIGINT NULL;

ALTER TABLE cd_player
    MODIFY id_server BIGINT NOT NULL;

ALTER TABLE game
    ADD id_server BIGINT NULL;

ALTER TABLE server
    ADD CONSTRAINT uc_server_address UNIQUE (address);

ALTER TABLE cd_player
    ADD CONSTRAINT FK_CD_PLAYER_ON_ID_SERVER FOREIGN KEY (id_server) REFERENCES server (id);

ALTER TABLE game
    ADD CONSTRAINT FK_GAME_ON_ID_SERVER FOREIGN KEY (id_server) REFERENCES server (id);

ALTER TABLE cd_player
DROP
COLUMN server;

ALTER TABLE game
DROP
COLUMN server;

ALTER TABLE cd_player
    MODIFY token VARCHAR (255) NOT NULL;

ALTER TABLE cd_player
DROP
FOREIGN KEY FK_CD_PLAYER_ON_ID_PLAYER;

ALTER TABLE cd_player
DROP
KEY uc_03937c85befdf71d84d6ad533;

ALTER TABLE cd_player ADD CONSTRAINT FK_CD_PLAYER_ON_ID_PLAYER FOREIGN KEY (id_player) REFERENCES player (id);

ALTER TABLE cd_player ADD CONSTRAINT uc_03937c85befdf71d84d6ad533 UNIQUE (id_player, id_server);