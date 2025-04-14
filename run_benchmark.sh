#!/bin/bash

set -x

MODULE_TARGET_DIR=json-rules-benchmarks/target

if [ $1 == "-prof" ]; then
  java -jar $MODULE_TARGET_DIR/benchmarks.jar -wi 10 -i 5 -f 1 -prof "async:libPath=$HOME/async-profiler/lib/libasyncProfiler.dylib;interval=100000;event=itimer;output=flamegraph;dir=benchmark-results"
else
  java -jar $MODULE_TARGET_DIR/benchmarks.jar -wi 10 -i 5 -f 1 -gc true
fi
