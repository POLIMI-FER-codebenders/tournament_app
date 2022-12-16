def image_tag = ""

pipeline {
    agent any
    environment {
       CI = 'false'
       DISCORD_WEBHOOK = credentials('discord_webhook_ta')
    }
    
    stages {
        stage('Discord notify start'){
            when {
                anyOf{
                    branch 'master'
                    branch 'development'
                    branch pattern: "PR-\\d+", comparator: "REGEXP"
                }
            }
            agent any
            steps {
                discordSend (
                    description: "Job started", 
                    footer: "ETA ~10min", 
                    link: env.BUILD_URL, 
                    result: "UNSTABLE", // so we get yellow color in discord, 
                    title: JOB_NAME, 
                    webhookURL: DISCORD_WEBHOOK
                )
            }
            post{
                unsuccessful {
                    discordSend (
                        description: "Hey ${env.CHANGE_AUTHOR}, job is not successful on branch ${env.GIT_BRANCH}", 
                        footer: currentBuild.currentResult, 
                        link: env.BUILD_URL, 
                        result: currentBuild.currentResult, 
                        title: JOB_NAME, 
                        webhookURL: DISCORD_WEBHOOK
                    )
                }
            }
        }
        stage('Run frontend tests') {
            when {
                anyOf{
                    branch 'master'
                    branch 'development'
                    branch pattern: "PR-\\d+", comparator: "REGEXP"
                }
            }
            /*agent {
                // Equivalent to "docker build -f Dockerfile.build --build-arg version=1.0.2 ./build/
                dockerfile {
                    filename './docker/Dockerfile.frontend.test'
                    //dir 'build'
                    //args '-v /root/.m2:/root/.m2'
                }
            }*/
            agent {
                docker { image 'node:16.17.1-alpine3.15' }
            }
            steps {
                dir("frontend_ta"){
                    sh 'npm install react-router-dom'
                    sh 'npm install'
                    sh 'npm run build'
                }
            }
            post{
                unsuccessful {
                    discordSend (
                        description: "Hey ${env.CHANGE_AUTHOR}, job is not successful on branch ${env.GIT_BRANCH}", 
                        footer: currentBuild.currentResult, 
                        link: env.BUILD_URL, 
                        result: currentBuild.currentResult, 
                        title: JOB_NAME, 
                        webhookURL: DISCORD_WEBHOOK
                    )
                }
            }
        }
        stage('Run backend tests') {
            when {
                anyOf{
                    branch 'master'
                    branch 'development'
                    branch pattern: "PR-\\d+", comparator: "REGEXP"
                }
            }
            agent {
                docker { 
                    image 'maven:3.8.6-eclipse-temurin-17' 
                    args '-v $HOME/.m2:/root/.m2'
                }
            }
            /*agent {
                // Equivalent to "docker build -f Dockerfile.build --build-arg version=1.0.2 ./build/
                dockerfile {
                    filename './docker/Dockerfile.backend.test'
                    //dir 'build'
                    args '-v /root/.m2:/root/.m2'
                }
            }*/
            steps {
                dir("backend_ta"){
                    sh 'mvn clean test'
                }
            }
            post{
                success {
                    discordSend (
                        description: "Hey ${env.CHANGE_AUTHOR}, everything checks out on ${env.GIT_BRANCH} :D", 
                        //footer: "Your image: codebenders/codedefenders:${env.GIT_COMMIT}", 
                        link: env.BUILD_URL, 
                        result: currentBuild.currentResult, 
                        title: JOB_NAME, 
                        webhookURL: DISCORD_WEBHOOK
                    )
                }
                unsuccessful {
                    discordSend (
                        description: "Hey ${env.CHANGE_AUTHOR}, job is not successful on branch ${env.GIT_BRANCH}", 
                        footer: currentBuild.currentResult, 
                        link: env.BUILD_URL, 
                        result: currentBuild.currentResult, 
                        title: JOB_NAME, 
                        webhookURL: DISCORD_WEBHOOK
                    )
                }
            }
        }
        stage('Docker build dev') {
            when {
                anyOf{
                    branch 'development'
                }
            }
            agent any
            environment {
		        DOCKERHUB_CREDENTIALS = credentials('dockerhub_access')
	        }
            steps {
                sh "docker build --file ./docker/backend/Dockerfile --tag codebenders/tournament_app_backend:${env.GIT_COMMIT} ."
                sh "docker tag codebenders/tournament_app_backend:${env.GIT_COMMIT} codebenders/tournament_app_backend:dev"
                
                sh "docker build --file ./docker/frontend/Dockerfile --tag codebenders/tournament_app_frontend:${env.GIT_COMMIT} ."
                sh "docker tag codebenders/tournament_app_frontend:${env.GIT_COMMIT} codebenders/tournament_app_frontend:dev"


        		sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'

                sh "docker push codebenders/tournament_app_backend:${env.GIT_COMMIT}"
                sh "docker push codebenders/tournament_app_backend:dev"

                sh "docker push codebenders/tournament_app_frontend:${env.GIT_COMMIT}"
                sh "docker push codebenders/tournament_app_frontend:dev"
            }
            post{
                success{
                    discordSend (
                        description: "Hey team, job is successful on branch ${env.GIT_BRANCH} :D", 
                        footer: "New development images: codebenders/tournament_app_backend:dev, codebenders/tournament_app_backend:${env.GIT_COMMIT}\ncodebenders/tournament_app_frontend:dev and codebenders/tournament_app_frontend:${env.GIT_COMMIT}", 
                        link: env.BUILD_URL, 
                        result: currentBuild.currentResult, 
                        title: JOB_NAME, 
                        webhookURL: DISCORD_WEBHOOK
                    )
                }
                unsuccessful {
                    discordSend (
                        description: "Hey team, job is not successful on branch ${env.GIT_BRANCH} :(", 
                        footer: currentBuild.currentResult, 
                        link: env.BUILD_URL, 
                        result: currentBuild.currentResult, 
                        title: JOB_NAME, 
                        webhookURL: DISCORD_WEBHOOK
                    )
                }
            }
        }
        stage('Docker build release'){
            when {
                anyOf{
                    branch 'master'
                }
            }
            agent any
            environment {
		        DOCKERHUB_CREDENTIALS = credentials('dockerhub_access')
	        }
            steps {
                sh "docker build --file ./docker/backend/Dockerfile --tag codebenders/tournament_app_backend:${env.GIT_COMMIT} ."
                sh "docker tag codebenders/tournament_app_backend:${env.GIT_COMMIT} codebenders/tournament_app_backend:latest"
                
                sh "docker build --file ./docker/frontend/Dockerfile --tag codebenders/tournament_app_frontend:${env.GIT_COMMIT} ."
                sh "docker tag codebenders/tournament_app_frontend:${env.GIT_COMMIT} codebenders/tournament_app_frontend:latest"


        		sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'

                sh "docker push codebenders/tournament_app_backend:${env.GIT_COMMIT}"
                sh "docker push codebenders/tournament_app_backend:latest"

                sh "docker push codebenders/tournament_app_frontend:${env.GIT_COMMIT}"
                sh "docker push codebenders/tournament_app_frontend:latest"
            }
            post{
                success {
                    discordSend (
                        description: "Hey team, job is successful on branch ${env.GIT_BRANCH} :D", 
                        footer: "New release images: codebenders/tournament_app_backend:latest, codebenders/tournament_app_backend:${env.GIT_COMMIT}\ncodebenders/tournament_app_frontend:latest and codebenders/tournament_app_frontend:${env.GIT_COMMIT}", 
                        link: env.BUILD_URL, 
                        result: currentBuild.currentResult, 
                        title: JOB_NAME, 
                        webhookURL: DISCORD_WEBHOOK
                    )
                }
                unsuccessful {
                    discordSend (
                        description: "Hey team, job is not successful on branch ${env.GIT_BRANCH} :(", 
                        footer: currentBuild.currentResult, 
                        link: env.BUILD_URL, 
                        result: currentBuild.currentResult, 
                        title: JOB_NAME, 
                        webhookURL: DISCORD_WEBHOOK
                    )
                }
            }
        }
    }
    post {
        aborted {
            discordSend (
                        description: "Hey team, pipeline was aborted on branch ${env.GIT_BRANCH} :(", 
                        footer: currentBuild.currentResult, 
                        link: env.BUILD_URL, 
                        result: currentBuild.currentResult, 
                        title: JOB_NAME, 
                        webhookURL: DISCORD_WEBHOOK
                    )
        }
    }
}
