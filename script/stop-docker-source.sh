#!/bin/bash

#Source script for linux based dev environments. This powers the stop-docker.sh
#script in the concept projects

docker-compose -f ../../docker/docker-compose.yml -f ../../docker/docker-compose.override.yml down
