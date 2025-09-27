#!/bin/bash
source .env
docker exec kafka-broker kafka-topics --bootstrap-server $BOOTSTRAP_SERVER --command-config $ADMIN_CONFIG --create --topic topic1
docker exec kafka-broker kafka-topics --bootstrap-server $BOOTSTRAP_SERVER --command-config $ADMIN_CONFIG --create --topic topic2

docker exec kafka-broker kafka-topics --bootstrap-server $BOOTSTRAP_SERVER --command-config $ADMIN_CONFIG --list
docker exec kafka-broker kafka-acls   --bootstrap-server $BOOTSTRAP_SERVER --command-config $ADMIN_CONFIG --list
