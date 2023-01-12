package org.wahlzeit.model;

import org.wahlzeit.services.*;

import java.sql.*;

@PatternInstance(patternName = "Singleton", participants = {
		"Singleton"
})
@PatternInstance(patternName = "Abstract Factory", participants = {
		"ConcreteFactory2"
})
public class SportPhotoFactory extends PhotoFactory {
	/**
	 * Hidden singleton instance; needs to be initialized from the outside.
	 */
	private static boolean isInitialized = false;

	protected SportPhotoFactory() {
		// Do nothing
	}

	public static void initialize() {
		getInstance();
	}

	/**
	 * Public singleton access method.
	 */
	public static synchronized SportPhotoFactory getInstance() {
		if (!isInitialized) {
			SysLog.logSysInfo("setting specialized SportPhotoFactory");
			PhotoFactory.setInstance(new SportPhotoFactory());
			isInitialized = true;
		}

		return (SportPhotoFactory) PhotoFactory.getInstance();
	}

	public SportPhoto createPhoto() {
		return new SportPhoto();
	}

	public SportPhoto createPhoto(Sport sport) {
		return new SportPhoto(sport);
	}

	public SportPhoto createPhoto(ResultSet rs) throws SQLException {
		return new SportPhoto(rs);
	}

	public SportPhoto createPhoto(PhotoId id) {
		return new SportPhoto(id);
	}
}
