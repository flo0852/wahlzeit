package org.wahlzeit.model;
import java.io.File;

public class SportPhotoManager extends PhotoManager {

protected static final SportPhotoManager instance = new SportPhotoManager();

    public SportPhotoManager(){
        photoTagCollector = PhotoFactory.getInstance().createPhotoTagCollector();
    }

    public static final SportPhotoManager getInstance() {
		return instance;
	}

    public SportPhoto createPhoto(File file) throws Exception {
		PhotoId id = PhotoId.getNextId();
		SportPhoto result = PhotoUtil.createPhoto(file, id);
		addPhoto(result);
		return result;
	}

}
