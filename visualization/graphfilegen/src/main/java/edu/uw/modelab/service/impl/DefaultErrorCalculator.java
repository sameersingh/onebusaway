package edu.uw.modelab.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.modelab.dao.TripDao;
import edu.uw.modelab.pojo.Segment;
import edu.uw.modelab.pojo.Stop;
import edu.uw.modelab.pojo.Trip;
import edu.uw.modelab.pojo.TripInstance;
import edu.uw.modelab.service.ErrorCalculator;
import edu.uw.modelab.service.TimeEstimator;
import edu.uw.modelab.utils.Utils;

public class DefaultErrorCalculator implements ErrorCalculator {

	private static final Logger LOG = LoggerFactory
			.getLogger(DefaultErrorCalculator.class);

	private final Map<String, Double> yHatPerSegmentPerTripInstancePerTrip;
	private final Map<Integer, Map<Integer, Double>> errorObaPerKPerTripId;
	private final Map<Integer, Map<Integer, Double>> errorModePerKPerTripId;
	private final TripDao tripDao;
	private final TimeEstimator timeEstimator;
	private final String filename;
	private final String featureYhatFileName;

	public DefaultErrorCalculator(final String filename, final TripDao tripDao,
			final TimeEstimator timeEstimator, final String featureYhatFileName) {
		this.filename = filename;
		this.tripDao = tripDao;
		this.timeEstimator = timeEstimator;
		this.featureYhatFileName = featureYhatFileName;
		this.yHatPerSegmentPerTripInstancePerTrip = new HashMap<String, Double>();
		this.errorObaPerKPerTripId = new LinkedHashMap<>();
		this.errorModePerKPerTripId = new LinkedHashMap<>();
	}

	public void init() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(getClass()
					.getResourceAsStream(featureYhatFileName)));
			String line = null;
			while ((line = br.readLine()) != null) {
				final String[] tokens = line.split(",");
				yHatPerSegmentPerTripInstancePerTrip.put(tokens[0],
						Double.valueOf(tokens[1]));
			}
		} catch (final IOException exc) {
			LOG.error("Exception reading input file. Message {}",
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

	@Override
	public void calculateError(final int tripId) {
		final Trip trip = tripDao.getTripById(tripId);
		final Set<TripInstance> tripInstances = trip.getInstances();
		final Iterator<TripInstance> tripInstancesIt = tripInstances.iterator();
		final List<Segment> segments = new ArrayList<>(trip.getSegments());
		final StringBuilder sb = new StringBuilder();
		while (tripInstancesIt.hasNext()) {
			tripInstancesIt.next();
			for (final Segment segment : segments) {
				final String to = segment.getTo().getStopTime()
						.getSchedArrivalTime();
				final String from = segment.getFrom().getStopTime()
						.getSchedArrivalTime();
				sb.append(Utils.diff(to, from) + "\n");
			}
		}
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File(filename));
			pw.print(sb.toString());
			pw.flush();
		} catch (final FileNotFoundException exc) {
			LOG.error("Error creating oba error file. Msg {}", exc.getMessage());
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	@Override
	public void calculateError(final int tripId, final int k) {
		final Trip trip = tripDao.getTripByIdAndServiceDateLessThan(tripId,
				1378191600000L);
		final Set<TripInstance> tripInstances = trip.getInstances();
		final int numberOfTripInstances = tripInstances.size();
		final Iterator<TripInstance> tripInstancesIt = tripInstances.iterator();
		final List<Segment> segments = new ArrayList<>(trip.getSegments());
		final int numberOfSegments = segments.size();

		assert k < numberOfSegments;

		int j = 0;
		int i = k;

		double errorsOba = 0;
		double errorsMode = 0;
		while (i < numberOfSegments) {
			final long scheduledDiff = getScheduled(segments.get(i),
					segments.get(j));
			while (tripInstancesIt.hasNext()) {
				final TripInstance tripInstance = tripInstancesIt.next();
				final long actualI = getActual(segments.get(i), tripInstance);
				final long t_true = actualI;
				final long actualJ = getActual(segments.get(j), tripInstance);
				final long t_hat_oba = actualJ + (scheduledDiff * 1000);
				double delays = 0;
				for (int idx = j; idx < i; idx++) {
					final String key = Utils.label(tripInstance,
							segments.get(idx));
					final double delay = yHatPerSegmentPerTripInstancePerTrip
							.get(key);
					delays += delay;
				}
				final long roundedDelays = Math.round(delays);
				final long t_hat_mode = t_hat_oba + (roundedDelays * -1000);
				final double errOba = Math.pow((t_true - t_hat_oba) / 1000, 2);
				errorsOba += errOba;
				final double errMode = Math
						.pow((t_true - t_hat_mode) / 1000, 2);
				errorsMode += errMode;
			}
			i++;
			j++;
		}
		final int N = numberOfTripInstances * i;
		final double errorObaK = Math.sqrt(errorsOba / N);
		final double errorModeK = Math.sqrt(errorsMode / N);
		Map<Integer, Double> obaErrorMap = errorObaPerKPerTripId.get(tripId);
		if (obaErrorMap == null) {
			obaErrorMap = new LinkedHashMap<>();
			obaErrorMap.put(k, errorObaK);
			errorObaPerKPerTripId.put(tripId, obaErrorMap);
		} else {
			obaErrorMap.put(k, errorObaK);
		}

		Map<Integer, Double> modeErrorMap = errorModePerKPerTripId.get(tripId);
		if (modeErrorMap == null) {
			modeErrorMap = new LinkedHashMap<>();
			modeErrorMap.put(k, errorModeK);
			errorModePerKPerTripId.put(tripId, modeErrorMap);
		} else {
			modeErrorMap.put(k, errorModeK);
		}

		System.out.println(errorObaPerKPerTripId.get(tripId).get(k) + "\t"
				+ errorModePerKPerTripId.get(tripId).get(k));
	}

	private long getActual(final Segment segment,
			final TripInstance tripInstance) {
		long result = 0;
		final Stop stop = segment.getFrom();
		if (segment.isFirst()) {
			// assume from actual is equal to scheduled one
			final String actual = stop.getStopTime().getSchedArrivalTime();
			result = Utils.time(tripInstance.getServiceDate(), actual);
		} else {
			result = timeEstimator.actual(tripInstance, stop);
		}
		return result;
	}

	private long getScheduled(final Segment segmentI, final Segment segmentJ) {
		final String segmentISched = segmentI.getFrom().getStopTime()
				.getSchedArrivalTime();
		final String segmentJSched = segmentJ.getFrom().getStopTime()
				.getSchedArrivalTime();
		return Utils.diff(segmentISched, segmentJSched);
	}

}
