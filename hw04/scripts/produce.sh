#!/bin/bash
source .env
echo Write topics -------------------------------------------------------------------------------------------
docker exec -i kafka-broker kafka-console-producer --bootstrap-server $BOOTSTRAP_SERVER --topic $TOPIC_NAME
