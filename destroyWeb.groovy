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
		        input 'Empty parameter(s). Still continue ?'
	        }
    	}
      	stage('Get Repository'){
            steps{
                git branch: 'main', url: 'https://github.com/mhazheer/DeployWeb.git'
		    }
	    }
      	stage('Destroy Web') {
            steps {
                dir("./") {
                    sh "echo $envi "
                	 
                    sh "terraform fmt"                  
                    sh "terraform init -backend-config='bucket=bucket-efrei-devops' -backend-config='key=$envi/web/terraform.tfstate' -backend-config='region=eu-west-1'"
                    sh "terraform destroy -var='env=$envi' -auto-approve -lock=false"
                }
            }
        }
    }
    
}
