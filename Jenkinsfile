pipeline {
  agent any

  stages {

    stage("Checkout") {
      steps {
        checkout scm
      }
    }

    stage("Build & Test") {
      steps {
        sh "cd order-service && mvn -B clean test"
        sh "cd payment-service && mvn -B clean test"
        sh "cd inventory-service && mvn -B clean test"
        sh "cd notification-service && mvn -B clean test"
      }
    }

    stage("Start Infra") {
      steps {
        sh "docker compose up -d"
      }
    }

    stage("Start Services") {
      steps {
        sh """
          nohup mvn -f order-service spring-boot:run > order.log 2>&1 &
          nohup mvn -f payment-service spring-boot:run > payment.log 2>&1 &
          nohup mvn -f inventory-service spring-boot:run > inventory.log 2>&1 &
          nohup mvn -f notification-service spring-boot:run > notification.log 2>&1 &
        """
      }
    }

    stage("Smoke Test") {
      steps {
        sleep 20
        sh """
          curl -s -X POST http://localhost:8081/orders \
            -H "Content-Type: application/json" \
            -d '{"customerId":"C100","amount":150}'
        """
      }
    }
  }
}