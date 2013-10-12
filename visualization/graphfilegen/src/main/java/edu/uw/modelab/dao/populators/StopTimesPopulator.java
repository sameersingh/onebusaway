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

import edu.uw.modelab.pojo.StopTime;

public class StopTimesPopulator extends AbstractPopulator {

	private static final String sql = "insert into stop_time (trip_id, stop_id, stop_sequence) values (?, ?, ?)";

	private final JdbcTemplate template;

	public StopTimesPopulator(final String file, final boolean enabled,
			final DataSource dataSource) {
		super(file, enabled, ",");
		this.template = new JdbcTemplate(dataSource);
	}

	@Override
	protected void doPopulate(final List<String[]> tokens) {
		final List<StopTime> stopTimes = new ArrayList<StopTime>(tokens.size());
		CollectionUtils.forAllDo(tokens, new Closure() {
			@Override
			public void execute(final Object tokens) {
				final String[] strTokens = (String[]) tokens;
				final StopTime stopTime = new StopTime(Integer
						.valueOf(unquote(strTokens[0])), Integer
						.valueOf(unquote(strTokens[4])), Integer
						.valueOf(unquote(strTokens[3])));
				stopTimes.add(stopTime);
			}
		});

		template.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(final PreparedStatement pss, final int i)
					throws SQLException {
				final StopTime stopTime = stopTimes.get(i);
				pss.setInt(1, stopTime.getTripId());
				pss.setInt(2, stopTime.getStopId());
				pss.setInt(3, stopTime.getStopSequence());
			}

			@Override
			public int getBatchSize() {
				return stopTimes.size();
			}
		});
	}

}
