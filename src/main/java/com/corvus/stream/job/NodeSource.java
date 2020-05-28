package com.corvus.stream.job;

import java.util.LinkedList;

import org.apache.flink.annotation.Public;
import org.apache.flink.streaming.api.functions.source.FromIteratorFunction;

import com.arangodb.entity.BaseDocument;
import com.corvus.stream.db.ArangoDBSource;

/**
 * @author Rick Vincent
 * Iterator function for ArangoDBSource
 *
 */
@Public
public class NodeSource extends FromIteratorFunction<LinkedList<BaseDocument>> {


	private static final long serialVersionUID = 1L;
	
	public NodeSource(ArangoDBSource iterator) {
		super(iterator);
	}

}
