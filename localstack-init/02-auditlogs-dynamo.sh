#!/bin/bash

echo "======================================"
echo "Creando tabla DynamoDB para Audit Logs"
echo "======================================"
awslocal dynamodb create-table \
  --table-name AuditLogs \
  --attribute-definitions AttributeName=logId,AttributeType=S \
  --key-schema AttributeName=logId,KeyType=HASH \
  --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5
