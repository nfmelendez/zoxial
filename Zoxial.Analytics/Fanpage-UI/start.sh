mkdir -p log
mvn clean install
nohup mvn $1 jetty:run > log/logging.log  2>&1 &
echo $! > app.pid

