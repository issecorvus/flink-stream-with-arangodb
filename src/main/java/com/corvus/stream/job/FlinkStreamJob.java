package com.corvus.stream.job;


import java.util.LinkedList;

import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;
import org.apache.flink.streaming.api.windowing.triggers.CountTrigger;

import com.arangodb.entity.BaseDocument;
import com.corvus.stream.db.Route;
import com.corvus.stream.sink.OutputSink;

/**
 * @author Rick Vincent
 * 
 * Builds and executes the Flink stream job taking the ArangoDB source,
 * calculating distance and applying a first filter, then reducing the 
 * routes by number of stop overs.
 *
 */
public class FlinkStreamJob {
	private static StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
	
	public void execute() throws Exception {
		JobExecutionResult result = env.execute("Minimum Route Finder");
		result.getJobID();
	}
	
	public static class Builder {
		private DataStream<LinkedList<BaseDocument>> routesStream = null;
		private DataStream<Route> distanceStream = null;
		private NodeSource nodeSource = null;
		
		public Builder withNodeSource(NodeSource nodeSource) {
			this.nodeSource = nodeSource;
			return this;
		}

		public FlinkStreamJob build() {
			FlinkStreamJob instance = new FlinkStreamJob();
			this.routesStream = env
					.addSource(this.nodeSource)
					.name("direct-routes");
			this.distanceStream = routesStream
		            .keyBy(LinkedList<BaseDocument>::size)
		            .process(new DistanceFilter())
		            .name("route-detector");
			SingleOutputStreamOperator<Route> route = this.distanceStream
					.keyBy(Route::getId)
					.window(GlobalWindows.create())
					.trigger(CountTrigger.of(2))
					.process( new ReduceRoutes());
			route.addSink(new OutputSink());
			return instance;
		}
	}

}
