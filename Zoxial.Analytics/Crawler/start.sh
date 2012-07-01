mkdir -p log
mvn clean install
nohup mvn exec:java > /dev/null &
echo $! > app.pid

