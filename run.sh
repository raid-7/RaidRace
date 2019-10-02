#!/bin/bash

if [[ "$1" == "c" ]]; then
    java -cp target/main-1.0-SNAPSHOT.jar ru.raid_7.raidrace.client.RaidRace ${@:2}
fi

if [[ "$1" == "s" ]]; then
    java -cp target/main-1.0-SNAPSHOT.jar ru.raid_7.raidrace.server.Main ${@:2}
fi
