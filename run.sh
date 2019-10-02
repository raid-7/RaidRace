#!/bin/bash

cd "$(dirname "$0")"

usage() {
    echo "Usage:

Run client
    c host [port=25565]
or run server
    s [port=25565] [num_of_players=2]"
    exit 1
}


if (( $# == 0 )); then
    usage
fi

if [[ "$1" == "c" ]]; then
    # host port
    if (( $# == 1 )); then
        usage
    fi
    java -cp target/main-1.0-SNAPSHOT.jar ru.raid_7.raidrace.client.RaidRace ${@:2}
fi

if [[ "$1" == "s" ]]; then
    # port players map
    java -cp target/main-1.0-SNAPSHOT.jar ru.raid_7.raidrace.server.Main ${@:2}
fi
