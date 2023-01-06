# Tournament Application Properties

This file contains all available application properties which can be set to change application server settings.

- `spring.datasource.url`: url of the application database schema
- `spring.datasource.username`: username of application database user
- `spring.datasource.password`: password of application database user
- `code-defenders.default-servers.token`: admin token of the two default CodeDefenders instances. These instances are used for testing purposes. If set the application will automatically register the two default instances using the given token. Do not assign this property to avoid registering default servers at production time
- `code-defenders.default-game-class.file-name`: file name of the default class under testing automatically stored in the database at application deployment. The file containing the class must be located in [/resources/game_classes](https://github.com/POLIMI-FER-codebenders/tournament_app/tree/main/backend_ta/src/main/resources/game_classes)
- `tournament-app.web-server.address`: address of the web server hosting the frontend of the Tournament Application
- `tournament-app.admin.password`: password of the Tournament Application admin account
- `tournament-app.tournament.class-selection-time-duration`: duration in seconds of SELECTING_CLASSES tournament phase
- `tournament-app.tournament-match.break-time-duration`: duration in seconds of the break time preceding each game
- `tournament-app.tournament-match.phase-one-duration`: duration in seconds of the first phase of the game
- `tournament-app.tournament-match.phase-two-duration`: duration in seconds of the second phase of the game
- `tournament-app.tournament-match.phase-three-duration`: duration in seconds of the third phase of the game
- `tournament-app.streaming.updates-delay`: duration in milliseconds of the time interval between each request of updates made by the streaming component to CodeDefenders servers