#!/bin/bash
CURRENT_DIR=$(dirname "$0")
kafka-server-start ${CURRENT_DIR}/server.properties
