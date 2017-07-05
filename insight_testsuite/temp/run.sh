#!/bin/bash

javac -cp src/main/java/gson-2.8.1.jar src/main/java/*.java -d src/main/java/build

java -cp src/main/java/build:src/main/java/gson-2.8.1.jar AnomalyDetection ./log_input/batch_log.json ./log_input/stream_log.json ./log_output/flagged_purchases.json
