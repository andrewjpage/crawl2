#!/bin/bash

if [[ -z $CRAWL_HOME ]]
then
     echo You must set the CRAWL_HOME environment variable
     exit 1
fi

for arg in "$@"; do
    exec="$exec $arg"
done

java -server -Djava.awt.headless=true -Xmx1024m \
	-Dlog4j.configuration=file://$CRAWL_HOME/log4j.properties \
	-cp $CRAWL_HOME/crawl.jar \
	org.genedb.crawl.business.GenerateLuceneOrganism  \
	$exec

