package edu.uw.modelab.console;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.uw.modelab.dao.TripDao;
import edu.uw.modelab.dao.impl.populators.RoutesPopulator;
import edu.uw.modelab.dao.impl.populators.StopTimesPopulator;
import edu.uw.modelab.dao.impl.populators.StopsPopulator;
import edu.uw.modelab.dao.impl.populators.TripInstancesPopulator;
import edu.uw.modelab.dao.impl.populators.TripsPopulator;
import edu.uw.modelab.error.pojo.Dataset;
import edu.uw.modelab.error.pojo.RootMeanSquareError;
import edu.uw.modelab.feature.FeatureFileCreator;
import edu.uw.modelab.pojo.Trip;
import edu.uw.modelab.service.ErrorService;
import edu.uw.modelab.visualization.VisualizationFileCreator;

public class Driver {

	private static final Logger LOG = LoggerFactory.getLogger(Driver.class);

	public Driver() {
		final ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
				"classpath:app-context.xml");
		final long start = System.currentTimeMillis();
		instantiatePopulators(appContext);
		final List<Integer> tripIds = getTripIds(appContext);
		// visualization(appContext, tripIds);
		// createFeatures(appContext, tripIds);
		calculateErrors(appContext, tripIds);
		final long end = System.currentTimeMillis();
		LOG.info("Time spent: " + ((end - start) / 1000));
	}

	private void calculateErrors(
			final ClassPathXmlApplicationContext appContext,
			final List<Integer> tripIds) {
		final ErrorService errorService = appContext.getBean("errorService",
				ErrorService.class);
		final Set<Trip> trips = appContext.getBean("tripDao", TripDao.class)
				.getTripsIn(tripIds);
		for (int i = 1; i < 26; i++) {
			final Map<Dataset, RootMeanSquareError> errors = errorService
					.getErrors(trips, i);
			System.out.println(errors.get(Dataset.TEST));
		}
	}

	private void createFeatures(
			final ClassPathXmlApplicationContext appContext,
			final List<Integer> trips) {
		final FeatureFileCreator featureFileCreator = appContext.getBean(
				"featureFileCreator", FeatureFileCreator.class);
		featureFileCreator.createFeatures(trips);
	}

	private List<Integer> getTripIds(
			final ClassPathXmlApplicationContext appContext) {
		// final List<Integer> trips = Arrays.asList(new Integer[] { 21673115,
		// 21673118, 21670614, 21670616, 21542721, 21542723, 21672958,
		// 21672960, 18918481, 18919624, 21759759, 21759766, 21767755,
		// 23726161, 21704210, 21650159, 21650162, 20157477, 23240137,
		// 23240144, 23775546, 23775576, 23240059, 23240085 });
		// final List<Integer> trips = Arrays.asList(new Integer[] { 21767755
		// });
		final List<Integer> trips = appContext
				.getBean("tripDao", TripDao.class).getTripIds(100);
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
		appContext.getBean("tripInstancesCreator",
				VisualizationFileCreator.class).createForTrips(trips);
	}

	private void createStops(final ClassPathXmlApplicationContext appContext) {
		appContext.getBean("stopsCreator", VisualizationFileCreator.class)
				.create();
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
