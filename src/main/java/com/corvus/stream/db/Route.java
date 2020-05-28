package com.corvus.stream.db;

/**
 * @author Rick Vincent
 * 
 * Represents a route between two airports where distance is calculated by a {@link DistanceFilter}
 *
 */
public class Route {
	public String key = null;
	public String id = null;
	public String from = null;
	public String to = null;
	public Long distance = null;

	public Route() {
		
	}
	private Route(String key, String id, String from, String to, Long distanceBetweenAirports) {
		this.key = key;
		this.id = id;
		this.from = from;
		this.to = to;
		this.distance = distanceBetweenAirports;
	}
	public String getKey() {
		return key;
	}
	public String getId() {
		return id;
	}
	public String getFrom() {
		return from;
	}
	public String getTo() {
		return to;
	}
	public Long getDistance() {
		return distance;
	}
	public String toString() {
		return "Route: " + key + " distance: " + distance;
	}
	public static class Builder {
		private String key;
		private String id;
		private String from;
		private String to;
		private Long distance = 0L;
		public Builder withKey(String key) {
			this.key = key;
			return this;
		}
		public Builder withId(String id) {
			this.id = id;
			return this;
		}
		public Builder withFrom(String from) {
			this.from = from;
			return this;
		}
		public Builder withTo(String to) {
			this.to = to;
			return this;
		}
		public Builder withDistance(Long distance) {
			this.distance = distance;
			return this;
		}
		public Route build() {
			Route instance = new Route(key,id,from,to,distance);
			return instance;
		}
	}

}
