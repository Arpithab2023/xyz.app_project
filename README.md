# xyz.app_project

Tiny Java 17 web app used for the CI/CD on AWS hands-on lab.

Run locally (JDK 17 + Maven):

    mvn clean package
    java -jar target/xyz-app.jar     # open http://localhost:8080

Before using the pipeline, replace in `Jenkinsfile` and `ansible/deploy.yml`:
YOUR_DOCKERHUB_USER, SONAR_PRIVATE_IP, ANSIBLE_PRIVATE_IP.
