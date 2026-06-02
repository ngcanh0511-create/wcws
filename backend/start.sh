#!/bin/sh
java -Dserver.port=${PORT:-8080} -jar build/libs/*.jar