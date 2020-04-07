node{
    tools {
                maven 'Maven 3.6.3'
                jdk 'jdk8'
            }

    stage('Clone Sources')
    {

     checkout scm

     sh '''
                         echo "PATH = ${PATH}"
                         echo "M2_HOME = ${M2_HOME}"
                     '''
    }

    stage('Maven build') {
            buildInfo = rtMaven.run pom: 'ArventGateWayApi/pom.xml', goals: 'clean install'
        }
  }