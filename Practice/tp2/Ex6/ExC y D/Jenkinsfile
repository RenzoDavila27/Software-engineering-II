pipeline {
    agent any

    environment {
        SERVER_USER = 'root'                      // Usuario en tu VPS
        SERVER_HOST = '66.97.42.224'             // IP o dominio del VPS
        PROJECT_PATH = '/root/DeployDonWeb/gimnasio_prueba' // Ruta del proyecto en el VPS
        GIT_URL = 'https://github.com/FacundoLella/gimnasio_prueba.git'
    }

    stages {

        stage('Deploy remoto con Docker Compose') {
            steps {
                sshagent(['server-ssh-cred']) {
                    sh """
                        ssh -p 5184 -o StrictHostKeyChecking=no $SERVER_USER@$SERVER_HOST '
                            # Clonar repo si no existe
                            if [ ! -d "$PROJECT_PATH" ]; then
                                echo "📦 Clonando repositorio..."
                                git clone $GIT_URL $PROJECT_PATH
                            else
                                echo "🔄 Actualizando repositorio existente..."
                                cd $PROJECT_PATH
                                git reset --hard
                                git clean -fd
                                git pull
                            fi

                            cd $PROJECT_PATH

                            # Reconstruir imagen Docker y levantar contenedores
                            echo "⚙️ Reconstruyendo imagen Docker y levantando contenedores..."
                            docker compose build --no-cache
                            docker compose up -d
                        '
                    """
                }
            }
        }
    }

    post {
        success {
            echo '✅ Deploy completado con éxito, servicios levantados correctamente.'
        }
        failure {
            echo '❌ Falló el deploy. Revisa los logs.'
        }
    }
}