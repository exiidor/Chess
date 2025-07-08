#!/bin/bash
cd ../chess/
USE_SSL=true \
KEYSTORE_PATH=/home/azureuser/certificates/example.com.p12 \
KEYSTORE_PASSWORD=123456 \
./gradlew chess-server:run
