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

import edu.uw.modelab.pojo.Stop;

public class StopsPopulator extends AbstractPopulator {

	private static final String sql = "insert into stop (id, name, lat, lon) values (?, ?, ?, ?)";

	private final JdbcTemplate template;

	public StopsPopulator(final String file, final boolean enabled,
			final DataSource dataSource) {
		super(file, enabled);
		this.template = new JdbcTemplate(dataSource);
	}

	@Override
	protected void doPopulate(final List<String[]> tokens) {
		final List<Stop> stops = new ArrayList<Stop>(tokens.size());
		CollectionUtils.forAllDo(tokens, new Closure() {
			@Override
			public void execute(final Object tokens) {
				final String[] strTokens = (String[]) tokens;
				final Stop stop = new Stop(Long.valueOf(unquote(strTokens[0])),
						unquote(strTokens[2]), unquote(strTokens[4]),
						unquote(strTokens[5]));
				stops.add(stop);
			}
		});

		template.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(final PreparedStatement pss, final int i)
					throws SQLException {
				final Stop stop = stops.get(i);
				pss.setLong(1, stop.getId());
				pss.setString(2, stop.getName());
				pss.setString(3, stop.getLat());
				pss.setString(4, stop.getLon());
			}

			@Override
			public int getBatchSize() {
				return stops.size();
			}
		});
	}
}
