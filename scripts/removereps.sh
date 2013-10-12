#!/bin/sh

INFILE=$1
OUTFILE="`basename $INFILE | sed 's/\..*//'`"

awk '!x[$0]++' $INFILE > $OUTFILE