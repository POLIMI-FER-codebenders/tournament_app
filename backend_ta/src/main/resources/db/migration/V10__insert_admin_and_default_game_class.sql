INSERT INTO player VALUES (-1,'admin','admin@mail.com','${admin.password}',NULL,NULL,1);

INSERT INTO game_class VALUES (NULL,'${game-class.file-name}',-1,'${game-class.data}');

INSERT INTO server VALUES (NULL,'https://codedef1.duckdns.org','${default-servers.token}',${default-servers.active});

INSERT INTO server VALUES (NULL,'https://codedef2.duckdns.org','${default-servers.token}',${default-servers.active});