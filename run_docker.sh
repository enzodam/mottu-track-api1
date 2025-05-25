#!/bin/bash

# Defina o nome da imagem e do container
IMAGE_NAME="mottu-track-api"
CONTAINER_NAME="mottu-track-api-container"

# Pare e remova o container existente, se houver
echo "Parando e removendo container antigo (se existir)..."
docker stop $CONTAINER_NAME || true
docker rm $CONTAINER_NAME || true

# Remova a imagem antiga, se houver (opcional, descomente se desejar sempre reconstruir do zero)
# echo "Removendo imagem antiga (se existir)..."
# docker rmi $IMAGE_NAME || true

# Construa a imagem Docker
echo "Construindo a imagem Docker..."
docker build -t $IMAGE_NAME .

# Verifique se a construção da imagem foi bem-sucedida
if [ $? -ne 0 ]; then
    echo "Erro ao construir a imagem Docker. Abortando."
    exit 1
fi

# Execute o container em background
echo "Executando o container em background..."
docker run -d -p 8080:8080 --name $CONTAINER_NAME $IMAGE_NAME

# Verifique se o container iniciou corretamente
if [ $? -ne 0 ]; then
    echo "Erro ao iniciar o container Docker. Verifique os logs."
    exit 1
fi

echo "Container $CONTAINER_NAME iniciado com sucesso."
echo "Acesse a API em http://localhost:8080 (ou o IP da sua VM)"
echo "Para ver os logs: docker logs $CONTAINER_NAME"
echo "Para parar o container: docker stop $CONTAINER_NAME"

