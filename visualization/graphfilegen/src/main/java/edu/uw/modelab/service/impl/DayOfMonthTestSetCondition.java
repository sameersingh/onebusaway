package edu.uw.modelab.service.impl;

import edu.uw.modelab.service.TestSetCondition;
import edu.uw.modelab.utils.Utils;

public class DayOfMonthTestSetCondition implements TestSetCondition {

	@Override
	public boolean isForTest(final long serviceDate) {
		final int day = Utils.dayOfMonth(serviceDate);
		return day == 31;
	}

}
