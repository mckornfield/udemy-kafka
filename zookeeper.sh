#!/bin/bash
CURRENT_DIR=$(dirname "$0")
mkdir -p ${CURRENT_DIR}/data/zookeeper
zookeeper-server-start ${CURRENT_DIR}/zookeeper.properties
