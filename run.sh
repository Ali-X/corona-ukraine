#!/bin/sh
ls
# Start the proxy
./cloud_sql_proxy -instances=covid-272613:us-central1:corona-database=tcp:5402 -credential_file=credentials.json&

# wait for the proxy to spin up
sleep 10

# Start the server
java -jar ./target/test/telegrambot-0.0.1-SNAPSHOT.jar