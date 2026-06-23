#!/bin/bash

set -e

DOCKER_USERNAME=$1
IMAGE_TAG=$2

IMAGE_NAME="$DOCKER_USERNAME/ideundeun-core:$IMAGE_TAG"

BLUE_NAME="core-blue"
GREEN_NAME="core-green"

BLUE_PORT=8081
GREEN_PORT=8082

NGINX_CONF="/etc/nginx/conf.d/core.conf"

echo "======================================"
echo " Core Server Blue/Green 배포 시작"
echo "======================================"
echo "[1/6] 이미지 Pull: $IMAGE_NAME"

docker pull $IMAGE_NAME

if grep -q "127.0.0.1:${BLUE_PORT}" $NGINX_CONF; then
  CURRENT_NAME=$BLUE_NAME
  CURRENT_PORT=$BLUE_PORT
  NEXT_NAME=$GREEN_NAME
  NEXT_PORT=$GREEN_PORT
  COMPOSE_FILE="docker-compose.green.yml"
else
  CURRENT_NAME=$GREEN_NAME
  CURRENT_PORT=$GREEN_PORT
  NEXT_NAME=$BLUE_NAME
  NEXT_PORT=$BLUE_PORT
  COMPOSE_FILE="docker-compose.blue.yml"
fi

echo "[2/6] 현재 운영 중: $CURRENT_NAME (port $CURRENT_PORT)"
echo "[2/6] 새 배포 대상: $NEXT_NAME (port $NEXT_PORT)"

docker rm -f $NEXT_NAME || true

echo "[3/6] $NEXT_NAME 컨테이너 시작"
DOCKER_USERNAME=$DOCKER_USERNAME IMAGE_TAG=$IMAGE_TAG docker-compose -f $COMPOSE_FILE up -d

echo "[4/6] Health check 시작 (최대 10회 / 3초 간격)"

for i in {1..10}; do
  echo "      Health check 시도 중... ($i/10)"
  if curl -sf http://localhost:$NEXT_PORT/actuator/health > /dev/null; then
    echo "      Health check 성공 ($i번째 시도)"
    break
  fi

  if [ $i -eq 10 ]; then
    echo "      Health check 실패 - EC2에서 'docker logs $NEXT_NAME' 으로 확인하세요"
    exit 1
  fi

  sleep 3
done

echo "[5/6] Nginx 트래픽 전환: $CURRENT_NAME -> $NEXT_NAME (port $CURRENT_PORT -> $NEXT_PORT)"

sudo sed -i "s/127.0.0.1:${CURRENT_PORT}/127.0.0.1:${NEXT_PORT}/g" $NGINX_CONF

sudo nginx -t
sudo systemctl reload nginx

echo "      Nginx reload 완료 - 트래픽이 $NEXT_NAME 으로 전환됐습니다"

echo "[6/6] 기존 컨테이너 제거: $CURRENT_NAME"
docker rm -f $CURRENT_NAME || true
docker image prune -f

echo "======================================"
echo " Core Server Blue/Green 배포 완료"
echo " 운영 컨테이너: $NEXT_NAME (port $NEXT_PORT)"
echo "======================================"
