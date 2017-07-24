package io.pivotal.pal.tracker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public class JdbcTimeEntryRepository implements TimeEntryRepository {

	private static final ResultSetExtractor<List<TimeEntry>> ENTRIES_EXTRACTOR = new ResultSetExtractor<List<TimeEntry>>() {

		@Override
		public List<TimeEntry> extractData(ResultSet resutSet) throws SQLException {
			List<TimeEntry> entries = new ArrayList<>();
			
			while (resutSet.next()) {
				TimeEntry entry = new TimeEntry();
				entry.setId(resutSet.getLong(1));
				entry.setProjectId(resutSet.getLong(2));
				entry.setUserId(resutSet.getLong(3));
				entry.setDate(resutSet.getString(4));
				entry.setHours(resutSet.getInt(5));
				
				entries.add(entry);
			}
			
			return entries;
		}
	}; 
	
	private static final String SELECT_BY_ID_QUERY = "SELECT id,project_id,user_id,date,hours FROM time_entries WHERE id = ?";
	private static final String SELECT_ALL_QUERY = "SELECT id,project_id,user_id,date,hours FROM time_entries";
	private static final String INSERT_QUERY = "INSERT INTO time_entries (project_id,user_id,date,hours) VALUES (?, ?, ?, ?)";
	private static final String UPDATE_QUERY = "UPDATE time_entries SET project_id=?, user_id=?, date=?, hours=? WHERE id=?";
	private static final String DELETE_QUERY = "DELETE FROM time_entries WHERE id=?";

	private final JdbcTemplate template;

	public JdbcTimeEntryRepository(DataSource dataSource) {
		super();
		this.template = new JdbcTemplate(dataSource);
	}

	@Override
	public TimeEntry create(TimeEntry timeEntry) {
		KeyHolder idExtractor = new GeneratedKeyHolder();

		template.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement statement = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
				
				fillParameters(timeEntry, statement);
				
				return statement;
			}
		}, idExtractor);
		
		timeEntry.setId(idExtractor.getKey().longValue());
		
		return timeEntry;
	}

	@Override
	public TimeEntry get(Long id) {
		List<TimeEntry> entries = template.query(SELECT_BY_ID_QUERY, new Object[] {id}, ENTRIES_EXTRACTOR);
		TimeEntry theEntry = null;
		
		if (entries.size() == 1) {
			theEntry = entries.get(0);
		}
		
		return theEntry;
	}

	@Override
	public List<TimeEntry> list() {
		return template.query(SELECT_ALL_QUERY, ENTRIES_EXTRACTOR);
	}

	@Override
	public TimeEntry update(Long id, TimeEntry timeEntry) {
		template.update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY);
				
				fillParameters(timeEntry, statement);
				statement.setLong(5, id);
				
				return statement;
			}
		});
		
		timeEntry.setId(id);
		
		return timeEntry;
	}

	@Override
	public void delete(Long id) {
		template.update(DELETE_QUERY, id);
	}

	private void fillParameters(TimeEntry timeEntry, PreparedStatement statement) throws SQLException {
		statement.setLong(1, timeEntry.getProjectId());
		statement.setLong(2, timeEntry.getUserId());
		statement.setString(3, timeEntry.getDate());
		statement.setInt(4, timeEntry.getHours());
	}

}
