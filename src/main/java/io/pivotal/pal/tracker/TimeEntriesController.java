package io.pivotal.pal.tracker;

import java.util.List;

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
	
	private static final ResponseEntity<TimeEntry> NOT_FOUND_RESPONSE = new ResponseEntity<TimeEntry>(HttpStatus.NOT_FOUND);

	private final TimeEntryRepository timeEntryRepository;
	
	public TimeEntriesController(TimeEntryRepository timeEntryRepository) {
		this.timeEntryRepository = timeEntryRepository;
	}

	@PostMapping
	public ResponseEntity<TimeEntry> create(@RequestBody TimeEntry timeEntry) {
		return new ResponseEntity<>(timeEntryRepository.create(timeEntry), HttpStatus.CREATED) ;
	}

	@GetMapping("{id}")
	public ResponseEntity<TimeEntry> read(@PathVariable long id) {
		TimeEntry theEntry = timeEntryRepository.get(id);
		
		ResponseEntity<TimeEntry> response = NOT_FOUND_RESPONSE;	
		if (theEntry != null) {
			response = new ResponseEntity<>(theEntry, HttpStatus.OK);
		}
		
		return response;
	}

	@GetMapping
	public ResponseEntity<List<TimeEntry>> list() {
		return new ResponseEntity<>(timeEntryRepository.list(), HttpStatus.OK);
	}

	@PutMapping("{id}")
	public ResponseEntity<TimeEntry> update(@PathVariable long id, @RequestBody TimeEntry theEntry) {
		TimeEntry updatedEntry = timeEntryRepository.update(id, theEntry);
		
		ResponseEntity<TimeEntry> response = NOT_FOUND_RESPONSE;
		if(updatedEntry != null) {
			response = new ResponseEntity<>(updatedEntry, HttpStatus.OK);
		}
		
		return response;
	}

	@DeleteMapping("{id}")
	public ResponseEntity<TimeEntry> delete(@PathVariable long id) {
		timeEntryRepository.delete(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
