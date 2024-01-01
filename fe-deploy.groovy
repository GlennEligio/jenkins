pipeline {
    agent any
    
    parameters {
        gitParameter branchFilter: 'origin/(.*)', defaultValue: 'master', name: 'BRANCH', type: 'PT_BRANCH', quickFilterEnabled: true, sortMode: "DESCENDING_SMART"
    }
    
    stages {		
		stage('Checkout') {
			steps {
				script {
				checkout([$class: 'GitSCM', branches: [[name: 'origin/*']], userRemoteConfigs: [[url: 'https://github.com/GlennEligio/dn-tx.git']]])
				}
			}
		}
	
        stage ('Fetch source code') {
            steps {
                sh 'cd /var/lib/jenkins/workspace/dntx/deploy/fe-deploy'
                git branch: "${params.BRANCH}", url: 'https://github.com/GlennEligio/dn-tx.git'
            }
        }
        
        stage ('Install dependencies') {
            steps {
                sh 'cd /var/lib/jenkins/workspace/dntx/deploy/fe-deploy/frontend && npm install'
            }
        }
        
        stage ('Build docker image') {
			steps {
				sh "cd ./frontend && docker build -f Dockerfile.prod -t 'shuntjg/dntx-frontend:${params.BRANCH}_${BUILD_TIMESTAMP}' ."
			}
        }
        
        stage('Docker Image') {
            steps {
                script {
                    // Use withDockerRegistry to push the image
                    withDockerRegistry([url: "", credentialsId: "docker-hub-credentials"]) {
                        sh "docker push 'shuntjg/dntx-frontend:${params.BRANCH}_${BUILD_TIMESTAMP}'"
                    }
                }
            }
        }
    }
}