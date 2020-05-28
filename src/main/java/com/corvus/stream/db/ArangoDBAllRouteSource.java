package com.corvus.stream.db;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.arangodb.ArangoCursor;
import com.arangodb.entity.BaseDocument;
import com.arangodb.util.MapBuilder;
import com.corvus.stream.Logging;

/**
 * @author Rick Vincent
 * 
 * Provides shortest path routes between a subset of airports. 
 *
 */
public class ArangoDBAllRouteSource extends ArangoDBSource implements Logging {
	
	private static final long serialVersionUID = 1L;

	public enum AIRPORTS {
		BIS,LAS,DEN,IAD,JFK,PBI;
	}
	
	protected void getData() {
		try {
			logger.info("Using ArangoDBAllRouteSource");
			String airportQuery = String.format("FOR airport IN airports FILTER airport._key IN %s  RETURN airport", EnumSet.allOf(AIRPORTS.class).stream().map(e -> "\"" + e.name() + "\"").collect(Collectors.toList()));
			ArangoCursor<BaseDocument> cursor = arangoDB.db().query(airportQuery, null, null, BaseDocument.class);
			List<BaseDocument> docList = cursor.asListRemaining();
			for(BaseDocument start : docList) {
				for(BaseDocument dest : docList) {
					if(start != dest) {
						String query = "FOR v IN OUTBOUND SHORTEST_PATH @fromAirport TO @toAirport flights RETURN v";

						Map<String, Object> bindVars = new MapBuilder().put("fromAirport", start.getId())
								.put("toAirport", dest.getId()).get();
						try (ArangoCursor<BaseDocument> cursorShortest = arangoDB.db().query(query, bindVars, null,
								BaseDocument.class)) {
							LinkedList<BaseDocument> routeList = new LinkedList<>();
							cursorShortest.forEachRemaining(aDocument -> {
								routeList.add(aDocument);
							});
							routesList.add(routeList);
						}						
					}
				}				
			}

		} catch (Exception e) {
			logger.error("Failed to execute query. " + e.getMessage());
		}
	}
}
