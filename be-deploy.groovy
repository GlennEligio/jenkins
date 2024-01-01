pipeline {
    agent any
    
    parameters {
        gitParameter branchFilter: 'origin/(.*)', defaultValue: 'master', name: 'BRANCH', type: 'PT_BRANCH', quickFilterEnabled: true, sortMode: "DESCENDING_SMART"
    }
    
    stages {
        stage ('Fetch source code') {
            steps {
                sh 'cd /var/lib/jenkins/workspace/dntx/deploy/be-deploy'
                git branch: "${params.BRANCH}", url: 'https://github.com/GlennEligio/dn-tx.git'
            }
        }
        
        stage ('Install dependencies') {
            steps {
                sh 'cd ./backend && mvn install -DskipTests'
            }
        }
        
        stage ('Build docker image') {
			steps {
				sh "cd ./backend && mvn spring-boot:build-image -DskipTests '-Dmodule.image.name=dntx-backend' '-Dmodule.image.tag=${params.BRANCH}' '-Dmodule.build.timestamp=${BUILD_TIMESTAMP}'"
			}
        }
        
        stage('Push Docker Image') {
            steps {
                script {
                    // Use withDockerRegistry to push the image
                    withDockerRegistry([url: "", credentialsId: "docker-hub-credentials"]) {
                        sh "docker push 'shuntjg/dntx-backend:${params.BRANCH}_${BUILD_TIMESTAMP}'"
                    }
                }
            }
        }
    }
}