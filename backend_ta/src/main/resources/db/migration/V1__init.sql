CREATE TABLE invitation (
  id BIGINT AUTO_INCREMENT NOT NULL,
   id_team BIGINT NOT NULL,
   id_invited_player BIGINT NOT NULL,
   status ENUM('ACCEPTED', 'PENDING', 'REJECTED') DEFAULT 'PENDING' NOT NULL,
   CONSTRAINT PK_INVITATION PRIMARY KEY (id)
);

CREATE TABLE player (
  id BIGINT AUTO_INCREMENT NOT NULL,
   username VARCHAR(45) NOT NULL,
   email VARCHAR(45) NOT NULL,
   password VARCHAR(100) NOT NULL,
   id_team BIGINT DEFAULT NULL NULL,
   `role` ENUM('LEADER', 'MEMBER') DEFAULT NULL,
   CONSTRAINT PK_PLAYER PRIMARY KEY (id),
   UNIQUE (username),
   UNIQUE (email)
);

CREATE TABLE team (
  id BIGINT AUTO_INCREMENT NOT NULL,
   name VARCHAR(45) NOT NULL,
   max_number_of_players TINYINT NOT NULL,
   id_creator BIGINT NOT NULL,
   policy ENUM('OPEN', 'CLOSED') DEFAULT 'OPEN' NOT NULL,
   in_tournament TINYINT DEFAULT 0 NOT NULL,
   date_of_creation date NOT NULL,
   CONSTRAINT PK_TEAM PRIMARY KEY (id),
   UNIQUE (name)
);

CREATE TABLE tournament (
  id BIGINT AUTO_INCREMENT NOT NULL,
   name VARCHAR(40) NOT NULL,
   current_round INT DEFAULT NULL NULL,
   match_type ENUM('MULTIPLAYER', 'MELEE') NOT NULL,
   number_of_teams INT NOT NULL,
   team_size INT NOT NULL,
   status ENUM('TEAMS_JOINING', 'SCHEDULING', 'IN_PROGRESS', 'ENDED') DEFAULT 'TEAMS_JOINING' NOT NULL,
   type ENUM('KNOCKOUT', 'LEAGUE') NOT NULL,
   id_creator BIGINT NOT NULL,
   CONSTRAINT PK_TOURNAMENT PRIMARY KEY (id)
);

CREATE TABLE tournament_score (
  id BIGINT AUTO_INCREMENT NOT NULL,
   league_points INT DEFAULT NULL NULL,
   score INT DEFAULT NULL NULL,
   id_team BIGINT NOT NULL,
   tournament_id BIGINT NOT NULL,
   forfeit BIT(1) DEFAULT 0 NOT NULL,
   CONSTRAINT PK_TOURNAMENT_SCORE PRIMARY KEY (id)
);

ALTER TABLE tournament_score ADD CONSTRAINT uc_3deca3013a8ccac10e5f0c3f0 UNIQUE (id_team, tournament_id);

CREATE INDEX FK_INVITATION_ON_ID_INVITED_PLAYER ON invitation(id_invited_player);

CREATE INDEX ID_creator_idx ON team(id_creator);

CREATE INDEX ID_team_idx ON invitation(id_team);

CREATE INDEX ID_team_player ON player(id_team);

CREATE INDEX id_creator_tournament ON tournament(id_creator);

CREATE INDEX id_team_tournament_score ON tournament_score(id_team);

CREATE INDEX id_tournament_tournament_score ON tournament_score(tournament_id);

ALTER TABLE invitation ADD CONSTRAINT FK_INVITATION_ON_ID_INVITED_PLAYER FOREIGN KEY (id_invited_player) REFERENCES player (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE invitation ADD CONSTRAINT FK_INVITATION_ON_ID_TEAM FOREIGN KEY (id_team) REFERENCES team (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE player ADD CONSTRAINT FK_PLAYER_ON_ID_TEAM FOREIGN KEY (id_team) REFERENCES team (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE team ADD CONSTRAINT FK_TEAM_ON_ID_CREATOR FOREIGN KEY (id_creator) REFERENCES player (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE tournament ADD CONSTRAINT FK_TOURNAMENT_ON_ID_CREATOR FOREIGN KEY (id_creator) REFERENCES player (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE tournament_score ADD CONSTRAINT FK_TOURNAMENT_SCORE_ON_ID_TEAM FOREIGN KEY (id_team) REFERENCES team (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE tournament_score ADD CONSTRAINT FK_TOURNAMENT_SCORE_ON_TOURNAMENT FOREIGN KEY (tournament_id) REFERENCES tournament (id) ON UPDATE RESTRICT ON DELETE RESTRICT;