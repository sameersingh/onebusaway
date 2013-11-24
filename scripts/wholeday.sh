#!/bin/bash

INFILE=$1
OUTFILE="`basename $INFILE | sed 's/\..*//'`"

# timestamp, serviceDate, tripID, distanceAlongTrip, schedDev
gunzip -c $INFILE | awk '{print $13"\t"$11"\t"$19"\t"$5"\t"$10}' > $OUTFILE