mkdir -p log
mvn clean $1 install
nohup mvn $1 exec:java > ./log/logging.log  2>&1 &
echo $! > app.pid

