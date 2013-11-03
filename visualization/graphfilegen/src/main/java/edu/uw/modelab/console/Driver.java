package edu.uw.modelab.console;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.uw.modelab.dao.populators.RoutesPopulator;
import edu.uw.modelab.dao.populators.StopTimesPopulator;
import edu.uw.modelab.dao.populators.StopsPopulator;
import edu.uw.modelab.dao.populators.TripInstancesPopulator;
import edu.uw.modelab.dao.populators.TripsPopulator;
import edu.uw.modelab.service.ErrorCalculator;
import edu.uw.modelab.service.FeatureFileCreator;

public class Driver {

	public Driver() {
		final ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
				"classpath:app-context.xml");
		instantiatePopulators(appContext);

		// appContext.getBean("dotCreator", FileCreator.class).create();
		// final List<Integer> trips = Arrays.asList(new Integer[] { 21759759,
		// 21759766, 21767755, 21650159, 21650162, 23240137, 23240144,
		// 23240059, 23240085, 21650156, 20157477 });
		final List<Integer> trips = Arrays.asList(new Integer[] { 21673115,
				21673118, 21670614, 21670616, 21542721, 21542723, 21672958,
				21672960, 18918481, 18919624, 21759759, 21759766, 21767755,
				23726161, 21704210, 21650159, 21650162, 20157477, 23240137,
				23240144, 23775546, 23775576, 23240059, 23240085 });
		// final List<Integer> trips = Arrays.asList(new Integer[] { 21767755
		// });

		// appContext.getBean("stopsCreator", FileCreator.class).create();
		// appContext.getBean("tripInstancesCreator", FileCreator.class)
		// .createForTrips(trips);

		final FeatureFileCreator featureFileCreator = appContext.getBean(
				"featureFileCreator", FeatureFileCreator.class);
		featureFileCreator.createFeatures(trips);

		final ErrorCalculator errorCalculator = appContext.getBean(
				"errorCalculator", ErrorCalculator.class);

		// errorCalculator.calculateObaAndModeError(trips, 1);

		// final Trip trip = appContext.getBean("tripDao", TripDao.class)
		// .getTripById(21759759);
		// final DistanceAlongTripCalculator distanceAlongTripCalculator =
		// appContext
		// .getBean("distanceAlongTripCalculator",
		// DistanceAlongTripCalculator.class);
		// distanceAlongTripCalculator.addDistancesAlongTrip(trip);

		// errorCalculator.calculateScheduledError(21767755, Error.TEST);
		// for (int i = 1; i < 26; i++) {
		// errorCalculator.calculateObaAndModeError(trips, i);
		// }

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
