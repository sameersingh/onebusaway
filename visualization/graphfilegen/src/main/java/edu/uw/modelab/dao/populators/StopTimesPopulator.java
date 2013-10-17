package edu.uw.modelab.dao.populators;

import static edu.uw.modelab.utils.Utils.unquote;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import edu.uw.modelab.pojo.StopTime;

public class StopTimesPopulator extends AbstractPopulator {

	private static final String sql = "insert into stop_time (trip_id, arrival_time, departure_time, stop_id, stop_sequence) "
			+ "values (?, ?, ?, ?, ?)";

	private final JdbcTemplate template;

	public StopTimesPopulator(final String file, final boolean enabled,
			final DataSource dataSource) {
		super(file, enabled, ",");
		this.template = new JdbcTemplate(dataSource);
	}

	@Override
	protected void doPopulate(final List<String[]> tokens) {
		final Set<StopTimeInsertObject> stopTimesInsertObjects = new HashSet<StopTimeInsertObject>(
				tokens.size());
		CollectionUtils.forAllDo(tokens, new Closure() {
			@Override
			public void execute(final Object tokens) {
				final String[] strTokens = (String[]) tokens;
				final StopTimeInsertObject stio = new StopTimeInsertObject(
						new StopTime(unquote(strTokens[1]),
								unquote(strTokens[2]), Integer
										.valueOf(unquote(strTokens[4]))),
						Integer.valueOf(unquote(strTokens[0])), Integer
								.valueOf(unquote(strTokens[3])));
				stopTimesInsertObjects.add(stio);
			}
		});

		final List<StopTimeInsertObject> toInsert = new ArrayList<>(
				stopTimesInsertObjects);

		template.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(final PreparedStatement pss, final int i)
					throws SQLException {
				final StopTimeInsertObject stopInsertObject = toInsert.get(i);
				final StopTime stopTime = stopInsertObject.getStopTime();
				pss.setInt(1, stopInsertObject.getTripId());
				pss.setString(2, stopTime.getSchedArrivalTime());
				pss.setString(3, stopTime.getSchedDepartureTime());
				pss.setInt(4, stopInsertObject.getStopId());
				pss.setInt(5, stopTime.getStopSequence());
			}

			@Override
			public int getBatchSize() {
				return stopTimesInsertObjects.size();
			}
		});
	}

	private class StopTimeInsertObject {

		private final StopTime stopTime;
		private final int tripId;
		private final int stopId;

		StopTimeInsertObject(final StopTime stopTime, final int tripId,
				final int stopId) {
			this.stopTime = stopTime;
			this.tripId = tripId;
			this.stopId = stopId;
		}

		public StopTime getStopTime() {
			return stopTime;
		}

		public int getTripId() {
			return tripId;
		}

		public int getStopId() {
			return stopId;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = (prime * result) + getOuterType().hashCode();
			result = (prime * result) + stopId;
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
			final StopTimeInsertObject other = (StopTimeInsertObject) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (stopId != other.stopId) {
				return false;
			}
			if (tripId != other.tripId) {
				return false;
			}
			return true;
		}

		private StopTimesPopulator getOuterType() {
			return StopTimesPopulator.this;
		}

	}

}
