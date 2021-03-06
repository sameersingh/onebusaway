package edu.uw.modelab.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.uw.modelab.pojo.Stop;
import edu.uw.modelab.visualization.VisualizationFileCreator;

public class D3StopsCreatorTest {

	private VisualizationFileCreator fileCreator;

	// private Dao mockDao;

	@Before
	public void setUp() {
		// mockDao = Mockito.mock(Dao.class);
		// fileCreator = new D3StopsCreator("test_stops.json", mockDao);
	}

	private Map<String, List<Integer>> stopsPerRoute() {
		final Map<String, List<Integer>> stopsPerRoutes = new HashMap<String, List<Integer>>();
		final List<Integer> stops32bus = new ArrayList<Integer>();
		stops32bus.add(9978);
		stops32bus.add(29130);
		stops32bus.add(25200);
		stops32bus.add(25210);
		stops32bus.add(75406);
		stops32bus.add(75403);
		stops32bus.add(75405);
		stops32bus.add(9138);
		stops32bus.add(26365);
		stops32bus.add(29421);
		stops32bus.add(26370);
		stops32bus.add(26380);
		stops32bus.add(26390);
		stopsPerRoutes.put("32", stops32bus);

		final List<Integer> stops98bus = new ArrayList<Integer>();
		stops98bus.add(26700);
		stops98bus.add(26702);
		stops98bus.add(26641);
		stops98bus.add(26645);
		stops98bus.add(26665);
		stops98bus.add(1619);
		stops98bus.add(1630);
		stopsPerRoutes.put("98", stops98bus);
		return stopsPerRoutes;
	}

	private List<Stop> stops() {
		final List<Stop> stops = new ArrayList<Stop>();
		/*
		 * Stop stop = new Stop(1619, "WESTLAKE AVE & VIRGINIA ST",
		 * "47.6148643", "-122.33786"); stops.add(stop); stop = new Stop(1630,
		 * "WESTLAKE AVE & OLIVE WAY", "47.6132812", "-122.337547");
		 * stops.add(stop); stop = new Stop(26641, "WESTLAKE AVE N & MERCER ST",
		 * "47.6243477", "-122.338547"); stops.add(stop); stop = new Stop(26645,
		 * "WESTLAKE AVE N & HARRISON ST", "47.6213799", "-122.33857");
		 * stops.add(stop); stop = new Stop(26665, "WESTLAKE AVE & 9TH AVE",
		 * "47.6176071", "-122.338425"); stops.add(stop); stop = new Stop(26700,
		 * "FAIRVIEW AVE N & WARD ST", "47.6276665", "-122.332367");
		 * stops.add(stop); stop = new Stop(26702,
		 * "SLU STREETCAR & TERRY AVE N", "47.6259689", "-122.336533");
		 * stops.add(stop); stop = new Stop(9978,
		 * "SAND PT WAY NE & 40TH AVE NE", "47.6624718", "-122.285477");
		 * stops.add(stop); stop = new Stop(29130, "NE 45TH ST & 36TH AVE NE",
		 * "47.66119", "-122.28923"); stops.add(stop); stop = new Stop(25200,
		 * "NE 45TH ST & UNION BAY PL NE", "47.6612816", "-122.293724");
		 * stops.add(stop); stop = new Stop(25210,
		 * "MONTLAKE BLVD NE & NE 45TH ST", "47.6610413", "-122.298904");
		 * stops.add(stop); stop = new Stop(75406,
		 * "STEVENS WAY & PEND OREILLE RD", "47.6568756", "-122.304909");
		 * stops.add(stop); stop = new Stop(75403, "STEVENS WAY & BENTON LN",
		 * "47.6543655", "-122.305214"); stops.add(stop); stop = new Stop(75405,
		 * "GRANT LN & STEVENS WAY", "47.6551018", "-122.310921");
		 * stops.add(stop); stop = new Stop(9138,
		 * "NE CAMPUS PKWY & 12TH AVE NE", "47.6562996", "-122.31546");
		 * stops.add(stop); stop = new Stop(26365, "NE 40TH ST & 7TH AVE NE",
		 * "47.6559639", "-122.320801"); stops.add(stop); stop = new Stop(29421,
		 * "NE 40TH ST & LATONA AVE NE", "47.6556091", "-122.326523");
		 * stops.add(stop); stop = new Stop(26370, "N 40TH ST & EASTERN AVE N",
		 * "47.6556282", "-122.329376"); stops.add(stop); stop = new Stop(26380,
		 * "N 40TH ST & CORLISS AVE N", "47.6556473", "-122.331772");
		 * stops.add(stop); stop = new Stop(26390, "N 40TH ST & MERIDIAN AVE N",
		 * "47.6556931", "-122.334183"); stops.add(stop);
		 */
		return stops;
	}

	@Test
	public void testCreate() {
		/*
		 * final Map<String, List<Integer>> stopsPerRoutes = stopsPerRoute();
		 * Mockito
		 * .when(mockDao.getStopIdsPerRoute()).thenReturn(stopsPerRoutes); final
		 * List<Stop> stops = stops();
		 * Mockito.when(mockDao.getStops()).thenReturn(stops);
		 */
		fileCreator.create();
	}
}
