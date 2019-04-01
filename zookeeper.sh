#!/bin/bash
CURRENT_DIR=$(dirname "$0")
zookeeper-server-start ${CURRENT_DIR}/zookeeper.properties
