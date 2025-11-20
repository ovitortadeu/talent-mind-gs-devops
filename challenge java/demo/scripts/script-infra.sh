#!/bin/bash
# Script de Provisionamento de Infraestrutura Azure - Global Solution
# Execute este script via Azure CLI (bash)

# Variáveis - AJUSTE CONFORME NECESSÁRIO
RG_NAME="rg-talentmind-gs"
LOCATION="eastus"
ACR_NAME="acrtalentmindgs$RANDOM" # O nome deve ser único globalmente
APP_NAME="app-talentmind-java"

echo "--- Iniciando Provisionamento ---"

# 1. Criar Resource Group
echo "Criando Resource Group: $RG_NAME..."
az group create --name $RG_NAME --location $LOCATION

# 2. Criar Azure Container Registry (ACR)
echo "Criando Azure Container Registry: $ACR_NAME..."
az acr create --resource-group $RG_NAME --name $ACR_NAME --sku Basic --admin-enabled true

# 3. Login no ACR (para garantir acesso local se necessário)
# az acr login --name $ACR_NAME

echo "--- Infraestrutura Base Criada ---"
echo "Resource Group: $RG_NAME"
echo "ACR Name: $ACR_NAME"
echo "Próximos passos: Configurar a Pipeline no Azure DevOps para usar este ACR."