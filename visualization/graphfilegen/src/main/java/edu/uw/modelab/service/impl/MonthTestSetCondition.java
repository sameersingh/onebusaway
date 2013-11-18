package edu.uw.modelab.service.impl;

import edu.uw.modelab.service.TestSetCondition;
import edu.uw.modelab.utils.Utils;

public class MonthTestSetCondition implements TestSetCondition {

	@Override
	public boolean isForTest(final long serviceDate) {
		final int month = Utils.monthOfYear(serviceDate);
		return month == 6;

	}
}
