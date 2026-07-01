pipeline {
	agent any

	tools {
		jdk 'jdk-17'
	}

	environment {
		//use for Sonar
		scannerHome = tool 'Rikkei SonarQube'

		//use for firebase distribute
		GOOGLE_APPLICATION_CREDENTIALS = credentials('ServiceAccountCredential_Android_BaseMVVM')

		//use for deployGate distribute
		DEPLOYGATE_API_TOKEN = credentials('deploygate_token')
		DEPLOYGATE_USER = 'rikkei_mobile'

		//use for notice by google chat
		//WEBHOOK_GOOGLE_CHAT_URL = credentials('Android_BaseMVVM_GoogleChat_URL')
		WEBHOOK_TEAMS_CHAT_URL = "https://rikkeisoft0.webhook.office.com/webhookb2/23418114-e350-41f3-ba06-ff6e625d91c3@d43d7b87-367a-4e2c-9e40-9ded6a42bf83/IncomingWebhook/aaf1484f6b0d432686fb16e28bb62250/5694eccd-540f-47ab-9898-7c49bde313bd"

		//use for notice by slack
		SLACK_CHANNEL = "#general"
		SLACK_WORKSPACE = "VietBH"
		SLACK_TOKEN = "SLACK_TOKEN_PAIRLA"
	}

	options {
		gitLabConnection('Rikkei GitLab')
		gitlabBuilds(builds: ['SonarScanner'])
	}

	stages {

		stage('Check Git Tag') {
			steps {
				script {
					env.BUILD_FLAG = 'false'
					env.RUN_SONAR = 'true'
					try {
						//get tagName for push tag to deploy
						def tagName = sh(script: 'git describe --tags --exact-match', returnStdout: true).trim()
						env.TAG_NAME = tagName
						if (!tagName) {
							echo "No Git tag found."
							return
						}
						env.RUN_SONAR = 'false'
						//tagName format: targetUpload_flavor => firebase_debug_versionName+versionCode
						try {
							def (targetUpload, flavor, buildVersion) = tagName.tokenize('_')
							def (versionName, versionCode) = buildVersion.tokenize('+')
							echo "TargetToUpload: ${targetUpload}"
							echo "Flavor: ${flavor}"
							def distributeAppTagPattern = ~/^(firebase|deploygate|playStore)_\w+_\d+\.\d+\.\d+(\+\d+)?$/
							if (tagName =~ distributeAppTagPattern) {
								echo "Found Git tag: ${tagName}"
								env.BUILD_FLAG = 'true'
								env.VERSION_NAME = versionName
								env.VERSION_CODE = versionCode
								env.TARGET_UPLOAD = targetUpload
								env.FLAVOR = flavor
							} else {
								sendNoticeWrongTagGoogleChat("Failed", "Android")
							}
						} catch (ignored) {
							sendNoticeWrongTagGoogleChat("Failed", "Android")
						}
					} catch (e) {
						echo "No Git tag found : ${e}"
					}
				}
			}
		}

		stage('SonarScanner & Quality') {
			when {
				expression { return env.RUN_SONAR == 'true' }
			}
			stages {
				stage('SonarScanner') {
					steps {
						withSonarQubeEnv(installationName: 'Rikkei SonarQube') {
							script {
								try {
									updateGitlabCommitStatus name: 'SonarScanner', state: 'running'
									sh './gradlew sonar'
									env.sonarScannerFinished = 1
									echo 'Finish Sonar scanner'
								} catch (e) {
									echo "Scanner error : ${e}"
									updateGitlabCommitStatus name: 'SonarScanner', state: 'canceled'
								}
							}
						}
					}
				}
				stage('Quality Gate') {
					when {
						expression { return env.sonarScannerFinished == '1' }
					}
					steps {
						timeout(time: 10, unit: 'MINUTES') {
							script {
								try {
									def qg = waitForQualityGate()
									if (qg.status == 'OK') {
										updateGitlabCommitStatus name: 'SonarScanner', state: 'success'
									} else {
										updateGitlabCommitStatus name: 'SonarScanner', state: 'failed'
									}
								} catch (ignored) {
									updateGitlabCommitStatus name: 'SonarScanner', state: 'failed'
								} finally {
									def taskInfo = readProperties file: '.scannerwork/report-task.txt'
									def dashboardUrl = taskInfo['dashboardUrl']
									addGitLabMRComment(comment: "Results of Jenkins build available at: <a href='${BUILD_URL}'>${BUILD_URL}</a><br />The SonarQube analyze completed, you can find the results at: <a href='${dashboardUrl}'>${dashboardUrl}</a>")
								}
							}
						}
					}
				}
			}
		}

		stage('Build And Deploy') {
			when {
				expression { return env.BUILD_FLAG == 'true' }
			}
			steps {
				script {
					try {
//						sendNoticeStartingDeployGoogleChat('Android')
						def buildType = env.FLAVOR
						if (env.TARGET_UPLOAD == "firebase") {
							sh 'export GOOGLE_APPLICATION_CREDENTIALS=$GOOGLE_APPLICATION_CREDENTIALS'
							sh "fastlane distribute_to_${env.TARGET_UPLOAD}  buildType:${buildType}"
						}

						if (env.TARGET_UPLOAD == "deploygate") {
							sh "fastlane distribute_to_${env.TARGET_UPLOAD} deployGateToken:${DEPLOYGATE_API_TOKEN} userName:${DEPLOYGATE_USER} buildType:${buildType} "
						}

//						sendGoogleChatNotification('Success', 'Android')
					} catch (e) {
						echo "Error: ${e}"
//						sendGoogleChatNotification('Failed', 'Android')
					}
				}
			}
		}
	}

	post {
		always { echo 'Pipeline finished!' }
		success { echo 'Pipeline succeeded!' }
		failure { echo 'Pipeline failed!' }
		aborted { echo 'Pipeline aborted!' }
	}
}

