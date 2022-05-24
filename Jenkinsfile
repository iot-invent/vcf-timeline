#!groovy
@Library('iot-invent-shared') _

pipeline {

    agent any
    // Prepare required tools
 	tools {
  		maven 'M3'
 	}
    options {
        timestamps()
        disableConcurrentBuilds()
		// Keep only the last 7 builds
    	buildDiscarder(logRotator(numToKeepStr: '3', artifactNumToKeepStr: '3'))
        skipStagesAfterUnstable()
	}
	parameters {
        booleanParam(name: "CLEANUP",
                description: "Cleanup Current Workspace.",
                defaultValue: false)
        booleanParam(name: "RELEASE",
                description: "Build a release from current commit.",
                defaultValue: false)
        string(name: "MVN_PARAMS", defaultValue: "", description: "Aditional Maven Parameters for: mvn clean install deploy")
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
//			post {
//                always {
//                    junit 'target/surefire-reports/*.xml' 
//                }
//            }
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
				withMaven(maven: 'M3', mavenSettingsConfig: 'iot_maven') {
					sh "mvn ${params.MVN_PARAMS} -B -Dresume=false release:prepare release:perform"
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