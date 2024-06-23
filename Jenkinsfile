pipeline {
    agent any
    tools {
        //jdk 'JDK'  Asegúrate de tener configurada una instalación de JDK en Jenkins
        gradle 'GRADLE' // Asegúrate de tener configurada una instalación de Gradle en Jenkins
    }
    environment {
        ANDROID_HOME = '/usr/lib/android-sdk' // Asegúrate de que esta ruta sea correcta y accesible
        PATH = "${ANDROID_HOME}/tools:${ANDROID_HOME}/platform-tools:${PATH}"
    }

    stages {
        stage('Preparation') {
            steps {
                // Clonar el repositorio
                git url: 'https://github.com/josebiton/Test_Front-ShopMarket.git', branch: 'main'
                // Otorga permisos de ejecución al archivo gradlew
            }
        }
        // Compila los test y los ejecuta
        stage('Testing the code') {
            steps {
                sh 'chmod +x ./gradlew'
                sh './gradlew test'
                echo 'Unit Test successfully'
            }
        }
        // Revisa la calidad de código con SonarQube
        /*stage('Analysis SonarQube') {
            steps {
                sh './gradlew sonar -Dsonar.login=squ_b2f1d63b7b4fbb6179a37c3fb1ccbd457316d343 -Dsonar.projectKey=app-la-panca -Dsonar.projectName="LaPancaProject" -Dsonar.host.url=http://192.168.18.224:9000'
                echo 'SonarQube Code review done'
            }
        }*/
    }
}
