package com.corvus.stream.sink;

import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import com.corvus.stream.db.Route;

/**
 * @author Rick Vincent
 * Simply outputs the value of our stream results
 *
 */
public class OutputSink implements SinkFunction<Route> {

	private static final long serialVersionUID = 1L;

	@Override
	public void invoke(Route value, Context context) {
		System.out.println(value.getKey() + " distance = " + value.getDistance());
	}
}
