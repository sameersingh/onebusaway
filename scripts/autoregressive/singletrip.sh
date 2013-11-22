#!/bin/bash

TRIPID=$1
INFILE=$2
OUTFILE="`basename $INFILE | sed 's/\..*//'`"

# timestamp, serviceDate, tripID, distanceAlongTrip, schedDev
gunzip -c $INFILE | awk -v t=$TRIPID '{if ($19 == t) print $13"\t"$11"\t"$19"\t"$5"\t"$10}' > $OUTFILE

