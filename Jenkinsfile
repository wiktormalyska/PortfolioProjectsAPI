pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'PortfolioProjectsAPI'
    }

    stages {
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