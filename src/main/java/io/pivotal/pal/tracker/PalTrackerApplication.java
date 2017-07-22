package io.pivotal.pal.tracker;

import java.sql.SQLException;

import org.mariadb.jdbc.MariaDbDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PalTrackerApplication {
	
    public static void main(String[] args) {
        SpringApplication.run(PalTrackerApplication.class, args);
    }
    
    @Bean
    public TimeEntryRepository getTimeEntryRepository() throws SQLException {
		return new JdbcTimeEntryRepository(new MariaDbDataSource(System.getenv("SPRING_DATASOURCE_URL")));
	}
}