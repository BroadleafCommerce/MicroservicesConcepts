#!/bin/bash

function exitfn (){
    echo "Early Termination"
    exit
}

trap "exitfn" INT
rm -rf ./src/main/resources/db
rm -rf ./src/main/resources/application*
printf "\n"
printf "***** Building App And Generating Schema *****"
printf "\n"
cd ../../
mvn clean install
printf "\n"
printf "***** Launching Supporting Components *****"
printf "\n"
cd docker
docker-compose down
docker-compose pull
docker-compose up -d
cd ../concepts/productExtensionOnly
printf "\n"
printf "***** Launching App *****"
printf "\n"
mvn spring-boot:run -Papp
