#!/bin/bash

function Stop(){
  #ps aux | grep java | grep wxb | grep jar | grep -v grep | awk '{print $2}' | xargs kill -9
  lsof -i:8081 | grep java | awk '{print $2}' | xargs kill -9
}

function Start(){
  mkdir log
  nohup java -jar target/springBootDemo-*.jar >> log/start.log &
}

function Package(){
  mvn clean && mvn package -Dmaven.test.skip=true -P dev
}

function Deploy(){
  git pull origin master;
  Package;
  Stop;
  Start;
}

case $1 in
  stop)
    Stop
  ;;
  start)
    Start
  ;;
  package)
    Package
  ;;
  deploy)
    Deploy
  ;;
  *)
    Deploy
  ;;
esac





