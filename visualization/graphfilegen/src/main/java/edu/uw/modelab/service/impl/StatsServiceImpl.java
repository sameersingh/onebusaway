package edu.uw.modelab.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.modelab.dao.Dao;
import edu.uw.modelab.service.StatsService;

public class StatsServiceImpl implements StatsService {

	private static final Logger LOG = LoggerFactory
			.getLogger(StatsServiceImpl.class);

	private final String filename;

	private final Dao dao;

	public StatsServiceImpl(final String filename, final Dao dao) {
		this.filename = filename;
		this.dao = dao;
	}

	@Override
	public void getStopsPerRoute() {
		LOG.info("Getting number of stops per route...");
		final Map<String, Integer> stopsPerRoute = dao.getStopsPerRoute();
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File(filename));
			final Iterator<Entry<String, Integer>> it = stopsPerRoute
					.entrySet().iterator();
			while (it.hasNext()) {
				final Entry<String, Integer> entry = it.next();
				pw.println(entry.getKey() + "\t" + entry.getValue());
			}
			pw.flush();
		} catch (final FileNotFoundException e) {
			LOG.error("Cannot write stops per route", e.getMessage());
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
		LOG.info("Done...");
	}
}
