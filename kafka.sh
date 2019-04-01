#!/bin/bash
CURRENT_DIR=$(dirname "$0")
mkdir -p ${CURRENT_DIR}/data/kafka
kafka-server-start ${CURRENT_DIR}/server.properties
