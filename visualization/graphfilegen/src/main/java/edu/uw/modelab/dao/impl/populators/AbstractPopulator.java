package edu.uw.modelab.dao.impl.populators;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPopulator {

	private static final Logger LOG = LoggerFactory
			.getLogger(AbstractPopulator.class);

	private final String file;
	private final boolean enabled;
	private final String separator;

	public AbstractPopulator(final String file, final boolean enabled,
			final String separator) {
		this.file = file;
		this.enabled = enabled;
		this.separator = separator;
	}

	protected abstract void doPopulate(List<String[]> tokens);

	public void populate() {
		if (enabled) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(getClass()
						.getResourceAsStream(file)));
				String line = br.readLine(); // discard first line, headers
				final List<String[]> tokensPerLine = new ArrayList<String[]>();
				while ((line = br.readLine()) != null) {
					tokensPerLine.add(line.split(separator));
				}
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
