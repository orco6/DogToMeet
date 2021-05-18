pipeline { 
  agent { 
      docker { 
        image 'windsekirun/jenkins-android-docker:1.1.1' 
      } 
  } 
  options { 
    // Stop the build early in case of compile or test failures 
    skipStagesAfterUnstable() 
  } 
  stages { 
    stage ('Prepare'){ 
          steps { 
            sh 'chmod +x ./gradlew'
          } 
    } 
    stage('Compile') { 
          steps { 
            sh 'ls -l'
            // Compile the app and its dependencies 
            sh './gradlew compileDebugSources' 
          } 
    } 
    stage('Build APK') { 
      steps { 
        // Finish building and packaging the APK 
        sh './gradlew assembleDebug' 
          } 
    }
     stage('Tests') {
      //Start all the existing tests in the test package 
          steps {
            sh './gradlew test --rerun-tasks'
            
                }         
    }
  }
  post {
          always{
                  sh 'find . -name "TEST-*.xml" -exec touch {} \\;'
                  junit '**/*.xml'
          }
          failure {
             mail to: 'team3sce@gmail.com',
                subject: "Status of pipeline: ${currentBuild.fullDisplayName}",
                body: "${env.BUILD_URL} has result ${currentBuild.result} and ${BUILD_URL}/consoleText"
          }
    }
}
