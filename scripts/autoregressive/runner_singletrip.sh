#!/bin/bash

DUMPS_PATH=/projects/onebusaway/data/block_location_records/puget_sound_prod/log-201[2\|3]*.gz
for f in $DUMPS_PATH
do
  ./singletrip.sh 21767755 $f
done

exit 0

