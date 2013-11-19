package edu.uw.modelab.feature;

public interface DatasetSplitCondition {

	boolean isForTest(long serviceDate);

}
