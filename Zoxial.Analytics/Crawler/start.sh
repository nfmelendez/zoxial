mkdir -p log
mvn clean $1 install
nohup mvn $1 exec:java > /dev/null  2>&1 &
echo $! > app.pid

