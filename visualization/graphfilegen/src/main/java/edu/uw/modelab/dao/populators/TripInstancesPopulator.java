package edu.uw.modelab.dao.populators;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import edu.uw.modelab.utils.EllipticalMercator;
import edu.uw.modelab.utils.Threshold;

public class TripInstancesPopulator extends BulkPopulator {

	private static final Logger LOG = LoggerFactory
			.getLogger(TripInstancesPopulator.class);

	private static final String SQL = "insert into trip_instance (timestamp, service_date, trip_id, distance_trip, sched_deviation, lat, lon, y, x) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String SQL_ALL_TRIP_IDS = "select id from trip";
	private final JdbcTemplate template;
	private final Map<Integer, Map<String, Threshold>> preprocessors;

	public TripInstancesPopulator(final String folder, final boolean enabled,
			final DataSource dataSource,
			final Map<Integer, Map<String, Threshold>> preprocessors) {
		super(folder, enabled, "\t");
		this.template = new JdbcTemplate(dataSource);
		this.preprocessors = preprocessors;
	}

	@Override
	protected void doPopulate(final List<String[]> tokens) {
		// in order not to get garbage here
		final List<Integer> tripIds = template.queryForList(SQL_ALL_TRIP_IDS,
				Integer.class);
		final Set<TripInstanceInsertObject> tripInstances = new HashSet<TripInstanceInsertObject>(
				tokens.size());
		CollectionUtils.forAllDo(tokens, new Closure() {
			@Override
			public void execute(final Object tokens) {
				final String[] strTokens = (String[]) tokens;
				final Integer tripId;
				try {
					tripId = Integer.valueOf(strTokens[2]);
					if (!tripIds.contains(tripId)) {
						LOG.warn("Trip id is not in the database, discarding register...");
						return;
					}
				} catch (final Exception exc) {
					LOG.warn("Trip id cannot be converted to integer, discarding register...");
					return;
				}
				try {
					final double schedDeviation = Double.valueOf(strTokens[4]);
					if ((schedDeviation < -2000) || (schedDeviation > 2000)) {
						LOG.warn(
								"Ignoring register, scheduled deviation is {} ",
								schedDeviation);
						return;
					}

					final double distanceAlongTrip = Double
							.valueOf(strTokens[3]);
					final Map<String, Threshold> preprocessor = preprocessors
							.get(tripId);
					if (preprocessor != null) {
						final Threshold distanceAlongTripThreshold = preprocessor
								.get("DAT");

						if ((distanceAlongTrip < distanceAlongTripThreshold
								.getMin())
								|| (distanceAlongTrip > distanceAlongTripThreshold
										.getMax())) {
							LOG.warn(
									"Ignoring register, beginning or end of trip - distance {} ",
									distanceAlongTrip);
							// return;
						}
					} else {
						// at least remove the start of trips
						// if (distanceAlongTrip < 100) {
						// LOG.warn(
						// "Ignoring register, beginning of trip - distance {} ",
						// distanceAlongTrip);
						// return;
						// }
					}

					// do this per route
					final double lat = Double.valueOf(strTokens[5]);
					final double lon = Double.valueOf(strTokens[6]);
					final double y = EllipticalMercator.mercY(lat);
					final double x = EllipticalMercator.mercX(lon);
					final TripInstanceInsertObject tiio = new TripInstanceInsertObject(
							Long.valueOf(strTokens[0]), Long
									.valueOf(strTokens[1]), tripId,
							distanceAlongTrip, schedDeviation, lat, lon, y, x);
					tripInstances.add(tiio);
				} catch (final Exception ex) {
					LOG.error("Discarding register, unexpected exception",
							ex.getMessage());
				}
			}
		});

		final List<TripInstanceInsertObject> tripInstanceList = new ArrayList<TripInstanceInsertObject>(
				tripInstances);

		template.batchUpdate(SQL, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(final PreparedStatement pss, final int i)
					throws SQLException {
				final TripInstanceInsertObject tripInstance = tripInstanceList
						.get(i);
				pss.setLong(1, tripInstance.getTimeStamp());
				pss.setLong(2, tripInstance.getServiceDate());
				pss.setLong(3, tripInstance.getTripId());
				pss.setDouble(4, tripInstance.getDistanceAlongTrip());
				pss.setDouble(5, tripInstance.getSchedDev());
				pss.setDouble(6, tripInstance.getLat());
				pss.setDouble(7, tripInstance.getLon());
				pss.setDouble(8, tripInstance.getY());
				pss.setDouble(9, tripInstance.getX());
			}

			@Override
			public int getBatchSize() {
				return tripInstances.size();
			}
		});
	}

	private class TripInstanceInsertObject {

		private final long timeStamp;
		private final long serviceDate;
		private final int tripId;
		private final double distanceAlongTrip;
		private final double schedDev;
		private final double lat;
		private final double lon;
		private final double y;
		private final double x;

		public TripInstanceInsertObject(final long timeStamp,
				final long serviceDate, final int tripId,
				final double distanceAlongTrip, final double schedDev,
				final double lat, final double lon, final double y,
				final double x) {
			this.timeStamp = timeStamp;
			this.serviceDate = serviceDate;
			this.tripId = tripId;
			this.distanceAlongTrip = distanceAlongTrip;
			this.schedDev = schedDev;
			this.lat = lat;
			this.lon = lon;
			this.y = y;
			this.x = x;
		}

		public long getTimeStamp() {
			return timeStamp;
		}

		public long getServiceDate() {
			return serviceDate;
		}

		public int getTripId() {
			return tripId;
		}

		public double getDistanceAlongTrip() {
			return distanceAlongTrip;
		}

		public double getSchedDev() {
			return schedDev;
		}

		public double getLat() {
			return lat;
		}

		public double getLon() {
			return lon;
		}

		public double getY() {
			return y;
		}

		public double getX() {
			return x;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = (prime * result) + getOuterType().hashCode();
			result = (prime * result)
					+ (int) (serviceDate ^ (serviceDate >>> 32));
			result = (prime * result) + (int) (timeStamp ^ (timeStamp >>> 32));
			result = (prime * result) + tripId;
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final TripInstanceInsertObject other = (TripInstanceInsertObject) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (serviceDate != other.serviceDate) {
				return false;
			}
			if (timeStamp != other.timeStamp) {
				return false;
			}
			if (tripId != other.tripId) {
				return false;
			}
			return true;
		}

		private TripInstancesPopulator getOuterType() {
			return TripInstancesPopulator.this;
		}

	}
}
