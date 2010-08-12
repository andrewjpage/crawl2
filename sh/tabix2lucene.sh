#!/bin/bash

VERSION=0.1

function usage {
	echo "tabix2lucene.sh -t TABIX_DIR -l LUCENE_DIR (-c CRAWL_HOME)"
	echo "Note: Set the CRAWL_HOME folder as an environment variable."
}

while getopts "t:l:c:vh" o ; do  
	case $o in  
		t ) TABIX_DIR=$OPTARG;;
		l ) LUCENE_DIR=$OPTARG;;
		c)  CRAWL_HOME=$OPTARG;;
		v ) echo $VERSION  
				exit 0;;
		?) usage
				exit;;
	esac  
done

if [[ -z $TABIX_DIR ]] || [[ -z $LUCENE_DIR ]] || [[ -z $CRAWL_HOME ]]
then
     usage
     exit 1
fi


java -server -Djava.awt.headless=true -Xmx1024m \
	-Dlog4j.configuration=file://$CRAWL_HOME/log4j.properties \
	-cp $CRAWL_HOME/crawl.jar \
	org.genedb.crawl.business.GenerateLuceneIndex \
	-t $TABIX_DIR \
	-l $LUCENE_DIR

