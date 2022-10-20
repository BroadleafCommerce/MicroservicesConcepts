#!/bin/bash

function exitfn (){
    echo "Early Termination"
    exit
}

trap "exitfn" INT
printf "\n"
printf "***** Building App And Generating Schema *****"
printf "\n"
cd ../../
mvn clean install -pl :broadleaf-concept-product-extension-complex-json -am  -D skipTests
printf "\n"
printf "***** Launching Supporting Components *****"
printf "\n"
cd docker
docker-compose down
docker-compose pull
docker-compose up -d
cd ../concepts/productExtensionComplexJson
printf "\n"
printf "***** Launching App *****"
printf "\n"
mvn spring-boot:run -Papp
