#!/bin/bash

KAFKA_CFG=$1
sed -i.bak "s/^KAFKA_CFG=.*$/KAFKA_CFG=$KAFKA_CFG/" .env
rm .env.bak
echo "Updated KAFKA_CFG in .env to $KAFKA_CFG"
