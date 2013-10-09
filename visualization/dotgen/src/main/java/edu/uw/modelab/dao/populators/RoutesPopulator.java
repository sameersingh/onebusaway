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

import edu.uw.modelab.pojo.Route;

public class RoutesPopulator extends AbstractPopulator {

	private static final String sql = "insert into route (id, name, agency_id) values (?, ?, ?)";

	private final JdbcTemplate template;

	public RoutesPopulator(final String file, final boolean enabled,
			final DataSource dataSource) {
		super(file, enabled);
		this.template = new JdbcTemplate(dataSource);
	}

	@Override
	protected void doPopulate(final List<String[]> tokens) {
		final List<Route> routes = new ArrayList<Route>(tokens.size());
		CollectionUtils.forAllDo(tokens, new Closure() {
			@Override
			public void execute(final Object tokens) {
				final String[] strTokens = (String[]) tokens;
				final Route route = new Route(Long
						.valueOf(unquote(strTokens[0])), unquote(strTokens[2]),
						unquote(strTokens[1]));
				routes.add(route);

			}
		});

		template.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(final PreparedStatement pss, final int i)
					throws SQLException {
				final Route route = routes.get(i);
				pss.setLong(1, route.getId());
				pss.setString(2, route.getName());
				pss.setString(3, route.getAgencyId());
			}

			@Override
			public int getBatchSize() {
				return routes.size();
			}
		});
	}
}
