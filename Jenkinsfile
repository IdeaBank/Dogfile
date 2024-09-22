pipeline {
    agent {
    kubernetes { // for kaniko
yaml '''
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: kaniko
    image: gcr.io/kaniko-project/executor:debug
    command: ['sleep']
    args: ['infinity']
    volumeMounts:
      - name: docker-registry
        mountPath: /kaniko/.docker
  volumes:
    - name: docker-registry
      secret:
        secretName: regcred
        items: 
        - key: .dockerconfigjson
          path: config.json
'''
    }
  }

    parameters {
        string(name : 'application_name', defaultValue : 'dogfile', description : 'application name. this must be unique')
        string(name : 'gitlab_link', defaultValue : 'gitlab.211-34-229-165.nip.io/honeybadger/dogfile/dogfile-spring', description : 'gitlab link')
        string(name : 'helm_chart', defaultValue : 'gitlab.211-34-229-165.nip.io/honeybadger/devops/dogfile-helm-chart', description : 'helm chart')
        string(name : 'helm_chart_name', defaultValue : 'dogfile-helm-chart', description : 'helm chart name')
        string(name : 'gitlab_branch', defaultValue : 'deploy', description : 'gitlab branch')
        string(name : 'image_name', defaultValue : 'dogfile', description : 'docker image name')
    }

    environment {
        GITLAB_USERNAME = "so-so2456"
        DOCKER_USERNAME = "homecoder"
        // NEXUS_URL = "nexus-docker.211-34-229-165.nip.io"
    }

    stages {
        stage('Git Checkout') {
            steps {
                withCredentials([string(credentialsId: 'gitlab-token', variable: 'TOKEN')]) {
                    git branch: "${params.gitlab_branch}", poll: false, url: "http://${env.GITLAB_USERNAME}:${TOKEN}@${params.gitlab_link}"
                }
            }
        }
        stage('Build Project') {
            steps {
                sh './mvnw clean package'
            }
        }
        // stage('Test Project') {
        //     steps {
        //         // sh './mvnw clean package'
        //         echo "Test Complete"
        //     }
        // }
        stage('Build and Tag Docker Image') {
            steps {
                script {
                    NEW_TAG = sh(
                        script: 'git log -1 --pretty=%h',
                        returnStdout: true
                    ).trim()
                }
                container('kaniko') {
                    sh "/kaniko/executor --verbosity debug --insecure --skip-tls-verify --dockerfile=./Dockerfile \
                    --context=dir:///home/jenkins/agent/workspace/dogfile/dogfile-deploy \
                    --destination=${env.DOCKER_USERNAME}/${params.image_name}:${NEW_TAG}"
                    // --destination=${env.NEXUS_URL}/${params.image_name}:latest" tls 해결되면 반영
                }
            }
        }
        stage("Update Chart") {
            steps {
                withCredentials([string(credentialsId: 'gitlab-token', variable: 'TOKEN')]) {
                    script {
                        MAIN_PATH = sh(script: 'pwd', returnStdout: true)
                    }
                    sh """
                        rm -rf ./helm-chart-repo
                        git clone http://${env.GITLAB_USERNAME}:${TOKEN}@${params.helm_chart}
                        cd ./${params.helm_chart_name}/${params.application_name}
                        sed -i "s|tag: .*\$|tag: \"${NEW_TAG}\"|g" ./values.yaml

                        git config --local user.email "jenkins_bot@honeyossori.com"
                        git config --local user.name "jenkins_bot"
                        
                        git add ./values.yaml
                        git commit -m "[FROM Jenkins] Container Image Tag was changed to ${NEW_TAG}"
                        git push
                        cd ${MAIN_PATH}
                    """
                }
            }
        }
    }
    // discord notifier
    post {
        success {
            withCredentials([string(credentialsId: 'discord-webhook', variable: 'DISCORD')]) {
                discordSend description: """
                title : ${currentBuild.displayName}
                result : ${currentBuild.result}
                duration : ${currentBuild.duration / 1000}s
                """,
                link: env.BUILD_URL, result: currentBuild.currentResult, 
                title: "${env.JOB_NAME} : ${currentBuild.displayName} SUCCESS", 
                webhookURL: "$DISCORD"
            }
        }
        failure {
            withCredentials([string(credentialsId: 'discord-webhook', variable: 'DISCORD')]) {
                discordSend description: """
                title : ${currentBuild.displayName}
                result : ${currentBuild.result}
                duration : ${currentBuild.duration / 1000}s
                """,
                link: env.BUILD_URL, result: currentBuild.currentResult, 
                title: "${env.JOB_NAME} : ${currentBuild.displayName} FAIL", 
                webhookURL: "$DISCORD" 
            }
        }
    }
}