#!/bin/bash

SCRIPT_DIR=$(dirname $(readlink -f $0))

DOWNLOAD_SCRIPT="${SCRIPT_DIR}/download.sh"
RUN_SCRIPT="${SCRIPT_DIR}/run.sh"
STOP_SCRIPT="${SCRIPT_DIR}/stop.sh"

STUBBY_OUT="${SCRIPT_DIR}/target/stub-server.out" # only used when run in background
STUBBY_JAR="${SCRIPT_DIR}/target/stub-server-1.0-SNAPSHOT.jar"

HTTP_PORT=9001
HTTPS_PORT=9443

HTTP_BASE="http://localhost:${HTTP_PORT}"

