package edu.uw.modelab.feature.impl;

import edu.uw.modelab.feature.DatasetSplitCondition;
import edu.uw.modelab.utils.Utils;

public class MonthYearDatasetSplitCondition implements DatasetSplitCondition {

	@Override
	public boolean isForTest(final long serviceDate) {
		final int month = Utils.monthOfYear(serviceDate);
		final int year = Utils.year(serviceDate);
		return (year == 2013) && ((month == 10) || (month == 9));

	}
}
