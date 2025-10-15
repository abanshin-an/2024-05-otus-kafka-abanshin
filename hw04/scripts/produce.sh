#!/bin/bash
source .env
echo Write topics -------------------------------------------------------------------------------------------
docker exec -i $CONTAINER_NAME kafka-console-producer --bootstrap-server $BOOTSTRAP_SERVER --topic $TOPIC_NAME --property parse.key=true --property key.separator=:
