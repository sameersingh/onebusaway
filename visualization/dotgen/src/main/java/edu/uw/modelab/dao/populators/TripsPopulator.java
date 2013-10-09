package edu.uw.modelab.dao.populators;

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

	private static final String sql = "insert into trip (id, route_id) values (?, ?)";

	private final JdbcTemplate template;

	public TripsPopulator(final String file, final boolean enabled,
			final DataSource dataSource) {
		super(file, enabled);
		this.template = new JdbcTemplate(dataSource);
	}

	@Override
	protected void doPopulate(final List<String[]> tokens) {
		final List<Trip> trips = new ArrayList<Trip>(tokens.size());
		CollectionUtils.forAllDo(tokens, new Closure() {
			@Override
			public void execute(final Object tokens) {
				final String[] strTokens = (String[]) tokens;
				final Trip trip = new Trip(Long.valueOf(unquote(strTokens[2])),
						Long.valueOf(unquote(strTokens[0])));
				trips.add(trip);
			}
		});

		template.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(final PreparedStatement pss, final int i)
					throws SQLException {
				final Trip trip = trips.get(i);
				pss.setLong(1, trip.getId());
				pss.setLong(2, trip.getRouteId());
			}

			@Override
			public int getBatchSize() {
				return trips.size();
			}
		});
	}
}
