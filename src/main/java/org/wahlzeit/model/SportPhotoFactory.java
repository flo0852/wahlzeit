package org.wahlzeit.model;
import org.wahlzeit.services.*;

import java.sql.*;

public class SportPhotoFactory extends PhotoFactory{
    /**
	 * Hidden singleton instance; needs to be initialized from the outside.
	 */
    private static SportPhotoFactory instance = null;

    protected SportPhotoFactory(){
        //Do nothing
    }

    /**
	 * Public singleton access method.
	 */
	public static synchronized SportPhotoFactory getInstance() {
		if (instance == null) {
			SysLog.logSysInfo("setting generic SportPhotoFactory");
			setInstance(new SportPhotoFactory());
		}
		
		return instance;
	}
    protected static synchronized void setInstance(SportPhotoFactory sportPhotoFactory) {
		if (instance != null) {
			throw new IllegalStateException("attempt to initialize SportPhotoFactory twice");
		}
		
		instance = sportPhotoFactory;
	}

	public SportPhoto createPhoto(){
		return new SportPhoto();
	}
	
	public SportPhoto createPhoto(ResultSet rs) throws SQLException {
		return new SportPhoto(rs);
	}

	public SportPhoto createPhoto(PhotoId id) {
		return new SportPhoto(id);
	}
}
