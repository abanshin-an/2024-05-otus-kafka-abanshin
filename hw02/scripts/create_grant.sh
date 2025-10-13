#!/bin/bash
source .env
docker exec kafka-broker kafka-topics --bootstrap-server $BOOTSTRAP_SERVER --command-config $ADMIN_CONFIG --create --topic $TOPIC_NAME
docker exec kafka-broker kafka-acls   --bootstrap-server $BOOTSTRAP_SERVER --command-config $ADMIN_CONFIG --add --allow-principal User:Alice --operation WRITE --topic $TOPIC_NAME
docker exec kafka-broker kafka-acls   --bootstrap-server $BOOTSTRAP_SERVER --command-config $ADMIN_CONFIG --add --allow-principal User:Bob   --operation READ  --topic $TOPIC_NAME
docker exec kafka-broker kafka-acls   --bootstrap-server $BOOTSTRAP_SERVER --command-config $ADMIN_CONFIG --add --allow-principal User:Bob   --operation READ  --operation DESCRIBE --group console-consumer
docker exec kafka-broker kafka-acls   --bootstrap-server $BOOTSTRAP_SERVER --command-config $ADMIN_CONFIG --add --allow-principal User:Bob   --operation READ  --group console-consumer
docker exec kafka-broker kafka-topics --bootstrap-server $BOOTSTRAP_SERVER --command-config $ADMIN_CONFIG --list
docker exec kafka-broker kafka-acls   --bootstrap-server $BOOTSTRAP_SERVER --command-config $ADMIN_CONFIG --list
