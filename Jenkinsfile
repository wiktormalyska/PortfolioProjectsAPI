pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'portfolio-projects-api'
    }

    stages {
        stage('Input .env file') {
            steps {
                withCredentials([file(credentialsId: 'portfolio-projects-api-.env', variable: 'BACKEND_ENV_FILE')]) {
                    sh 'cp "$BACKEND_ENV_FILE" src/main/resources/.env'
                }
            }
        }

         stage('Build Docker Image') {
             steps {
                 script {
                     sh 'docker build -t ${DOCKER_IMAGE} .'
                 }
             }
         }

        stage ('Run Tests') {
            steps {
                script {
                    sh 'docker run --rm ${DOCKER_IMAGE} ./gradlew test'
                }
            }
        }

        stage('Publish Test Results') {
            steps {
                junit '**/build/test-logs/*.xml'
            }
        }
    }
    post {
        always {
            sh 'docker rmi ${DOCKER_IMAGE}'
        }
    }
}