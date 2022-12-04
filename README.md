# CodeDefenders Tournament Application
Tournament application for CodeDefenders. Developed by a team of POLIMI and FER students for the Distributed Software Development course and the SCORE competition

## Deployment with Docker
Our application can be easily deployed and run using Docker!

### Before deploying
You can change database credentials and default port for web and application servers by editing the [.env](.env) file.

WARNING: At the moment application server port can only be changed if you are building frontend image yourself. It can't be changed if you are using an image downloaded from Dockerhub.

### How to deploy
Follow these three steps:
- clone this repository and move to its root
- run `docker compose up -d`
- restart the containers by running `docker compose stop` and `docker compose up -d` again (this step can be necessary to avoid some HTTP errors)

You can now access the tournament application for CodeDefenders at [http://localhost:80/](http://localhost:80/).
