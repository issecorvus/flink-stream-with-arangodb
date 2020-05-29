package com.corvus.stream;

import static com.corvus.stream.config.ApplicationConfig.*;

import java.util.Optional;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import com.corvus.stream.config.ApplicationConfig;
import com.corvus.stream.db.ArangoDBAllRouteSource;
import com.corvus.stream.db.ArangoDBDirectRouteSource;
import com.corvus.stream.job.FlinkStreamJob;
import com.corvus.stream.job.NodeSource;

/**
 * @author Rick Vincent
 * Runs the Flink Stream demo application for ArangoDb.  The ArangoDb data is really
 * bounded data so using BatchTableEnvironment is probably more appropriate, but for
 * this exercise, we want to show a windowed model.  The user can choose between two
 * different ArangoDB sources and this is controlled by the property com.corvus.stream.all.routes
 * in application.properties.
 *
 */
@SpringBootApplication
public class StreamMainApplication implements Logging {
	
	@Autowired
	private Environment env;
	private static ConfigurableApplicationContext appContext = null;
	
	public static void main(String[] args) {

		AnnotationConfigApplicationContext  applicationConfigContext = new AnnotationConfigApplicationContext(ApplicationConfig.class);		
		FlinkStreamJob.Builder job = applicationConfigContext.getBean(FlinkStreamJob.Builder.class);
		
		appContext = new SpringApplicationBuilder(StreamMainApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
		
        StreamMainApplication application = appContext.getBean(StreamMainApplication.class);
        
        System.exit(SpringApplication.exit(appContext, () -> application.run(job) ? 0 : 1));
	}
	
    /**
     * Runs a Flink job based on an ArangoDB node source.  The {@link ArangoDBAllRouteSource}
     * is really just a subset of airports, otherwise it would take quite a lot of time.  
     * @param Flink job builder
     * @return sucess or failure
     */
    private boolean run(FlinkStreamJob.Builder job) {
    	try {
    		Boolean allRoutes = Boolean.parseBoolean(env.getProperty(KEY_ALL_ROUTES));
    		job
    			.withNodeSource( allRoutes == true ? 
    					new NodeSource(new ArangoDBAllRouteSource()) : 
    					new NodeSource(new ArangoDBDirectRouteSource()))
    			.build().execute();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
        return true;
    }
    public static Environment getEnvironment() {
    	return appContext == null ? null : appContext.getEnvironment();
    }
    
}
