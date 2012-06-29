mvn clean install
nohup mvn jetty:run > log/logging.log &
echo $! > app.pid

