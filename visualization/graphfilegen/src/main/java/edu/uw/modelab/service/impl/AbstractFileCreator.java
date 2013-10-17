package edu.uw.modelab.service.impl;

import java.io.IOException;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.modelab.service.FileCreator;

public abstract class AbstractFileCreator implements FileCreator {

	private static final Logger LOG = LoggerFactory
			.getLogger(AbstractFileCreator.class);

	private final String filename;

	public AbstractFileCreator(final String filename) {
		this.filename = filename;
	}

	@Override
	public void create() {
		PrintWriter pw = null;
		try {
			LOG.info("Creating output file...");
			pw = new PrintWriter(filename, "UTF-8");
			beginning(pw);
			addNodes(pw);
			addEdges(pw);
			end(pw);
			pw.flush();
			LOG.info("Output file created ...");
		} catch (final IOException exc) {
			LOG.error("Error creating output file. Msg {}", exc.getMessage());
		} finally {
			if (pw != null) {
				pw.close();
			}
		}

	}

	@Override
	public void createForTrip(final int tripId) {
		PrintWriter pw = null;
		try {
			LOG.info("Creating output file for trip {}", tripId);
			pw = new PrintWriter(filename, "UTF-8");
			beginning(pw);
			addNodes(pw, tripId);
			addEdges(pw, tripId);
			end(pw);
			pw.flush();
			LOG.info("Output file created for trip {}", tripId);
		} catch (final IOException exc) {
			LOG.error("Error creating output file for trip {}. Msg {}", tripId,
					exc.getMessage());
		} finally {
			if (pw != null) {
				pw.close();
			}
		}

	}

	@Override
	public void createForTripInstance(final int tripId, final long serviceDate) {
		PrintWriter pw = null;
		try {
			LOG.info("Creating output file for trip {}, instance {}", tripId,
					serviceDate);
			pw = new PrintWriter(filename, "UTF-8");
			beginning(pw);
			addNodes(pw, tripId);
			addEdges(pw, tripId);
			end(pw);
			pw.flush();
			LOG.info("Output file created for trip {}", tripId);
		} catch (final IOException exc) {
			LOG.error(
					"Error creating output file for trip {}, instance {}. Msg {}",
					tripId, serviceDate, exc.getMessage());
		} finally {
			if (pw != null) {
				pw.close();
			}
		}

	}

	protected abstract void beginning(PrintWriter writer);

	protected abstract void addNodes(PrintWriter writer);

	protected abstract void addEdges(PrintWriter writer);

	protected abstract void end(PrintWriter writer);

	protected abstract void addNodes(PrintWriter writer, int tripId);

	protected abstract void addEdges(PrintWriter writer, int tripId);

	protected abstract void addNodes(PrintWriter writer, int tripId,
			long serviceDate);

	protected abstract void addEdges(PrintWriter writer, int tripId,
			long serviceDate);

}
