#!/bin/bash

start_time=$(date +%s)

java -cp ant/packages/crawl.jar:ant/classes/:etc:. org.genedb.crawl.bam.BAMConverter "$@"

finish_time=$(date +%s)
echo "Time duration: $((finish_time - start_time)) secs."  