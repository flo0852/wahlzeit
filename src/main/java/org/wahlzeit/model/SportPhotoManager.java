package org.wahlzeit.model;
import java.io.File;
import java.net.PortUnreachableException;

public class SportPhotoManager extends PhotoManager {


    public SportPhotoManager(){
        photoTagCollector = PhotoFactory.getInstance().createPhotoTagCollector();
    }

    public static final SportPhotoManager getInstance() {
		if(PhotoManager.getInstance() == null){
			PhotoManager.setInstance(new SportPhotoManager());
		}
		return (SportPhotoManager) PhotoManager.getInstance();
	}

	public static void initialize(){
		getInstance();
	}

    public SportPhoto createPhoto(File file) throws Exception {
		PhotoId id = PhotoId.getNextId();
		SportPhoto result = PhotoUtil.createPhoto(file, id);
		addPhoto(result);
		return result;
	}

}
