package edu.uw.modelab.console;

import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.uw.modelab.dao.BusPositionDao;
import edu.uw.modelab.dao.StopDao;
import edu.uw.modelab.dao.TripDao;
import edu.uw.modelab.dao.populators.BusPositionsPopulator;
import edu.uw.modelab.dao.populators.RoutesPopulator;
import edu.uw.modelab.dao.populators.StopTimesPopulator;
import edu.uw.modelab.dao.populators.StopsPopulator;
import edu.uw.modelab.dao.populators.TripsPopulator;
import edu.uw.modelab.pojo.BusPosition;
import edu.uw.modelab.pojo.Stop;
import edu.uw.modelab.pojo.Trip;

public class Driver {

	public Driver() {
		final ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
				"classpath:app-context.xml");
		instantiatePopulators(appContext);

		final List<Stop> stops = appContext.getBean("stopDao", StopDao.class)
				.getStopsByTripId(21767755);
		System.out.println(stops.size());
		final Trip trip = appContext.getBean("tripDao", TripDao.class)
				.getTripById(21767755);
		final List<BusPosition> busPositions = appContext.getBean(
				"busPositionDao", BusPositionDao.class)
				.getBusPositionsByTripId(trip.getId());
		// new TimeEstimatorImpl().estimateTime(busPositions, stops);

	}

	private void instantiatePopulators(
			final ClassPathXmlApplicationContext appContext) {
		appContext.getBean("routesPopulator", RoutesPopulator.class);
		appContext.getBean("tripsPopulator", TripsPopulator.class);
		appContext.getBean("stopsPopulator", StopsPopulator.class);
		appContext.getBean("stopTimesPopulator", StopTimesPopulator.class);
		appContext
				.getBean("busPositionsPopulator", BusPositionsPopulator.class);
	}

	public static void main(final String[] args) {
		new Driver();

	}
}
