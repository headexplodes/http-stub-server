#!/bin/bash

SCRIPT_DIR=$(dirname $(readlink -f $0))
source "${SCRIPT_DIR}/env.sh"

wget -q --no-proxy -O - "${HTTP_BASE}/_control/shutdown" > /dev/null

