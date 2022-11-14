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
        booleanParam(name: "RELEASE",
                description: "Build a release from current commit.",
                defaultValue: false)
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
        stage('Build') {
            steps {
				withMaven(maven: 'M3', mavenSettingsConfig: 'iot_maven') {
					sh "mvn ${params.MVN_PARAMS} -B -DskipTests clean package"
    			} 
			}
        }
        stage('Test') {
            steps {
				withMaven(maven: 'M3', mavenSettingsConfig: 'iot_maven') {
					sh "mvn ${params.MVN_PARAMS} test"
    			} 
			}
		//	post {
        //        always {
        //            junit 'target/surefire-reports/*.xml' 
        //        }
        //    }
        }
        stage('Deploy') {
            steps {
				withMaven(maven: 'M3', mavenSettingsConfig: 'iot_maven') {
					sh "mvn ${params.MVN_PARAMS} deploy"
    			} 
			}
        }
        stage("Release") {
            when {
                expression { params.RELEASE }
            }
            steps {
	               	withMaven(maven: 'M3', 
	               			  mavenSettingsConfig: 'iot_maven') {
						sh """
        				git config --global user.email support@iot-invent.com
        				git config --global user.name iot-invent-bot
        				"""
						sh "mvn ${params.MVN_PARAMS} -B -Dresume=false release:prepare release:perform"
	   				}
            }
        }

 
    }

}