def sendNoticeToTeams(String platform){
	office365ConnectorSend webhookUrl: "${WEBHOOK_TEAMS_CHAT_URL}",
			factDefinitions: [[name: "fact1", template: "content of fact1"],
							  [name: "fact2", template: "content of fact2"]],
			status: 'Success'
}

//def sendNoticeStartingDeployGoogleChat(String platform) {
//	def distributeMessage = """
//    {
//        "cardsV2": [{
//            "cardId": "card1",
//            "card": {
//                "header": {
//                    "title": "Jenkins Notification",
//                    "subtitle": "${env.JOB_NAME}",
//                    "imageUrl": "https://www.jenkins.io/images/logos/googly/256.png",
//                    "imageType": "CIRCLE"
//                },
//                "sections": [{
//                    "header": "Build Info",
//                    "widgets": [{
//                        "textParagraph": {
//                            "text": "Platform: <b>${platform}</b>"
//                        },
//                        "horizontalAlignment": "START"
//                    },
//                    {
//                        "textParagraph": {
//                            "text": "Distribution status: <b>Starting deploy</b>"
//                        },
//                        "horizontalAlignment": "START"
//                    },
//                    {
//                        "textParagraph": {
//                            "text": "Branch: <b>${env.GIT_BRANCH}</b>"
//                        },
//                        "horizontalAlignment": "START"
//                    },
//                    {
//                        "textParagraph": {
//                            "text": "Version name: ${env.VERSION_NAME} - ${env.VERSION_CODE}"
//                        },
//                        "horizontalAlignment": "START"
//                    },
//                    {
//                        "textParagraph": {
//                            "text": "Distributed to: ${env.TARGET_UPLOAD}"
//                        },
//                        "horizontalAlignment": "START"
//                    },
//                    {
//                        "textParagraph": {
//                            "text": "Flavor: ${env.FLAVOR}"
//                        },
//                        "horizontalAlignment": "START"
//                    },
//                    {
//                        "textParagraph": {
//                            "text": "Triggered by tag: ${env.TAG_NAME}"
//                        },
//                        "horizontalAlignment": "START"
//                    },
//                    {
//                        "textParagraph": {
//                            "text": "Commit Hash: ${GIT_COMMIT}"
//                        },
//                        "horizontalAlignment": "START"
//                    }
//                ],
//                "collapsible": true,
//                "uncollapsibleWidgetsCount": 3
//            },
//            {
//                "widgets": [{
//                    "buttonList": {
//                        "buttons": [{
//                            "text": "View details",
//                            "onClick": {
//                                "openLink": {
//                                    "url": "${env.BUILD_URL}"
//                                }
//                            }
//                        }]
//                    },
//                    "horizontalAlignment": "CENTER"
//                }]
//            }
//        ]
//    }
//    }]
//    }
//    """
//
//	googlechatnotification(
//			url: env.WEBHOOK_GOOGLE_CHAT_URL,
//			message: "${distributeMessage}",
//			messageFormat: "card"
//	)
//}

