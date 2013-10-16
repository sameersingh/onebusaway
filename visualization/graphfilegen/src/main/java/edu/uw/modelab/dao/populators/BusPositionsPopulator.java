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

import edu.uw.modelab.pojo.BusPosition;

public class BusPositionsPopulator extends AbstractPopulator {

	private static final Logger LOG = LoggerFactory
			.getLogger(BusPositionsPopulator.class);

	private static final String sql = "insert into bus_position (timestamp, service_date, trip_id, distance_trip, sched_deviation, lat, lon) values (?, ?, ?, ?, ?, ?, ?)";
	private static final String sql_all_trip_ids = "select id from trip";
	private final JdbcTemplate template;

	public BusPositionsPopulator(final String file, final boolean enabled,
			final DataSource dataSource) {
		super(file, enabled, "\t");
		this.template = new JdbcTemplate(dataSource);
	}

	@Override
	protected void doPopulate(final List<String[]> tokens) {
		// in order not to get garbage here
		final List<Integer> tripIds = template.queryForList(sql_all_trip_ids,
				Integer.class);
		final Set<BusPosition> busPositions = new HashSet<BusPosition>(
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
				final BusPosition busPosition = new BusPosition(Long
						.valueOf(strTokens[0]), Long.valueOf(strTokens[1]),
						Integer.valueOf(strTokens[2]), Double
								.valueOf(strTokens[3]), Double
								.valueOf(strTokens[4]), Double
								.valueOf(strTokens[5]), Double
								.valueOf(strTokens[6]));
				busPositions.add(busPosition);
			}
		});

		final List<BusPosition> busPositionsList = new ArrayList<BusPosition>(
				busPositions);

		template.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(final PreparedStatement pss, final int i)
					throws SQLException {
				final BusPosition busPosition = busPositionsList.get(i);
				pss.setLong(1, busPosition.getTimeStamp());
				pss.setLong(2, busPosition.getServiceDate());
				pss.setLong(3, busPosition.getTripId());
				pss.setDouble(4, busPosition.getDistanceAlongTrip());
				pss.setDouble(5, busPosition.getSchedDev());
				pss.setDouble(6, busPosition.getLat());
				pss.setDouble(7, busPosition.getLon());
			}

			@Override
			public int getBatchSize() {
				return busPositions.size();
			}
		});
	}
}
