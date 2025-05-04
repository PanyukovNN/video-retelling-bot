#!/bin/bash

SSH_CONFIG=nvpn
REMOTE_DIR=retelling-bot

# Команда для выполнения на сервере
REMOTE_CMD="
cd $REMOTE_DIR && \
docker compose down -v && \
APP_TAG=1.0.0-RC6 docker compose up -d
"

# Подключение и выполнение
ssh "$SSH_CONFIG" "$REMOTE_CMD"