pipeline {
    agent any

    parameters {
        choice(choices: ['dev', 'prod'], name: 'envi')
        string(name: 'port')
        string(name: 'projectName')
        string(defaultValue: 'https://github.com/cloudacademy/static-website-example', name: 'repoAppli')
    }

    stages {
        stage('Parameters verification'){
            when{
                anyOf{
                    environment name: 'envi', value: '' 
                    environment name: 'port', value: ''
                    environment name: 'projectName', value: ''
                    environment name: 'repoAppli', value: ''
                }
    	    }
            steps{
                input 'Empty parameter(s). Still continue ?'
            }
        }
        stage('get repository'){
            steps{
                git branch: 'main', url: 'https://github.com/ZenxB/Build_AMI.git'
            }
        }
        stage('Build AMI') {
            steps {
                dir("./") {
                    sh "echo $envi - $repoAppli - $port - $projectName"
                    sh "packer build -var 'env=$envi' -var 'app_repo=$repoAppli' -var 'app_port=$port' -var 'app_name=$projectName' BuildAMI.json"
                }
            }
        }
    }

}
