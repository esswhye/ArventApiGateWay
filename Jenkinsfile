node{
    stage('Clone Sources')
    {
     checkout scm
    }

    stage('Maven build') {
            buildInfo = rtMaven.run pom: 'ArventGateWayApi/pom.xml', goals: 'clean install'
        }
  }