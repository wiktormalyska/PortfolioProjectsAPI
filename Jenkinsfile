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

        stage('Input keystore file') {
            steps {
                withCredentials([file(credentialsId: 'portfolio-projects-api-keystore', variable: 'BACKEND_KEYSTORE_FILE')]) {
                    sh '''
                        # Remove the existing file first if it exists
                        rm -f src/main/resources/keystore.p12

                        # Copy the new keystore file
                        cp "$BACKEND_KEYSTORE_FILE" src/main/resources/keystore.p12

                        # Set appropriate permissions
                        chmod 644 src/main/resources/keystore.p12
                   '''
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
                    sh 'find . -name "gradlew" -exec chmod +x {} \\;'
                    sh './gradlew test'
                }
            }
        }

        stage ('Verify Workspace') {
            steps {
                sh 'tree -a'
            }
        }

        stage('Publish Test Results') {
            steps {
                sh 'pwd && ls -R ./build'
                junit '**/test-results/test/*.xml'
            }
        }

        stage('Run Docker Compose') {
            steps {
                script {
                    sh 'docker-compose down'
                    sh 'docker-compose up -d'
                }
            }
        }
    }
    post {
        always {
            sh 'docker-compose logs'
        }
    }
}