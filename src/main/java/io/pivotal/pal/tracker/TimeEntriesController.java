package io.pivotal.pal.tracker;

import java.util.List;

import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/timeEntries")
public class TimeEntriesController {

	private static final ResponseEntity<TimeEntry> NOT_FOUND_RESPONSE = new ResponseEntity<>(HttpStatus.NOT_FOUND);
	
	private static final String CREATED_METRIC_NAME = "timeEntry.created";
	private static final String UPDATED_METRIC_NAME = "timeEntry.updated";
	private static final String DELETED_METRIC_NAME = "timeEntry.deleted";
	private static final String READ_METRIC_NAME = "timeEntry.read";
	private static final String READ_ALL_METRIC_NAME = "timeEntry.read.all";
	
	private static final String COUNT_GAUGE_NAME = "timeEntries.count";

	private final CounterService counter;
	private final GaugeService gauge;
	
	private final TimeEntryRepository timeEntryRepository;

	public TimeEntriesController(TimeEntryRepository timeEntryRepository, CounterService counter,
	        GaugeService gauge) {
		this.timeEntryRepository = timeEntryRepository;
		this.counter = counter;
		this.gauge = gauge;
		
	}

	@PostMapping
	public ResponseEntity<TimeEntry> create(@RequestBody TimeEntry timeEntry) {	
		ResponseEntity<TimeEntry> createdEntry = new ResponseEntity<>(timeEntryRepository.create(timeEntry), HttpStatus.CREATED);
		
		counter.increment(CREATED_METRIC_NAME);
		gauge.submit(COUNT_GAUGE_NAME, timeEntryRepository.list().size());
		
		return createdEntry;
				
	}

	@GetMapping("{id}")
	public ResponseEntity<TimeEntry> read(@PathVariable long id) {
		TimeEntry theEntry = timeEntryRepository.get(id);

		ResponseEntity<TimeEntry> response = NOT_FOUND_RESPONSE;
		if (theEntry != null) {
			response = new ResponseEntity<>(theEntry, HttpStatus.OK);
			counter.increment(READ_METRIC_NAME);
		}

		return response;
	}

	@GetMapping
	public ResponseEntity<List<TimeEntry>> list() {
		counter.increment(READ_ALL_METRIC_NAME);
		return new ResponseEntity<>(timeEntryRepository.list(), HttpStatus.OK);
	}

	@PutMapping("{id}")
	public ResponseEntity<TimeEntry> update(@PathVariable long id, @RequestBody TimeEntry theEntry) {
		TimeEntry updatedEntry = timeEntryRepository.update(id, theEntry);

		ResponseEntity<TimeEntry> response = NOT_FOUND_RESPONSE;
		if (updatedEntry != null) {
			response = new ResponseEntity<>(updatedEntry, HttpStatus.OK);
			counter.increment(UPDATED_METRIC_NAME);
		}

		return response;
	}

	@DeleteMapping("{id}")
	public ResponseEntity<TimeEntry> delete(@PathVariable long id) {
		timeEntryRepository.delete(id);
		
		counter.increment(DELETED_METRIC_NAME);
		gauge.submit(COUNT_GAUGE_NAME, timeEntryRepository.list().size());
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