def sendNoticeStartingDeploySlack(String platform) {
	blocks = [
			[
					"type": "section",
					"text": [
							"type": "mrkdwn",
							"text": "*${env.JOB_NAME}*"
					]
			],
			[
					"type": "divider"
			],
			[
					"type": "section",
					"text": [
							"type": "mrkdwn",
							"text": "*Build Info*\n" +
									"Platform: *${platform}*\n" +
									"Distribution status: *Starting deploy*\n" +
									"Version name: *${env.VERSION_NAME} - ${env.VERSION_CODE}*\n" +
									"Distributed to: *${env.TARGET_UPLOAD}*\n" +
									"Flavor: *${env.FLAVOR}*\n" +
									"Triggered by tag: *${env.TAG_NAME}*\n" +
									"Commit Hash *${GIT_COMMIT}*\n" +
									"Build Url: *${env.BUILD_URL}*"
					],
			]
	]
	slackSend botUser: true, channel: "${env.SLACK_CHANNEL}", color: "${color}", blocks: blocks, teamDomain: "${env.SLACK_WORKSPACE}", tokenCredentialId: "${env.SLACK_TOKEN}"
}

//def sendNoticeWrongTagGoogleChat(String status, String platform) {
//	def color = getColorNotice(status)
//	def distributeMessage = """
//    {
//        "cardsV2": [{
//            "cardId": "card1",
//            "card": {
//                "header": {
//                    "title": "Jenkins Notification",
//                    "subtitle": "${env.JOB_NAME}",
//                    "imageUrl": "https://www.jenkins.io/images/logos/googly/256.png",
//                    "imageType": "CIRCLE"
//                },
//                "sections": [{
//                    "header": "Wrong tag format",
//                    "widgets": [{
//                        "textParagraph": {
//                            "text": "Platform: <b>${platform}</b>"
//                        },
//                        "horizontalAlignment": "START"
//                    },
//                    {
//                        "textParagraph": {
//                            "text": "Tag: <b><font color='${color}'>${env.TAG_NAME}</font></b>"
//
//                        },
//                        "horizontalAlignment": "START"
//                    },
//                    {
//                        "textParagraph": {
//                            "text": "<b><font color='${color}'>Check the format again</font></b>"
//                        },
//                        "horizontalAlignment": "START"
//                    }
//                ],
//                "collapsible": true,
//                "uncollapsibleWidgetsCount": 3
//            },
//            {
//                "widgets": [{
//                    "buttonList": {
//                        "buttons": [{
//                            "text": "View details",
//                            "onClick": {
//                                "openLink": {
//                                    "url": "${env.BUILD_URL}"
//                                }
//                            }
//                        }]
//                    },
//                    "horizontalAlignment": "CENTER"
//                }]
//            }
//        ]
//    }
//    }]
//    }
//    """
//	googlechatnotification(
//			url: env.WEBHOOK_GOOGLE_CHAT_URL,
//			message: "${distributeMessage}",
//			messageFormat: "card"
//	)
//}

def sendNoticeWrongTagSlack(String status, String platform) {
	blocks = [
			[
					"type": "section",
					"text": [
							"type": "mrkdwn",
							"text": "*${env.JOB_NAME}*"
					]
			],
			[
					"type": "divider"
			],
			[
					"type": "section",
					"text": [
							"type": "mrkdwn",
							"text": "*Build Info*\n" +
									"Platform: *${platform}*\n" +
									"Distribution status: *${status}*\n" +
									"Wrong tag format: *${env.TAG_NAME}*\n" +
									"Check the format again\n" +
									"Build Url: *${env.BUILD_URL}*"
					],
			]
	]
	slackSend botUser: true, channel: "${env.SLACK_CHANNEL}", color: "${color}", blocks: blocks, teamDomain: "${env.SLACK_WORKSPACE}", tokenCredentialId: "${env.SLACK_TOKEN}"
}

