package com.corvus.stream.db;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.entity.BaseDocument;
import com.corvus.stream.config.ApplicationConfig;

public class TestDB {

	@Test
	public void testArangoDB() {
		
		System.setProperty(ApplicationConfig.KEY_ARANGO_DB_USERNAME, "root");
		System.setProperty(ApplicationConfig.KEY_ARANGO_DB_PASSWORD, "openSesame");
		ArangoDBSource source = new ArangoDBAllRouteSource();
		ArangoDB db = source.getDB();
		ArangoCursor<BaseDocument> cursor = db.db("_system")
				.query("FOR airport IN airports FILTER airport._key IN [\"BIS\",\"LAX\",\"DEN\", \"IAD\", \"JFK\", \"PBI\"]  RETURN airport", null, null, BaseDocument.class);
		List<BaseDocument> docList = cursor.asListRemaining();
		assertEquals(6,docList.size());	
	}
}
