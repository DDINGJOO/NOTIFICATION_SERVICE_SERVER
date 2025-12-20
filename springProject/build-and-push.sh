#!/bin/bash

set -e

IMAGE_NAME="ddingsh9/notification-server"
VERSION="${1:-latest}"
PLATFORMS="linux/amd64,linux/arm64"
BUILDER_NAME="multiarch"

echo "============================================"
echo "  Notification Server Docker Build & Push"
echo "============================================"
echo "Image: ${IMAGE_NAME}"
echo "Version: ${VERSION}"
echo "Platforms: ${PLATFORMS}"
echo "============================================"

# Check Docker login
if ! docker info | grep -q "Username"; then
    echo "[WARN] Docker Hub login required"
    docker login
fi

# Create buildx builder (remove if corrupted)
echo "[INFO] Setting up buildx builder: ${BUILDER_NAME}"
docker buildx rm ${BUILDER_NAME} 2>/dev/null || true
docker buildx create --name ${BUILDER_NAME} --use --bootstrap

# Build and push
echo "[INFO] Building and pushing multi-platform image..."

if [ "${VERSION}" = "latest" ]; then
    docker buildx build --platform ${PLATFORMS} \
        -t ${IMAGE_NAME}:latest \
        --push .
else
    docker buildx build --platform ${PLATFORMS} \
        -t ${IMAGE_NAME}:latest \
        -t ${IMAGE_NAME}:${VERSION} \
        --push .
fi

echo "============================================"
echo "[SUCCESS] Image pushed successfully!"
echo "  - ${IMAGE_NAME}:latest"
if [ "${VERSION}" != "latest" ]; then
    echo "  - ${IMAGE_NAME}:${VERSION}"
fi
echo "============================================"

# Show image info
echo "[INFO] Image manifest:"
docker buildx imagetools inspect ${IMAGE_NAME}:latest