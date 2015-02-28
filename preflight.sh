#!/bin/sh
findbugs-algs4 ./src/*.class
checkstyle-algs4 ./src/*.java
zip puzzle-submit.zip ./src/*.java