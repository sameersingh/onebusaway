package edu.uw.modelab.console;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.uw.modelab.dao.populators.BusPositionsPopulator;
import edu.uw.modelab.dao.populators.RoutesPopulator;
import edu.uw.modelab.dao.populators.StopTimesPopulator;
import edu.uw.modelab.dao.populators.StopsPopulator;
import edu.uw.modelab.dao.populators.TripsPopulator;

public class Driver {

	public Driver() {
		final ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
				"classpath:app-context.xml");
		instantiatePopulators(appContext);

		// final FileCreator stopsCreator = appContext.getBean("stopsCreator",
		// FileCreator.class);
		// stopsCreator.create();
		// final FileCreator busPositionsCreator = appContext.getBean(
		// "busPositionsCreator", FileCreator.class);
		// busPositionsCreator.create();

		// final StatsService statsService = appContext.getBean("statsService",
		// StatsService.class);
		// statsService.getStopsPerRoute();
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
