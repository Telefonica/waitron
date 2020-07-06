def awsCredentials = [[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: '047a2523-0a76-477f-a24d-17efc60b6a82', accessKeyVariable: 'AWS_ACCESS_KEY_ID', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']]

pipeline {
    agent { node { label 'prod-nc-slave-01' } }
    options {
        disableConcurrentBuilds()
        withCredentials (awsCredentials)
    }
    environment {
        ENVIRONMENT_NAME = 'waitron-builder'
        GIT_BRANCH = 'master'
        GIT_REPONAME = 'waitron'
        AWS_DEFAULT_REGION = 'eu-west-3'
        AWS_DEFAULT_OUTPUT = 'json'
        WAITRON_VER = '20200706-1'
    }
    stages {
        stage ('build waitron docker image') {
            steps {
                dir ("${WORKSPACE}") {
                    sh '''
                         $(aws ecr get-login --no-include-email)
                         docker build . -t 709233559969.dkr.ecr.eu-west-3.amazonaws.com/waitron:${WAITRON_VER}
                       '''
                }
             }
          }
        stage ('push waitron docker image to registry') {
            steps {
                dir ("${WORKSPACE}") {
                    sh '''
                         docker push 709233559969.dkr.ecr.eu-west-3.amazonaws.com/waitron:${WAITRON_VER}
                       '''
               }
             }
        }
    }
    post {
      success {
            echo 'Waitron docker image backend built and pushed'
            echo ' clean dockerhub credentials'
            sh 'rm -f ${HOME}/.docker/config.json'
      }
    }
  }