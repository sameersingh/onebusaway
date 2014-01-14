package edu.uw.modelab.dao.impl.populators;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BulkPopulator {

	private static final Logger LOG = LoggerFactory
			.getLogger(BulkPopulator.class);

	private final String folder;
	private final boolean enabled;
	private final String separator;

	public BulkPopulator(final String folder, final boolean enabled,
			final String separator) {
		this.folder = folder;
		this.enabled = enabled;
		this.separator = separator;
	}

	protected abstract void doPopulate(List<String[]> tokens);

	public void populate() {
		if (enabled) {

			final File f = new File(folder);
			final File[] listOfFiles = f.listFiles();

			for (final File file : listOfFiles) {
				BufferedReader br = null;
				try {
					br = new BufferedReader(new FileReader(file));
					final List<String[]> tokensPerLine = new ArrayList<String[]>();
					String line = null;
					while ((line = br.readLine()) != null) {
						tokensPerLine.add(line.split(separator));
					}
					LOG.info("Populating file {}...", file.getName());
					doPopulate(tokensPerLine);
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
		}
	}
}
