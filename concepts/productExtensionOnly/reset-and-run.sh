#!/bin/bash

rm -rf ./src/main/resources/db
rm -rf ./src/main/resources/application*
printf "\n"
printf "***** Building App And Generating Schema *****"
printf "\n"
mvn clean install -Pschema
printf "\n"
printf "***** Launching Supporting Components *****"
printf "\n"
cd ../../docker
docker-compose down
docker-compose pull
docker-compose up -d
cd ../concepts/productExtensionOnly
printf "\n"
printf "***** Launching App *****"
printf "\n"
mvn spring-boot:run -Papp
