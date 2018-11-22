pipeline {
    agent any
    parameters {
        booleanParam(name: 'SKIP_TESTS', defaultValue: false, description: 'Skip tests')
        string(name: 'ALT_DEPLOYMENT_REPOSITORY', defaultValue: '', description: 'Alternative deployment repo')
    }
    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }
    stages {
        stage ('Build') {
            steps {
                withMaven(maven: 'maven', mavenSettingsConfig: 'nexus-mvn-settings') {
                    sh "mvn -DskipTests=${params.SKIP_TESTS} clean compile install"
                }
            }
        }
        stage ('Publish') {
            steps {
                script {
                    env.MVN_ARGS=""
                    if (params.ALT_DEPLOYMENT_REPOSITORY != '') {
                        env.MVN_ARGS="-DaltDeploymentRepository=${params.ALT_DEPLOYMENT_REPOSITORY}"
                    }
                    if (env.BRANCH_NAME == 'master') {
                        env.MVN_ARGS="${env.MVN_ARGS} -Possrh-deploy"
                        env.NPM_DEPLOY="true"
                    }
                }
                withMaven(maven: 'maven', mavenSettingsConfig: 'nexus-mvn-settings',
                          mavenOpts: '-DskipTests=true') {
                    sh "mvn deploy $MVN_ARGS"
                }
                nodejs(nodeJSInstallationName: 'node 10', configId: 'npm-global-config') {  catchError {
                  ansiColor('xterm') {
                    sh '''
                       [ "$NPM_DEPLOY" != "true" ] && exit 0

                       cd plancul-api/target/npm
                       npm publish --access public
                       cd ../../..

                       cd astronomy-api/target/npm
                       npm publish --access public
                       cd ../../..
                        '''
                  }
                }}
            }
        }
    }
}
