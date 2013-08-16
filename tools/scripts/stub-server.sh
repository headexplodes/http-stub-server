#!/bin/bash

SCRIPT_DIR=$(dirname $(readlink -f $0))
source "${SCRIPT_DIR}/env.sh"

MAX_WAIT_SECONDS=4 # time to wait for stubby to start and stop

function is_running_check {
    wget -q --no-proxy -O - "${HTTP_BASE}/_control/responses" > /dev/null
    STUBBY_STATUS=$?
}

function wait_for_stop {
    local NEXT_WAIT_TIME=0
    is_running_check
    until [ $STUBBY_STATUS -ne 0 ] || [ $NEXT_WAIT_TIME -eq $MAX_WAIT_SECONDS ]; do
        sleep $(( NEXT_WAIT_TIME++ ))
        is_running_check
    done
    if [ $NEXT_WAIT_TIME -eq $MAX_WAIT_SECONDS ]; then
        echo "ERROR: Timeout waiting for stubby to stop"
        exit 1
    else 
        echo "Stubby is stopped"
    fi
}

function wait_for_start {
    local NEXT_WAIT_TIME=0
    is_running_check
    until [ $STUBBY_STATUS -eq 0 ] || [ $NEXT_WAIT_TIME -eq $MAX_WAIT_SECONDS ]; do
        sleep $(( NEXT_WAIT_TIME++ ))
        is_running_check
    done
    if [ $NEXT_WAIT_TIME -eq $MAX_WAIT_SECONDS ]; then
        echo "ERROR: Timeout waiting for stubby to start"
        exit 
    else 
        echo "Stubby is running at: ${HTTP_BASE}"
    fi
}

ERROR=0

COMMAND="$1"
INSTANCE="$2"
ARGV="$@"

if [ "$COMMAND" == "" ]; then
    echo "Enter command:"
    echo "1. To start stubby in the background, please type: ./stubby.sh start"
    echo "3. To run stubby in this console, please type: ./stubby.sh run"
    echo "2. To stop stubby, please type: ./stubby.sh stop"
    echo 
    is_running_check
    exit
fi

case ${COMMAND} in
start)
    ${DOWNLOAD_SCRIPT} # download in the foreground
    echo "Starting stubby..."    
    echo "Redirecting output to: ${STUBBY_OUT}" 
    nohup ${RUN_SCRIPT} ${HTTP_PORT} ${HTTPS_PORT} > ${STUBBY_OUT} &
    ERROR=$?
    wait_for_start
    ;;
run)
    ${DOWNLOAD_SCRIPT}
    ${RUN_SCRIPT} ${HTTP_PORT} ${HTTPS_PORT}
    ERROR=$?
    ;;
stop)
    echo "Stopping stubby..."
    ${STOP_SCRIPT} ${HTTP_PORT}
    ERROR=$?
    wait_for_stop
    ;;
status)
    is_running_check
    if [ $STUBBY_STATUS -eq 0 ]; then
        echo "Stubby is running at: ${HTTP_BASE}"
    else
        echo "Stubby is not running"
    fi
    ERROR=$STUBBY_STATUS
    ;;
*)
    ${APACHE_COMMAND} ${COMMAND}
    ERROR=$?
esac

exit $ERROR

