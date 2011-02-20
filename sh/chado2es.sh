#!/bin/bash

java -cp ant/packages/crawl.jar:ant/classes/:etc:. org.genedb.crawl.elasticsearch.index.sql.IncrementalSQLIndexBuilder "$@"  