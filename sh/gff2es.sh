#!/bin/bash

java -Xmx1500m -cp ant/packages/crawl.jar:ant/classes/:etc:. org.genedb.crawl.elasticsearch.index.gff.GFFIndexBuilder "$@"

#function usage {
#	echo "gff2tabix.sh -f INDIR -t TMPDIR "
#}
#
#TMPDIR="/tmp"
#
#while getopts ":f:t:h" o ; do  
#	case $o in  
#		f ) shift 2; FILE=$OPTARG;;
#		t ) shift 2; TMPDIR=$OPTARG;;
#	esac  
#done
#
#create the tmp folder
#RANDOMNUMBER=$[ ( $RANDOM % 100000 ) ]
#INPUTTMPDIR=$TMPDIR/GFF2TABIX_$RANDOMNUMBER
#mkdir -p $INPUTTMPDIR
#
#if [ -d $FILE ]
#then
	#copy files to it
#	cp $FILE/* $INPUTTMPDIR
#
#elif [ -f $FILE ]
#then
	#copy file to it
#	cp $FILE $INPUTTMPDIR
#	
#else 
#	echo $FILE is not a file of a folder
#	exit 1
#fi
#
#
#
# gunzip every GFF that is gzipped
#for GZIPPEDFILE in $(find "$INPUTTMPDIR" -name *gff.gz);
#do
#    echo Unzipping $GZIPPEDFILE  
#    gunzip $GZIPPEDFILE
#done
#
#for FILEPATH in $(find "$INPUTTMPDIR" -name *.gff);
#do
#    
#    FILENAME=$(basename $FILEPATH)
#    EXTENSION=${FILENAME##*.}
#    FILENAME=${FILENAME%.*}
#    
#    echo $FILEPATH
#    
#	java -cp ant/packages/crawl.jar:ant/classes/:etc:. org.genedb.crawl.elasticsearch.index.gff.GFFIndexBuilder -g $FILEPATH "$@"     
#done
#
#
#rm -rf $INPUTTMPDIR

