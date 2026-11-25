pipeline {
  agent any

  environment {
    // ---- EDIT THESE THREE LINES ----
    IMAGE        = "arpithab2025/xyz-app"
    SONAR_URL    = "http://172.31.43.210:9000"
    ANSIBLE_HOST = "ubuntu@172.31.10.164"
  }

  stages {
    stage('Build (Maven)') {
      steps { sh 'mvn -B clean package' }
    }

    stage('SonarQube Analysis') {
      steps {
        withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
          sh '''mvn -B org.sonarsource.scanner.maven:sonar-maven-plugin:3.10.0.2594:sonar \
                -Dsonar.projectKey=xyz-app \
                -Dsonar.host.url=$SONAR_URL \
                -Dsonar.token=$SONAR_TOKEN'''
        }
      }
    }

    stage('Docker Build') {
      steps { sh 'docker build -t $IMAGE:$BUILD_NUMBER -t $IMAGE:latest .' }
    }

    stage('Push to DockerHub') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'dockerhub-creds',
                         usernameVariable: 'DH_USER', passwordVariable: 'DH_PASS')]) {
          sh 'echo "$DH_PASS" | docker login -u "$DH_USER" --password-stdin'
          sh 'docker push $IMAGE:$BUILD_NUMBER'
          sh 'docker push $IMAGE:latest'
        }
      }
    }

    stage('Deploy with Ansible') {
      steps {
        sshagent(['ansible-ssh']) {
          sh '''scp -o StrictHostKeyChecking=no -r ansible $ANSIBLE_HOST:~/
                ssh -o StrictHostKeyChecking=no $ANSIBLE_HOST \
                  "ansible-playbook ~/ansible/deploy.yml -e image=$IMAGE -e tag=$BUILD_NUMBER"'''
        }
      }
    }
  }
}
