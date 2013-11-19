package edu.uw.modelab.feature.impl;

import edu.uw.modelab.feature.DatasetSplitCondition;
import edu.uw.modelab.utils.Utils;

public class MonthDatasetSplitCondition implements DatasetSplitCondition {

	@Override
	public boolean isForTest(final long serviceDate) {
		final int month = Utils.monthOfYear(serviceDate);
		return month == 6;

	}
}
