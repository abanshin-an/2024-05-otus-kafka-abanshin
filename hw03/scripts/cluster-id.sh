#!/bin/bash

CLUSTER_ID=$(openssl rand -base64 16 | tr '+/' '-_' | tr -d '=' | cut -c1-22)

sed -i.bak "s/^CLUSTER_ID=.*$/CLUSTER_ID=$CLUSTER_ID/" .env
rm .env.bak
echo "Updated CLUSTER_ID in .env to $CLUSTER_ID"
