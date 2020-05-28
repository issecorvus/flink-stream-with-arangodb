package com.corvus.stream.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import com.corvus.stream.job.FlinkStreamJob;

/**
 * @author Rick Vincent
 * 
 * Configuration for the Spring Boot app.
 *
 */
@Lazy
@Configuration
public class ApplicationConfig {
	public final static String KEY_ARANGO_DB_USERNAME = "com.corvus.stream.db.arangodb.username";
	public final static String KEY_ARANGO_DB_PASSWORD = "com.corvus.stream.db.arangodb.password";
	public final static String KEY_ALL_ROUTES = "com.corvus.stream.all.routes";

	@Bean
	public FlinkStreamJob.Builder flinkStreamJob() {
		return new FlinkStreamJob.Builder();
	}

}
