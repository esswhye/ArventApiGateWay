node{
    stage('Clone Sources')
    {

     checkout scm

     sh '''
                         echo "PATH = ${PATH}"
                         echo "M2_HOME = ${M2_HOME}"
                     '''
    }

    tools {
            maven 'Maven 3.6'
            jdk 'jdk8'
        }



    stage('Maven build') {
            buildInfo = rtMaven.run pom: 'ArventGateWayApi/pom.xml', goals: 'clean install'
        }
  }