//def sendGoogleChatNotification(String status, String platform) {
//	// Send notification to Google Chat
//	///status: Success, Failed, Skipped, Canceled, Unknown
//	def color = getColorNotice(status)
//	def distributeMessage = """
//    {
//        "cardsV2": [{
//            "cardId": "card1",
//            "card": {
//                "header": {
//                    "title": "Jenkins Notification",
//                    "subtitle": "${env.JOB_NAME}",
//                    "imageUrl": "https://www.jenkins.io/images/logos/googly/256.png",
//                    "imageType": "CIRCLE"
//                },
//                "sections": [{
//                    "header": "Build Info",
//                    "widgets": [{
//                        "textParagraph": {
//                            "text": "Platform: <b>${platform}</b>"
//                        },
//                        "horizontalAlignment": "START"
//                    },
//                    {
//                        "textParagraph": {
//                            "text": "Distribution status: <b><font color='${color}'>${status}</font></b>"
//                        },
//                        "horizontalAlignment": "START"
//                    },
//                    {
//                        "textParagraph": {
//                            "text": "Version name: ${env.VERSION_NAME} - ${env.VERSION_CODE}"
//                        },
//                        "horizontalAlignment": "START"
//                    },
//                    {
//                        "textParagraph": {
//                            "text": "Distributed to: ${env.TARGET_UPLOAD}"
//                        },
//                        "horizontalAlignment": "START"
//                    },
//                    {
//                        "textParagraph": {
//                            "text": "Flavor: ${env.FLAVOR}"
//                        },
//                        "horizontalAlignment": "START"
//                    },
//                    {
//                        "textParagraph": {
//                            "text": "Triggered by tag: ${env.TAG_NAME}"
//                        },
//                        "horizontalAlignment": "START"
//                    },
//                    {
//                        "textParagraph": {
//                            "text": "Commit Hash: ${GIT_COMMIT}"
//                        },
//                        "horizontalAlignment": "START"
//                    }
//                ],
//                "collapsible": true,
//                "uncollapsibleWidgetsCount": 3
//            },
//            {
//                "widgets": [{
//                    "buttonList": {
//                        "buttons": [{
//                            "text": "View details",
//                            "onClick": {
//                                "openLink": {
//                                    "url": "${env.BUILD_URL}"
//                                }
//                            }
//                        }]
//                    },
//                    "horizontalAlignment": "CENTER"
//                }]
//            }
//        ]
//    }
//    }]
//    }
//    """
//
//	googlechatnotification(
//			url: env.WEBHOOK_GOOGLE_CHAT_URL,
//			message: "${distributeMessage}",
//			messageFormat: "card"
//	)
//
//}

def sendSlackChatNotification(String status, String platform) {
	def color = getColorNotice(status)
	blocks = [
			[
					"type": "section",
					"text": [
							"type": "mrkdwn",
							"text": "*${env.JOB_NAME}*"
					]
			],
			[
					"type": "divider"
			],
			[
					"type": "section",
					"text": [
							"type": "mrkdwn",
							"text": "*Build Info*\n" +
									"Platform: *${platform}*\n" +
									"Distribution status: *${status}*\n" +
									"Version name: *${env.VERSION_NAME} - ${env.VERSION_CODE}*\n" +
									"Distributed to: *${env.TARGET_UPLOAD}*\n" +
									"Flavor: *${env.FLAVOR}*\n" +
									"Triggered by tag: *${env.TAG_NAME}*\n" +
									"Commit Hash *${GIT_COMMIT}*\n" +
									"Build Url: *${env.BUILD_URL}*"
					],
			]
	]
	slackSend botUser: true, channel: "${env.SLACK_CHANNEL}", color: "${color}", blocks: blocks, teamDomain: "${env.SLACK_WORKSPACE}", tokenCredentialId: "${env.SLACK_TOKEN}"
}

static def getColorNotice(String status) {
	def color = ""
	///status: Success, Failed, Skipped, Canceled, Unknown
	if (status == 'Success') {
		color = "#008000"
	} else if (status == 'Failed' || status == 'Canceled') {
		color = "#FF0000"
	} else if (status == 'Skipped') {
		color = "#FFA500"
	} else {
		color = "#000000"
	}
	return color
}


										
										
										
										
										
										
										
										
										
										