#!/bin/bash
source .env
echo User Alice ============================================================================================
echo List topics -------------------------------------------------------------------------------------------
docker exec kafka-broker kafka-topics --list --bootstrap-server $BOOTSTRAP_SERVER --command-config $ALICE_CONFIG
echo Write topics -------------------------------------------------------------------------------------------
docker exec -i kafka-broker kafka-console-producer --bootstrap-server $BOOTSTRAP_SERVER --producer.config $ALICE_CONFIG --topic $TOPIC_NAME
echo Read topics -------------------------------------------------------------------------------------------
docker exec -i kafka-broker kafka-console-consumer --bootstrap-server $BOOTSTRAP_SERVER --consumer.config $ALICE_CONFIG --topic $TOPIC_NAME --max-messages 1 --group console-consumer --from-beginning

echo User Bob ============================================================================================
docker exec kafka-broker kafka-topics --list --bootstrap-server $BOOTSTRAP_SERVER --command-config $BOB_CONFIG
echo Write topics -------------------------------------------------------------------------------------------
docker exec -i kafka-broker kafka-console-producer --bootstrap-server $BOOTSTRAP_SERVER --producer.config $BOB_CONFIG --topic $TOPIC_NAME
echo Read topics -------------------------------------------------------------------------------------------
docker exec -i kafka-broker kafka-console-consumer --bootstrap-server $BOOTSTRAP_SERVER --consumer.config $BOB_CONFIG --topic $TOPIC_NAME  --max-messages 1 --group console-consumer --from-beginning

echo User Bill ============================================================================================
docker exec kafka-broker kafka-topics --list --bootstrap-server $BOOTSTRAP_SERVER --command-config $BILL_CONFIG
echo Write topics -------------------------------------------------------------------------------------------
docker exec -i kafka-broker kafka-console-producer --bootstrap-server $BOOTSTRAP_SERVER --producer.config $BILL_CONFIG --topic $TOPIC_NAME
echo Read topics -------------------------------------------------------------------------------------------
docker exec -i kafka-broker kafka-console-consumer --bootstrap-server $BOOTSTRAP_SERVER --consumer.config $BILL_CONFIG --topic $TOPIC_NAME  --max-messages 1 --group console-consumer --from-beginning

