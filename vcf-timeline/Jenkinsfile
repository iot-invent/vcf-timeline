#!/usr/bin/env groovy
pipeline {

    agent any
    
    options {
        timestamps()
        disableConcurrentBuilds()
		// Keep only the last 5 builds
    	buildDiscarder(logRotator(numToKeepStr: '5', artifactNumToKeepStr: '5'))
        skipStagesAfterUnstable()
	}
	triggers {
        cron('H 4 * * 1-5')
    }
	parameters {
        booleanParam(name: "CLEANUP",
                description: "Cleanup Current Workspace.",
                defaultValue: false)
//        booleanParam(name: "RELEASE",
//                description: "Build a release from current commit.",
//                defaultValue: false)
        string(name: "MVN_PARAMS", defaultValue: "", description: "Aditional Maven Parameters")
    }
	
    stages {
		stage("Cleanup") {
            when {
                expression { params.CLEANUP }
            }
            steps {
				cleanWs()
            	checkout scm
            }
        }
        stage('Build & Deploy') {
            when {
       			not { branch 'master' }
      		}
            steps {
				nodejs(nodeJSInstallationName: 'nodeJs') {
					withMaven(maven: 'M3', mavenSettingsConfig: 'iot_maven') {
						sh "mvn ${params.MVN_PARAMS} -e -B clean deploy -Pproduction"
    				}
				}
  			}
        }

 		stage("Release") {
            when { branch 'master' }
            environment {
        		RELEASE_VERSION = getNextRelease()
     		}
            steps {
				echo "Releasing ${RELEASE_VERSION}"
				withMaven(maven: 'M3', mavenSettingsConfig: 'iot_maven') {
					
					sh "mvn -B clean -Pproduction -Drevision=${RELEASE_VERSION} -Dchangelist="
					sh "mvn -B enforcer:enforce -Pproduction -Drevision=${RELEASE_VERSION} -Dchangelist="
					sh "mvn -B deploy -Pproduction -Drevision=${RELEASE_VERSION} -Dchangelist="
    			}
    			withCredentials([gitUsernamePassword(credentialsId: 'iot-invent-bot',
                 	gitToolName: 'Default')]) {
                	sh """
       				git config --global user.email support@iot-invent.com
       				git config --global user.name iot-invent-bot
       				"""
       				sh "git tag v${RELEASE_VERSION}"
					sh "git push origin v${RELEASE_VERSION}"
                } 
            }
        }
    }
    post {
		always {
      		sendNotifications currentBuild.result
    	}
    }
}