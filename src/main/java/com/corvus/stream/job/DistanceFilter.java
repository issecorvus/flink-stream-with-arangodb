package com.corvus.stream.job;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import com.arangodb.entity.BaseDocument;
import com.corvus.stream.db.Route;

/**
 * @author Rick Vincent
 * Calculates distance and filters a linked list of routes retrieved by the ArangoDB datasource, 
 * collecting the ones that are the shortest distance based on number of stop overs.  
 * This will be the source of the {@link ReduceRoutes} routine.
 *
 */
public class DistanceFilter extends KeyedProcessFunction<Integer, LinkedList<BaseDocument>, Route> {

	private static final long serialVersionUID = 1L;
	private transient ValueState<Map<Integer,Long>> distanceState;
	private final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;

	@Override
	public void open(Configuration parameters) throws Exception {
		ValueStateDescriptor<Map<Integer,Long>> distanceDesc = new ValueStateDescriptor<>("distance-state", Types.MAP(Types.INT,Types.LONG));
		distanceState = getRuntimeContext().getState(distanceDesc);
	}

	@Override
	public void processElement(LinkedList<BaseDocument> airports, Context context, Collector<Route> routes)
			throws Exception {
		
		BaseDocument startDest = null;
		BaseDocument start = null;
		BaseDocument dest = null;
		BaseDocument lastDest = null;
		int count = 0;
		long distanceBetweenAirports = 0;
		int size = airports.size();
		StringBuilder routeStr = new StringBuilder();
		while (!airports.isEmpty()) {
			start = airports.size() > 0 ? airports.remove() : null;
			if (count++ == 0) {
				startDest = start;
				routeStr.append(startDest.getKey());
			}
			dest = airports.size() > 0 ? airports.remove() : null;
			if (dest != null) {
				lastDest = dest;
			} else {
				dest = start;
				start = lastDest;
			}
			routeStr.append(" -> " + dest.getKey());
			distanceBetweenAirports += distanceInKilometer((double) start.getAttribute("lat"),
					(double) start.getAttribute("long"), (double) dest.getAttribute("lat"),
					(double) dest.getAttribute("long"));

		}
		Map<Integer,Long> distance = distanceState.value();
		if(distance == null) {
			distance = new HashMap<>();
		}
		if (distance.get(size) == null || distanceBetweenAirports <= distance.get(size)) {			
			distance.put(size, distanceBetweenAirports);
			distanceState.update(distance);
			routes.collect(new Route.Builder()
					.withId(String.valueOf(size))
					.withKey(routeStr.toString())
					.withFrom(startDest.getKey())
					.withTo(dest.getKey())
					.withDistance(distanceBetweenAirports)
					.build());
		}

	}

	private long distanceInKilometer(double lat1, double long1, double lat2, double long2) {
		double latDistance = Math.toRadians(lat1 - lat2);
		double lngDistance = Math.toRadians(long1 - long2);
		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return (long) (Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c));
	}

}
