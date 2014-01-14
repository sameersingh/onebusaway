package edu.uw.modelab.feature.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.modelab.feature.NeighborhoodService;
import edu.uw.modelab.feature.pojo.Neighborhood;

public class SeattleNeighborhoodService implements NeighborhoodService {

	private static final Logger LOG = LoggerFactory
			.getLogger(SeattleNeighborhoodService.class);

	private final Map<String, Neighborhood> neighborhoods;
	private final String filename;
	private static final String SEPARATOR = " ";

	public SeattleNeighborhoodService(final String filename) {
		this.filename = filename;
		this.neighborhoods = new LinkedHashMap<>();
	}

	public void init() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(getClass()
					.getResourceAsStream(filename)));
			String line = null;
			while ((line = br.readLine()) != null) {
				final String[] tokens = line.split(SEPARATOR);
				neighborhoods.put(
						tokens[0],
						new Neighborhood(tokens[0], Double.valueOf(tokens[1]),
								Double.valueOf(tokens[2]), Double
										.valueOf(tokens[3])));
			}
		} catch (final IOException exc) {
			LOG.error("Exception reading input file. Message {}",
					exc.getMessage());
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (final IOException e) {
				}
			}
		}
	}

	@Override
	public List<Neighborhood> getNeighborhoods() {
		return new ArrayList<Neighborhood>(neighborhoods.values());
	}

	@Override
	public Neighborhood getNeighborhoodByName(final String name) {
		return neighborhoods.get(name);
	}

	@Override
	public List<String> getNeighborhoodNames() {
		return new ArrayList<>(neighborhoods.keySet());
	}
}
