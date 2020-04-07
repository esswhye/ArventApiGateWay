node{

    stage('Clone Sources')
    {
     checkout scm
    }

    stage("Unit Test & Build") {
              //An Example
              sh 'mvn test'
              sh 'mvn clean install'
              sh 'docker build -t arvent-gateway:B${BUILD_NUMBER} -f DockerfileTest .'


            }
    stage("Integration Test") {
          try {

            sh "docker rm -f arvent-gateway || true"
            sh "docker run -d -p 80:8080 --name=arvent-gateway --network arvent_backend arvent-gateway:B${BUILD_NUMBER}"
          }
          catch(e) {
            error "Integration Test failed"
          }finally {
            sh "docker rm -f arvent-gateway || true"
            sh "docker ps -aq | xargs docker rm || true"
            sh "docker images -aq -f dangling=true | xargs docker rmi || true "
          }
        }

  }