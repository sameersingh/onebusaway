package edu.uw.modelab.feature.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.modelab.dao.TripDao;
import edu.uw.modelab.feature.FeatureFileCreator;
import edu.uw.modelab.feature.pojo.AutoregressiveInputRow;
import edu.uw.modelab.feature.pojo.AutoregressiveOutputRow;

public class AutoregressiveFeatureFileCreator implements FeatureFileCreator {

	private static final Logger LOG = LoggerFactory
			.getLogger(AutoregressiveFeatureFileCreator.class);

	private final TripDao tripDao;
	private final String tripsFilename;
	private final String preprocessedFile;
	private Map<Integer, Integer> routeIdsPerTrip;
	private Map<Integer, Set<AutoregressiveInputRow>> inputMatrix;
	private final List<Integer> tripIds;
	private final int modelOrder;
	private final String outputFile;

	public AutoregressiveFeatureFileCreator(final TripDao tripDao,
			final String tripsFilename, final String preprocessFile,
			final int modelOrder, final String outputFile) {
		this.tripDao = tripDao;
		this.tripsFilename = tripsFilename;
		this.preprocessedFile = preprocessFile;
		this.modelOrder = modelOrder;
		this.tripIds = new ArrayList<>();
		this.inputMatrix = new HashMap<>();
		this.outputFile = outputFile;
	}

	void setRouteIdsPerTrip(final Map<Integer, Integer> routeIdsPerTrip) {
		this.routeIdsPerTrip = routeIdsPerTrip;
	}

	void setInputMatrix(
			final Map<Integer, Set<AutoregressiveInputRow>> inputMatrix) {
		this.inputMatrix = inputMatrix;
	}

	public void init() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(tripsFilename)));
			String line = null;
			while ((line = br.readLine()) != null) {
				final double dLine = Double.valueOf(line);
				tripIds.add((int) dLine);
			}
			this.routeIdsPerTrip = tripDao.getTripRoutes(tripIds);
		} catch (final IOException exc) {
			LOG.error("Exception reading trips file. Message {}",
					exc.getMessage());
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (final IOException e) {
				}
			}
		}
		try {
			br = new BufferedReader(new FileReader(new File(preprocessedFile)));
			String line = null;
			while ((line = br.readLine()) != null) {
				parseLine(line);
			}
		} catch (final IOException exc) {
			LOG.error("Exception reading preprocessed file. Message {}",
					exc.getMessage());
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (final IOException e) {
				}
			}
		}
	}

	private void parseLine(final String line) {
		final String[] tokens = line.split("\t");
		final double timestamp = Double.valueOf(tokens[0]);
		final double serviceDate = Double.valueOf(tokens[1]);
		final double distance = Double.valueOf(tokens[3]);
		final int schedDeviation = Integer.valueOf(tokens[4]);
		final AutoregressiveInputRow inputRow = new AutoregressiveInputRow(
				(long) timestamp, (long) serviceDate, distance, schedDeviation);
		final double tId = Double.valueOf(tokens[2]);
		final int tripId = (int) tId;
		Set<AutoregressiveInputRow> inputRows = inputMatrix.get(tripId);
		if (inputRows == null) {
			inputRows = new TreeSet<>();
			inputRows.add(inputRow);
			inputMatrix.put(tripId, inputRows);
		} else {
			inputRows.add(inputRow);
		}
	}

	@Override
	public void createFeatures() {
		dropTripsIfLessEntriesThanOrder();
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File(outputFile));
			final Set<Integer> keys = inputMatrix.keySet();
			for (final Integer key : keys) {
				final List<AutoregressiveOutputRow> outputRows = createOutputRowsForTrip(
						inputMatrix.get(key), key);
				for (final AutoregressiveOutputRow outputRow : outputRows) {
					pw.println(outputRow.toString());
				}
			}
		} catch (final FileNotFoundException e) {
			LOG.error("Error creating output file. Msg {}", e.getMessage());
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	private List<AutoregressiveOutputRow> createOutputRowsForTrip(
			final Set<AutoregressiveInputRow> inputRows, final int tripId) {
		final List<AutoregressiveOutputRow> outputRows = new ArrayList<>();
		final List<AutoregressiveInputRow> inputRowsList = new ArrayList<>(
				inputRows);
		int i = inputRows.size() - 1;
		while (i >= modelOrder) {
			final AutoregressiveOutputRow outputRow = new AutoregressiveOutputRow();
			outputRow.setXt(inputRowsList.get(i).getSchedDeviation());
			outputRow.setDistanceAlongTrip(inputRowsList.get(i).getDistance());
			// outputRow.setRouteId(routeIdsPerTrip.get(tripId));
			int j = i - 1;
			final List<Integer> previousXts = outputRow.getPreviousXts();
			while (previousXts.size() < modelOrder) {
				outputRow.addPreviousXts(inputRowsList.get(j)
						.getSchedDeviation());
				j--;
			}
			outputRows.add(outputRow);
			i--;
		}
		return outputRows;
	}

	private void dropTripsIfLessEntriesThanOrder() {
		final Set<Integer> keys = inputMatrix.keySet();
		final List<Integer> keysToRemove = new ArrayList<>();
		for (final Integer key : keys) {
			if (inputMatrix.get(key).size() < (modelOrder + 1)) {
				keysToRemove.add(key);
			}
		}
		for (final Integer key : keysToRemove) {
			inputMatrix.remove(key);
		}
	}

	@Override
	public void createFeatures(final int tripId) {
		createFeatures();
	}

	@Override
	public void createFeatures(final List<Integer> tripIds) {
		createFeatures();
	}

}
