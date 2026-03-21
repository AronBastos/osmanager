#!/bin/bash
# Script para iniciar Docker Compose com configurações automáticas

set -e

echo "🐳 Iniciando OSManager com Docker Compose..."
echo ""

# Cores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Verificar se Docker está instalado
if ! command -v docker &> /dev/null; then
    echo -e "${RED}✗ Docker não está instalado${NC}"
    echo "  Instale Docker em: https://www.docker.com/products/docker-desktop"
    exit 1
fi

# Verificar se Docker Compose está instalado
if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}✗ Docker Compose não está instalado${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Docker e Docker Compose encontrados${NC}"
echo ""

# Verificar se .env existe
if [ ! -f .env ]; then
    if [ -f .env.example ]; then
        echo -e "${YELLOW}⚠ Arquivo .env não encontrado${NC}"
        echo "  Criando a partir de .env.example..."
        cp .env.example .env
        echo -e "${GREEN}✓ Arquivo .env criado${NC}"
        echo "  Edite conforme necessário: nano .env"
    fi
fi

echo ""
echo -e "${YELLOW}📋 Configurações:${NC}"
echo "  - MySQL Database: 3306"
echo "  - Tomcat Application: 8080"
echo "  - phpMyAdmin: 8081"
echo ""

# Perguntar se deseja fazer rebuild
read -p "Deseja reconstruir as imagens? (s/n) " -n 1 -r
echo ""

if [[ $REPLY =~ ^[Ss]$ ]]; then
    echo -e "${YELLOW}🔨 Construindo imagens...${NC}"
    docker-compose build
    echo -e "${GREEN}✓ Imagens construídas${NC}"
fi

echo ""
echo -e "${YELLOW}🚀 Iniciando containers...${NC}"
docker-compose up -d

echo ""
echo -e "${GREEN}✓ Containers iniciados!${NC}"
echo ""

# Aguardar serviços ficarem prontos
echo -e "${YELLOW}⏳ Aguardando serviços ficarem prontos...${NC}"
sleep 10

# Verificar status
echo ""
echo -e "${YELLOW}📊 Status dos containers:${NC}"
docker-compose ps

echo ""
echo -e "${GREEN}✅ Ambiente Docker iniciado com sucesso!${NC}"
echo ""
echo -e "${YELLOW}📍 URLs:${NC}"
echo "  - Aplicação: http://localhost:8080"
echo "  - phpMyAdmin: http://localhost:8081"
echo ""
echo -e "${YELLOW}📝 Próximos passos:${NC}"
echo "  - Ver logs: docker-compose logs -f"
echo "  - Parar: docker-compose down"
echo "  - Limpar: docker-compose down -v"
echo ""

