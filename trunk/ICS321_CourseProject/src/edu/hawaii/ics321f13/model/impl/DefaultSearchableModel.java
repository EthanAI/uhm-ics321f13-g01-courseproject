package edu.hawaii.ics321f13.model.impl;

import java.io.IOException;

import edu.hawaii.ics321f13.model.interfaces.Database;
import edu.hawaii.ics321f13.model.interfaces.ImageResult;
import edu.hawaii.ics321f13.model.interfaces.ResultConstraint;
import edu.hawaii.ics321f13.model.interfaces.SearchableModel;
import edu.hawaii.ics321f13.model.interfaces.Traversable;

public class DefaultSearchableModel implements SearchableModel {
	
	public DefaultSearchableModel(Database database) {
		
	}
	
	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public Traversable<ImageResult> search(String key, Class<?> resultType,
			ResultConstraint constraint) {
		// TODO Auto-generated method stub
		return null;
	}

}
