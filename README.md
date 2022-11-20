# CodeDefenders Tournament Application
Tournament application for CodeDefenders. Developed by a team of POLIMI and FER students for the Distributed Software Development course and the SCORE competition

## Deployment with Docker
Our application can be easily deployed and run using Docker!

### Before deploying
You can change database credentials and default port for the web server by editing the [.env](.env) file.

WARNING: At the moment it is only possible to change the default port for the web server and not for the application server. Make sure that port 8080 is available on you local machine.

### How to deploy
Follow these three steps:
- clone this repository and move to its root
- run `docker compose up -d`
- restart the containers by running `docker compose stop` and `docker compose up -d` again (this step can be necessary to avoid some HTTP errors)

You can now access the tournament application for CodeDefenders at [http://localhost:80/](http://localhost:80/).
