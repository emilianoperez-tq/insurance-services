#!/bin/bash
echo "======================================"
echo "Inicializando LocalStack"
echo "======================================"

sleep 5

# ==========================================
# S3 BUCKETS
# ==========================================
echo ""
echo "======================================"
echo "Creando S3 Buckets"
echo "======================================"

echo "Creando bucket: insurance-documents"
awslocal s3 mb s3://insurance-documents
awslocal s3api put-bucket-cors --bucket insurance-documents --cors-configuration '{
  "CORSRules": [
    {
      "AllowedOrigins": ["*"],
      "AllowedMethods": ["GET", "PUT", "POST", "DELETE"],
      "AllowedHeaders": ["*"],
      "MaxAgeSeconds": 3000
    }
  ]
}'

echo "Creando bucket: insurance-claims"
awslocal s3 mb s3://insurance-claims
awslocal s3api put-bucket-cors --bucket insurance-claims --cors-configuration '{
  "CORSRules": [
    {
      "AllowedOrigins": ["*"],
      "AllowedMethods": ["GET", "PUT", "POST", "DELETE"],
      "AllowedHeaders": ["*"],
      "MaxAgeSeconds": 3000
    }
  ]
}'

# ==========================================
# SECRETS MANAGER
# ==========================================
echo ""
echo "======================================"
echo "Creando Secrets en AWS Secrets Manager"
echo "======================================"

# 1. PostgreSQL Database Credentials (usado por todos los servicios)
echo "Creando secreto: prod/database/postgres"
awslocal secretsmanager create-secret \
    --name prod/database/postgres \
    --description "PostgreSQL database credentials" \
    --secret-string '{
        "username": "admin",
        "password": "admin123",
        "host": "postgres",
        "port": "5432",
        "database": "insurance_db",
        "url": "jdbc:postgresql://postgres:5432/insurance_db"
    }'

# 2. Keycloak Credentials (auth-service)
echo "Creando secreto: prod/keycloak/auth"
awslocal secretsmanager create-secret \
    --name prod/keycloak/auth \
    --description "Keycloak authentication credentials" \
    --secret-string '{
        "server_url": "http://keycloak:8080/realms/insurance-service/protocol/openid-connect/token",
        "realm": "insurance-service",
        "client_id": "insurance-service-microservice",
        "client_secret": "J71q9dYayYRnrxS7A728Z57dd7YEdSwq",
        "admin_username": "admin",
        "admin_password": "admin123"
    }'

# 3. RabbitMQ Credentials (notification-service, claim-service)
echo "Creando secreto: prod/rabbitmq/credentials"
awslocal secretsmanager create-secret \
    --name prod/rabbitmq/credentials \
    --description "RabbitMQ credentials" \
    --secret-string '{
        "host": "rabbitmq",
        "port": "5672",
        "username": "admin",
        "password": "admin123",
        "management_port": "15672"
    }'

# 4. Email Service Credentials (notification-service)
echo "Creando secreto: prod/email/smtp"
awslocal secretsmanager create-secret \
    --name prod/email/smtp \
    --description "SMTP email service credentials" \
    --secret-string '{
        "host": "smtp.gmail.com",
        "port": "587",
        "username": "your-email@gmail.com",
        "password": "your-app-password-here",
        "from": "noreply@insurance.com"
    }'

# 5. Cloudinary Credentials (claim-service)
echo "Creando secreto: prod/cloudinary/api"
awslocal secretsmanager create-secret \
    --name prod/cloudinary/api \
    --description "Cloudinary API credentials" \
    --secret-string '{
        "cloud_name": "your-cloud-name",
        "api_key": "your-api-key",
        "api_secret": "your-api-secret",
        "secure": "true"
    }'

# 6. AWS/LocalStack Credentials (document-service)
echo "Creando secreto: prod/aws/s3"
awslocal secretsmanager create-secret \
    --name prod/aws/s3 \
    --description "AWS S3 credentials and configuration" \
    --secret-string '{
        "region": "us-east-1",
        "access_key_id": "test",
        "secret_access_key": "test",
        "endpoint": "http://localstack:4566",
        "bucket_documents": "insurance-documents",
        "bucket_claims": "insurance-claims"
    }'

# 7. JWT Secret (para servicios que generen tokens propios)
echo "Creando secreto: prod/jwt/signing"
awslocal secretsmanager create-secret \
    --name prod/jwt/signing \
    --description "JWT signing key" \
    --secret-string '{
        "secret": "mySuper$ecretJWTKey2024!ForProductionUseOnly",
        "expiration": "3600000",
        "issuer": "insurance-system",
        "algorithm": "HS512"
    }'

# ==========================================
# VERIFICACIÓN
# ==========================================
echo ""
echo "======================================"
echo "Recursos creados exitosamente"
echo "======================================"

echo ""
echo "S3 Buckets:"
awslocal s3 ls

echo ""
echo "Secrets Manager:"
awslocal secretsmanager list-secrets --query 'SecretList[*].[Name,Description]' --output table

echo ""
echo "======================================"
echo "Inicialización completada"
echo "======================================"
echo ""
echo "Para obtener un secreto, usa:"
echo "awslocal secretsmanager get-secret-value --secret-id prod/database/postgres"
echo ""
