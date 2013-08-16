#!/bin/bash

SCRIPT_DIR=$(dirname $(readlink -f $0))
source "${SCRIPT_DIR}/env.sh"

M2_SETTINGS="${SCRIPT_DIR}/m2-settings.xml"

if [ ! -f ${STUBBY_JAR} ] # only download if not exists
then
    mvn -s ${M2_SETTINGS} \
        org.apache.maven.plugins:maven-dependency-plugin:2.8:get \
        -Dartifact="au.com.sensis:http-stub-server-standalone:1.0-SNAPSHOT:jar:onejar" \
        -DremoteRepositories="http://nexus.company.com/nexus/content/groups/public" \
        -Ddest=${STUBBY_JAR}
fi

