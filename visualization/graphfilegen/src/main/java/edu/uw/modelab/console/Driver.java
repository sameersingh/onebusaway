package edu.uw.modelab.console;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.uw.modelab.service.FileCreator;

public class Driver {

	public Driver() {
		final ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
				"classpath:app-context.xml");
		final FileCreator creator = appContext.getBean("fileCreator",
				FileCreator.class);
		creator.create();
	}

	public static void main(final String[] args) {
		new Driver();

	}
}
