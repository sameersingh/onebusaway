package edu.uw.modelab.feature.impl;

import edu.uw.modelab.feature.DatasetSplitCondition;
import edu.uw.modelab.utils.Utils;

public class DayOfMonthDatasetSplitCondition implements DatasetSplitCondition {

	@Override
	public boolean isForTest(final long serviceDate) {
		final int day = Utils.dayOfMonth(serviceDate);
		return day == 31;
	}

}
