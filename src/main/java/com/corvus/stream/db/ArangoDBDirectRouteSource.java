package com.corvus.stream.db;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.arangodb.ArangoCursor;
import com.arangodb.entity.BaseDocument;
import com.arangodb.util.MapBuilder;
import com.corvus.stream.Logging;

/**
 * @author Rick Vincent
 * 
 * Provides direct routes for all airports.
 *
 */
public class ArangoDBDirectRouteSource extends ArangoDBSource implements Logging  {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void getData() {
		try {
			logger.info("Using ArangoDBDirectRouteSource");
			ArangoCursor<BaseDocument> cursor = arangoDB.db().query("FOR flight IN flights RETURN flight",
					null, null, BaseDocument.class);
			final Map<String, Route> vertexes = new HashMap<>();
			cursor.forEachRemaining(doc -> {
				vertexes.computeIfAbsent((String) doc.getAttribute("_from") + "->" + (String) doc.getAttribute("_to"),
						val -> new Route.Builder()
									.withKey(doc.getKey())
									.withId(doc.getId())
									.withFrom((String) doc.getAttribute("_from"))
									.withTo((String) doc.getAttribute("_to"))
									.build());
			});
			// Get shortest routes between airports
			for (Map.Entry<String, Route> entry : vertexes.entrySet()) {
				String query = "FOR v IN OUTBOUND SHORTEST_PATH @fromAirport TO @toAirport flights RETURN v";
				Map<String, Object> bindVars = new MapBuilder().put("fromAirport", entry.getValue().getFrom())
						.put("toAirport", entry.getValue().getTo()).get();
				try (ArangoCursor<BaseDocument> cursorShortest = arangoDB.db().query(query, bindVars, null,
						BaseDocument.class)) {
					LinkedList<BaseDocument> routeList = new LinkedList<>();
					cursorShortest.forEachRemaining(aDocument -> {
						routeList.add(aDocument);
					});
					routesList.add(routeList);
				}
			}
		} catch (Exception e) {
			logger.error("Failed to execute query. " + e.getMessage());
		}

	}

}
