package edu.uw.modelab.service;

import edu.uw.modelab.pojo.Segment;
import edu.uw.modelab.pojo.TripInstance;

public interface DelayCalculator {

	long calculateDelay(Segment segment, TripInstance tripInstance);

}
