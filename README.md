# CodeDefenders Tournament Application
Tournament Application for CodeDefenders. Developed by a team of POLIMI and FER students for the Distributed Software Development course and the SCORE competition

## Deployment with Docker
Our application can be easily deployed and run using Docker!

### How to deploy
Follow these steps:
- clone this repository and move to its root
- run `docker compose up -d`
- register an active CodeDefenders server following these [instructions](https://github.com/POLIMI-FER-codebenders/tournament_app/tree/main/documentation/manage_CodeDefenders_instances.md)

You can now access and use the tournament application for CodeDefenders at [http://localhost:80/](http://localhost:80/).

### Custom deployment
You can change database credentials, admin credentials and default port for web and application servers by editing the [.env](.env) file.

WARNING: At the moment web and application server ports can only be changed if you are building frontend and backend images yourself. They can't be changed if you are using images downloaded from our [Dockerhub repository](https://hub.docker.com/u/codebenders).

In the [.env](.env) file you can also set the duration of each phase of the game. Have a look at [this document](https://github.com/POLIMI-FER-codebenders/tournament_app/tree/main/documentation/application_properties.md) for a complete list of properties of the application server.
