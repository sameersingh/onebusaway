#!/bin/sh

INFILE=$1
OUTFILE="`basename $INFILE | sed 's/\..*//'`"

# timestamp, serviceDate, tripID, distanceAlongTrip, schedDev, lat, lon
gunzip -c $INFILE | awk '{print $13"\t"$11"\t"$19"\t"$5"\t"$10"\t"$6"\t"$7}' > $OUTFILE