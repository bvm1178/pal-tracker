package io.pivotal.pal.tracker;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PalTrackerApplication {
	
    public static void main(String[] args) {
        SpringApplication.run(PalTrackerApplication.class, args);
    }
    
    @Bean
    public TimeEntryRepository getTimeEntryRepository(DataSource dataSource) throws SQLException {
		return new JdbcTimeEntryRepository(dataSource);
	}
}