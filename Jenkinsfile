node{

    stage('Clone Sources')
    {
     checkout scm
    }

    stage('Maven build') {
            sh 'mvn clean install'
        }
  }