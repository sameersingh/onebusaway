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
import edu.uw.modelab.service.FileCreator;

public class Driver {

	public Driver() {
		final ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
				"classpath:app-context.xml");
		instantiatePopulators(appContext);
		final List<Integer> trips = getTrips();
		// visualization(appContext, trips);
		// createFeatures(appContext, trips);
		calculateErrors(appContext, trips);
	}

	private void calculateErrors(
			final ClassPathXmlApplicationContext appContext,
			final List<Integer> trips) {
		final ErrorCalculator errorCalculator = appContext.getBean(
				"errorCalculator", ErrorCalculator.class);
		// errorCalculator.calculateObaAndModeError(trips, 1);
		for (int i = 1; i < 26; i++) {
			errorCalculator.calculateObaAndModeError(trips, i);
		}
	}

	private void createFeatures(
			final ClassPathXmlApplicationContext appContext,
			final List<Integer> trips) {
		final FeatureFileCreator featureFileCreator = appContext.getBean(
				"featureFileCreator", FeatureFileCreator.class);
		featureFileCreator.createFeatures(trips);
	}

	private List<Integer> getTrips() {
		final List<Integer> trips = Arrays.asList(new Integer[] { 21673115,
				21673118, 21670614, 21670616, 21542721, 21542723, 21672958,
				21672960, 18918481, 18919624, 21759759, 21759766, 21767755,
				23726161, 21704210, 21650159, 21650162, 20157477, 23240137,
				23240144, 23775546, 23775576, 23240059, 23240085 });
		// final List<Integer> trips = Arrays.asList(new Integer[] { 21767755
		// });
		return trips;
	}

	private void visualization(final ClassPathXmlApplicationContext appContext,
			final List<Integer> trips) {
		createStops(appContext);
		createTripInstances(appContext, trips);
	}

	private void createTripInstances(
			final ClassPathXmlApplicationContext appContext,
			final List<Integer> trips) {
		appContext.getBean("tripInstancesCreator", FileCreator.class)
				.createForTrips(trips);
	}

	private void createStops(final ClassPathXmlApplicationContext appContext) {
		appContext.getBean("stopsCreator", FileCreator.class).create();
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
