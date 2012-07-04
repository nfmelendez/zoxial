mkdir -p log
mvn clean install
nohup mvn $1 exec:java > /dev/null  2>&1 &
echo $! > app.pid

