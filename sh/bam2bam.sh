#!/bin/bash

start_time=$(date +%s)

java -cp "build/libs/*" org.genedb.crawl.bam.BAMConverter "$@"

finish_time=$(date +%s)
echo "Time duration: $((finish_time - start_time)) secs."  