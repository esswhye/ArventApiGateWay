node{

    stage('Clone Sources')
    {
     checkout scm
    }

    stage('Maven build') {
            sh 'cd ArventApiGateWay'
            sh 'mvn clean install'
        }
  }