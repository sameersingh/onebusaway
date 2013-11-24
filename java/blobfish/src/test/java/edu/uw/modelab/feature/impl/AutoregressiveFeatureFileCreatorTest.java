package edu.uw.modelab.feature.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import edu.uw.modelab.dao.TripDao;
import edu.uw.modelab.feature.FeatureFileCreator;
import edu.uw.modelab.feature.pojo.AutoregressiveInputRow;

public class AutoregressiveFeatureFileCreatorTest {

	@Mock
	private TripDao mockTripDao;

	private FeatureFileCreator featureFileCreator;

	@Before
	public void setUp() {
		MockitoAnnotations
				.initMocks(AutoregressiveFeatureFileCreatorTest.class);
		featureFileCreator = new AutoregressiveFeatureFileCreator(mockTripDao,
				"whatever", "whatever", 2);
	}

	@Test
	public void testCreateFeatures() {
		final Map<Integer, Integer> tripRoutes = new HashMap<>();
		tripRoutes.put(26, 5);
		tripRoutes.put(28, 7);
		tripRoutes.put(32, 10);
		((AutoregressiveFeatureFileCreator) featureFileCreator)
				.setRouteIdsPerTrip(tripRoutes);

		final Map<Integer, Set<AutoregressiveInputRow>> inputMatrix = new HashMap<>();

		Set<AutoregressiveInputRow> inputRows = new TreeSet<>();
		inputRows.add(new AutoregressiveInputRow(1, 100, 500, 0));
		inputRows.add(new AutoregressiveInputRow(2, 100, 1000, -10));
		inputRows.add(new AutoregressiveInputRow(3, 100, 1000, 20));
		inputRows.add(new AutoregressiveInputRow(5, 100, 3000, -25));
		inputRows.add(new AutoregressiveInputRow(6, 100, 4000, -50));
		inputMatrix.put(26, inputRows);

		inputRows = new TreeSet<>();
		inputRows.add(new AutoregressiveInputRow(1, 100, 100, 50));
		inputRows.add(new AutoregressiveInputRow(5, 100, 200, 30));
		inputMatrix.put(28, inputRows);

		inputRows = new TreeSet<>();
		inputRows.add(new AutoregressiveInputRow(10, 100, 100, 10));
		inputRows.add(new AutoregressiveInputRow(20, 100, 200, 40));
		inputRows.add(new AutoregressiveInputRow(30, 100, 300, 0));
		inputMatrix.put(32, inputRows);

		((AutoregressiveFeatureFileCreator) featureFileCreator)
				.setInputMatrix(inputMatrix);

		// when
		featureFileCreator.createFeatures();
	}
}
