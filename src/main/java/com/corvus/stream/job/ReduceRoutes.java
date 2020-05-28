package com.corvus.stream.job;

import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;

import com.corvus.stream.db.Route;

/**
 * @author Rick Vincent
 * Finds the minimum distance route given by the window and returns it.
 *
 */
public class ReduceRoutes extends ProcessWindowFunction<Route, Route, String, GlobalWindow> {

	private static final long serialVersionUID = 1L;

	@Override
	public void process(String key, Context context,
			Iterable<Route> routes, Collector<Route> collector) throws Exception {
		Route minRoute = null;
		for(Route route : routes) {
			if(minRoute == null || route.getDistance() < minRoute.getDistance()) {
				minRoute = route;
			}
		}
		collector.collect(minRoute);
		
	}

}
