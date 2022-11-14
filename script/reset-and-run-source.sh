#!/bin/bash

#Source script for linux based dev environments. This powers the reset-and-run.sh
#script in the concept projects

function exitfn (){
    echo "Early Termination"
    exit
}

banner_multi_line() {
  printf "\n"
  _animateLine "+--------------------------------------------------------------+"
  _animateLine "$(printf "| %-60s |\n" "`date`")"
  _animateLine "|                                                              |"
  for i in "${_messages[@]}"
  do
     :
     _animateLine "$(printf "|`tput bold` %-60s `tput sgr0`|\n" "$i")"
  done
  _animateLine "+--------------------------------------------------------------+"
  printf "\n"
}

_animateLine() {
  delay=${SPINNER_DELAY:-0.001}
  local line="$1"
  for (( i = 0; i < ${#line}; i++ )) ; do
    printf "${line:$i:1}"
    sleep ${delay}
  done
  printf "\n"
}

trap "exitfn" INT
artifactId=$(mvn org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=project.artifactId -q -DforceStdout)
_messages=(
  "Building App And Generating Liquibase Schema For"
  "$artifactId"
)
banner_multi_line
mvn clean install -f ../../pom.xml -pl :$artifactId -am -DskipTests
_messages=(
  "Launching Supporting Components In Docker"
  "- admingateway"
  "- adminweb"
  "- auth"
  "- commercegateway"
  "- commerceweb"
  "- kafka"
  "- solr"
  "- openapi"
  "- zookeeper"
  "- postgres"
)
banner_multi_line
docker-compose -f ../../docker/docker-compose.yml -f ../../docker/docker-compose.override.yml down
docker-compose -f ../../docker/docker-compose.yml -f ../../docker/docker-compose.override.yml pull
docker-compose -f ../../docker/docker-compose.yml -f ../../docker/docker-compose.override.yml up -d
_messages=(
  "Launching App"
)
banner_multi_line
mvn spring-boot:run -Papp,mapperCache -DskipTests -Dskip-schema=true -Dskip-reset-resources=true -Dskip-gen-spring-factories=true
