package edu.uw.modelab.service;

import edu.uw.modelab.pojo.Segment;
import edu.uw.modelab.pojo.Stop;
import edu.uw.modelab.pojo.TripInstance;

public interface TimeService {

	long getActualTimesDiff(TripInstance tripInstance, Segment segment);

	long getActualTime(final TripInstance tripInstance, final Stop stop);

	long getActualTime(final TripInstance tripInstance, final Segment segment);

	long getScheduledTime(final TripInstance tripInstance, final Stop stop);

	long getScheduledTimeDiff(final Stop from, final Stop to);

	long getDelay(Segment segment, TripInstance tripInstance);

}
