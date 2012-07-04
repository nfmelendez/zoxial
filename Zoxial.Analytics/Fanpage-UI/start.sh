mkdir -p log
mvn $1 clean install
nohup mvn $1 jetty:run > log/logging.log  2>&1 &
echo $! > app.pid

