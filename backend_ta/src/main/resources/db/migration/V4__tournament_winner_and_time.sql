ALTER TABLE tournament ADD next_round_start_time datetime NULL;

ALTER TABLE tournament ADD winning_team_id BIGINT NULL;

ALTER TABLE tournament ADD CONSTRAINT FK_TOURNAMENT_ON_WINNING_TEAM FOREIGN KEY (winning_team_id) REFERENCES team (id);