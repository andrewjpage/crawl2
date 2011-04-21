#!/bin/bash

start_time=$(date +%s)

java -Xmx1500m -cp "build/libs/*" org.genedb.crawl.elasticsearch.index.das.DASIndexBuilder "$@"

finish_time=$(date +%s)
echo "Time duration: $((finish_time - start_time)) secs."  
