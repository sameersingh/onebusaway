package edu.uw.modelab.console;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.uw.modelab.dao.populators.RoutesPopulator;
import edu.uw.modelab.dao.populators.StopTimesPopulator;
import edu.uw.modelab.dao.populators.StopsPopulator;
import edu.uw.modelab.dao.populators.TripInstancesPopulator;
import edu.uw.modelab.dao.populators.TripsPopulator;
import edu.uw.modelab.service.FileCreator;

public class Driver {

	public Driver() {
		final ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
				"classpath:app-context.xml");
		instantiatePopulators(appContext);

		appContext.getBean("tripInstancesCreator", FileCreator.class)
				.createForTrip(21767755);
		appContext.getBean("stopsCreator", FileCreator.class).createForTrip(
				21767755);

		/*
		 * final List<Stop> stops = appContext.getBean("stopDao", StopDao.class)
		 * .getStopsByTripId(21767755); System.out.println(stops.size()); final
		 * Trip trip = appContext.getBean("tripDao", TripDao.class)
		 * .getTripById(21767755);
		 * System.out.println(trip.getSegments().size()); final
		 * List<BusPosition> busPositions = appContext.getBean(
		 * "busPositionDao", BusPositionDao.class)
		 * .getBusPositionsByTripId(trip.getId());
		 * System.out.println(busPositions.size()); new
		 * DefaultTimeEstimator().estimateArrivalTime(busPositions, trip);
		 */

	}

	private void instantiatePopulators(
			final ClassPathXmlApplicationContext appContext) {
		appContext.getBean("routesPopulator", RoutesPopulator.class);
		appContext.getBean("tripsPopulator", TripsPopulator.class);
		appContext.getBean("stopsPopulator", StopsPopulator.class);
		appContext.getBean("stopTimesPopulator", StopTimesPopulator.class);
		appContext.getBean("tripInstancesPopulator",
				TripInstancesPopulator.class);
	}

	public static void main(final String[] args) {
		new Driver();

	}
}
