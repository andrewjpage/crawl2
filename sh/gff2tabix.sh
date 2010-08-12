#!/bin/bash

#if [ $# != 3 ]; then
#  echo "gff2tabix INPUTDIR OUTPUTDIR TMPDIR"
#  exit 1
#fi
#INDIR=$1
#OUTDIR=$2
#TMPDIR=$3

function usage {
	echo "gff2tabix.sh -i INDIR -o OUTDIR -t TMPDIR -c CRAWL_HOME"
	echo "Note: Set the CRAWL_HOME folder as an environment variable."
}

VERSION=0.1

while getopts "i:o:t:vh" o ; do  
	case $o in  
		i ) INDIR=$OPTARG;;
		o ) OUTDIR=$OPTARG;;
		t ) TMPDIR=$OPTARG;;
		c)  CRAWL_HOME=$OPTARG;;
		v ) echo $VERSION  
				exit 0;;
		?) usage
				exit;;
	esac  
done

if [[ -z $INDIR ]] || [[ -z $OUTDIR ]] || [[ -z $CRAWL_HOME ]]
then
     usage
     exit 1
fi

if [[ -z $TMPDIR ]]
then 
	TMPDIR="/tmp"
fi

# echo $INDIR $OUTDIR $TMPDIR

# set the internal field separator (IFS) to not include spaces and you'll be able to use paths with spaces in them
ORIGINAL_IFS=$IFS
IFS=$'\n'

# create the output folder
mkdir -p $OUTDIR

#create the tmp folder
RANDOMNUMBER=$[ ( $RANDOM % 100000 ) ]

INPUTTMPDIR=$TMPDIR/GFF2TABIX_$RANDOMNUMBER
mkdir -p $INPUTTMPDIR

#copy files to it
cp $INDIR/* $INPUTTMPDIR

# gunzip every GFF that is gzipped
for GZIPPEDFILE in $(find "$INPUTTMPDIR" -name *gff.gz);
do
    echo Unzipping $GZIPPEDFILE  
    gunzip $GZIPPEDFILE
done

for FILEPATH in $(find "$INPUTTMPDIR" -name *.gff);
do
    
    FILENAME=$(basename $FILEPATH)
    EXTENSION=${FILENAME##*.}
    FILENAME=${FILENAME%.*}
    
    # calculate the number of lines in the original gff
    # we use this to not give a silly number in the -A and -B flag values 
    LINES=$(wc -l < $FILEPATH | sed -e 's/^[ \t]*//')
    
    echo "$INDIR/$FILENAME.$EXTENSION ($LINES lines) ... $OUTDIR/$FILENAME.*"
    
    ## generate FASTA & annotations
    java -classpath $CRAWL_HOME/crawl.jar -Dlog4j.configuration=file://$CRAWL_HOME/log4j.properties org.genedb.crawl.business.GFFAnnotatationAndFastaExtractor $FILEPATH $OUTDIR
    
    # index the fasta sequence
    samtools faidx $OUTDIR/$FILENAME.fasta
    
    # sort the file and bgzip it
    sort -k1,1 -k4,4n $OUTDIR/$FILENAME.gff | bgzip > $OUTDIR/$FILENAME.gff.gz
    
    # remove the unzipped version
    rm $OUTDIR/$FILENAME.gff
    
    ## generate the index
    tabix -p gff -f $OUTDIR/$FILENAME.gff.gz
    
done

rm -rf $INPUTTMPDIR


