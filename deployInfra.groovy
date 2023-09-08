pipeline {
    agent any
	
    parameters {
        choice(choices: ['dev', 'prod'], name: 'envi') 
	    string(name: 'projectName')
    }
    
    stages {
     	stage('Parameters verification'){
	        when{
			anyOf{
                    environment name: 'envi', value: '' 
		        	environment name: 'projectName', value: ''
		        }
	        }
	        steps{
			echo "$env - $projectName"
		        input 'Empty parameter(s). Still continue ?'
	        }
    	}
      	stage('Get Repository'){
		    steps{
		        git branch: 'main', url: 'https://github.com/mhazheer/InfraDeploy.git'
		    }
	    }
        stage('Build Infra') {
	        steps {
	            dir("./") {
                    sh "echo $env - $projectName"
		            sh "terraform fmt"
		            sh "terraform init -backend-config='bucket=bucket-efrei-devops' -backend-config='key=$envi/infra/terraform.tfstate' -backend-config='region=eu-west-1'"
		            sh "terraform apply -var='env=$envi' -auto-approve"
	            }
	        }
        }
    }
}
