package edu.uw.modelab.service.impl;

import edu.uw.modelab.service.TestSetCondition;
import edu.uw.modelab.utils.Utils;

public class MonthYearTestSetCondition implements TestSetCondition {

	@Override
	public boolean isForTest(final long serviceDate) {
		final int month = Utils.monthOfYear(serviceDate);
		final int year = Utils.year(serviceDate);
		return (year == 2013) && ((month == 10) || (month == 9));

	}
}
