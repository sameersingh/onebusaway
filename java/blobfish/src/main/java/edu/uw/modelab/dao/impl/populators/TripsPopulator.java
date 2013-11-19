package edu.uw.modelab.dao.impl.populators;

import static edu.uw.modelab.utils.Utils.unquote;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import edu.uw.modelab.pojo.Trip;

public class TripsPopulator extends AbstractPopulator {

	private static final String sql = "insert into trip (id, route_id, headsign) values (?, ?, ?)";

	private final JdbcTemplate template;

	public TripsPopulator(final String file, final boolean enabled,
			final DataSource dataSource) {
		super(file, enabled, ",");
		this.template = new JdbcTemplate(dataSource);
	}

	@Override
	protected void doPopulate(final List<String[]> tokens) {
		final List<TripInsertObject> tripInsertObjects = new ArrayList<TripInsertObject>(
				tokens.size());
		CollectionUtils.forAllDo(tokens, new Closure() {
			@Override
			public void execute(final Object tokens) {
				final String[] strTokens = (String[]) tokens;
				final TripInsertObject tio = new TripInsertObject(new Trip(
						Integer.valueOf(unquote(strTokens[2])),
						unquote(strTokens[3])), Integer
						.valueOf(unquote(strTokens[0])));
				tripInsertObjects.add(tio);
			}
		});

		template.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(final PreparedStatement pss, final int i)
					throws SQLException {
				final TripInsertObject tio = tripInsertObjects.get(i);
				final Trip trip = tio.getTrip();
				pss.setInt(1, trip.getId());
				pss.setInt(2, tio.getRouteId());
				pss.setString(3, trip.getHeadSign());
			}

			@Override
			public int getBatchSize() {
				return tripInsertObjects.size();
			}
		});
	}

	private class TripInsertObject {
		private final Trip trip;
		private final int routeId;

		public TripInsertObject(final Trip trip, final int routeId) {
			this.trip = trip;
			this.routeId = routeId;
		}

		public Trip getTrip() {
			return trip;
		}

		public int getRouteId() {
			return routeId;
		}

	}
}
