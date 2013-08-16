#!/bin/bash

SCRIPT_DIR=$(dirname $(readlink -f $0))
source "${SCRIPT_DIR}/env.sh"

KEYSTORE_FILE="${SCRIPT_DIR}/keystore.jks"
KEYSTORE_PASSWORD="changeit"

java \
    "-Djavax.net.ssl.keyStore=$KEYSTORE_FILE" \
    "-Djavax.net.ssl.keyStorePassword=$KEYSTORE_PASSWORD" \
    -jar ${STUBBY_JAR} \
    $@

