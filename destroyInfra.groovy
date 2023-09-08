pipeline {
    agent any
	
    parameters {
        choice(choices: ['dev', 'prod'], name: 'envi') 
    }
    
    stages {
     	stage('Parameters verification'){
	        when{
			anyOf{
                    environment name: 'envi', value: '' 
		        }
	        }
	        steps{
			echo "$envi - $projectName"
		        input 'Empty parameter(s). Still continue ?'
	        }
    	}
      	stage('Get Repository'){
		    steps{
		        git branch: 'main', url: 'https://github.com/mhazheer/InfraDeploy.git'
		    }
	    }
        stage('Destroy Infra') {
	        steps {
	            dir("./") {
                    sh "echo $env "
		            sh "terraform fmt"
		            sh "terraform init -backend-config='bucket=bucket-efrei-devops' -backend-config='key=$envi/infra/terraform.tfstate' -backend-config='region=eu-west-1'"
		            sh "terraform destroy -var='env=$envi' -auto-approve"
	            }
	        }
        }
    }
}
