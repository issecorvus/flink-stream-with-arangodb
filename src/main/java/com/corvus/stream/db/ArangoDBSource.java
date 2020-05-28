package com.corvus.stream.db;

import static com.corvus.stream.config.ApplicationConfig.KEY_ARANGO_DB_PASSWORD;
import static com.corvus.stream.config.ApplicationConfig.KEY_ARANGO_DB_USERNAME;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.arangodb.ArangoDB;
import com.arangodb.entity.BaseDocument;

public abstract class ArangoDBSource implements Iterator<LinkedList<BaseDocument>>, Serializable {
	private static final long serialVersionUID = 1L;
	protected List<LinkedList<BaseDocument>> routesList = new ArrayList<>();
	protected Iterator<LinkedList<BaseDocument>> iterator = null;
	protected transient ArangoDB arangoDB = null;
	
	
	@Override
	public boolean hasNext() {
		if (routesList.size() == 0) {
			getDB();
			getData();
			iterator = routesList.iterator();
		}
		return iterator.hasNext();
	}

	@Override
	public LinkedList<BaseDocument> next() {
		if (routesList.size() == 0) {
			getDB();
			getData();
			iterator = routesList.iterator();
		}
		return iterator.next();
	}
	protected ArangoDB getDB() {
		arangoDB = new ArangoDB.Builder()
				.user(System.getProperty(KEY_ARANGO_DB_USERNAME,"root"))
				.password(System.getProperty(KEY_ARANGO_DB_PASSWORD,"openSesame"))
				.build();
		return arangoDB;

	}
	protected abstract void getData();
}
