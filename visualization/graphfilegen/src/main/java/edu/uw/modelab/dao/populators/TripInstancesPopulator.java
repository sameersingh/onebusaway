package edu.uw.modelab.dao.populators;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import edu.uw.modelab.pojo.TripInstance;
import edu.uw.modelab.utils.EllipticalMercator;

public class TripInstancesPopulator extends BulkPopulator {

	private static final Logger LOG = LoggerFactory
			.getLogger(TripInstancesPopulator.class);

	private static final String SQL = "insert into trip_instance (timestamp, service_date, trip_id, distance_trip, sched_deviation, lat, lon, y, x) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String SQL_ALL_TRIP_IDS = "select id from trip";
	private final JdbcTemplate template;

	public TripInstancesPopulator(final String folder, final boolean enabled,
			final DataSource dataSource) {
		super(folder, enabled, "\t");
		this.template = new JdbcTemplate(dataSource);
	}

	@Override
	protected void doPopulate(final List<String[]> tokens) {
		// in order not to get garbage here
		final List<Integer> tripIds = template.queryForList(SQL_ALL_TRIP_IDS,
				Integer.class);
		final Set<TripInstance> tripInstances = new HashSet<TripInstance>(
				tokens.size());
		CollectionUtils.forAllDo(tokens, new Closure() {
			@Override
			public void execute(final Object tokens) {
				final String[] strTokens = (String[]) tokens;
				try {
					final Integer tripId = Integer.valueOf(strTokens[2]);
					if (!tripIds.contains(tripId)) {
						LOG.warn("Trip id is not in the database, discarding register...");
						return;
					}
				} catch (final Exception exc) {
					LOG.warn("Trip id cannot be converted to integer, discarding register...");
					return;
				}
				final double lat = Double.valueOf(strTokens[5]);
				final double lon = Double.valueOf(strTokens[6]);
				final double y = EllipticalMercator.mercY(lat);
				final double x = EllipticalMercator.mercX(lon);
				final TripInstance busPosition = new TripInstance(Long
						.valueOf(strTokens[0]), Long.valueOf(strTokens[1]),
						Integer.valueOf(strTokens[2]), Double
								.valueOf(strTokens[3]), Double
								.valueOf(strTokens[4]), lat, lon, y, x);
				tripInstances.add(busPosition);
			}
		});

		final List<TripInstance> tripInstanceList = new ArrayList<TripInstance>(
				tripInstances);

		template.batchUpdate(SQL, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(final PreparedStatement pss, final int i)
					throws SQLException {
				final TripInstance tripInstance = tripInstanceList.get(i);
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
}
