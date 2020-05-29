package com.corvus.stream.db;

import static com.corvus.stream.config.ApplicationConfig.KEY_ARANGO_DB_PASSWORD;
import static com.corvus.stream.config.ApplicationConfig.KEY_ARANGO_DB_USERNAME;
import static com.corvus.stream.StreamMainApplication.getEnvironment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.core.env.Environment;

import com.arangodb.ArangoDB;
import com.arangodb.entity.BaseDocument;

public abstract class ArangoDBSource implements Iterator<LinkedList<BaseDocument>>, Serializable {
	private static final long serialVersionUID = 1L;
	protected List<LinkedList<BaseDocument>> routesList = new ArrayList<>();
	protected Iterator<LinkedList<BaseDocument>> iterator = null;
	protected transient ArangoDB arangoDB = null;
	private Environment env;
	
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
		if (this.env == null) {
			this.env = getEnvironment();
		}
		arangoDB = new ArangoDB.Builder()
				.user(Optional.ofNullable(System.getProperty(KEY_ARANGO_DB_USERNAME)).orElseGet( () -> env.getProperty(KEY_ARANGO_DB_USERNAME,"")))
				.password( Optional.ofNullable(System.getProperty(KEY_ARANGO_DB_PASSWORD)).orElseGet(() -> env.getProperty(KEY_ARANGO_DB_PASSWORD,"")))
				.build();
		return arangoDB;

	}
	protected abstract void getData();
}
