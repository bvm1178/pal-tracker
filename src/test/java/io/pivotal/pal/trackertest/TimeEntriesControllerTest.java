package io.pivotal.pal.trackertest;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.pivotal.pal.tracker.TimeEntriesController;
import io.pivotal.pal.tracker.TimeEntry;
import io.pivotal.pal.tracker.TimeEntryRepository;


import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

public class TimeEntriesControllerTest {
	
	private TimeEntryRepository timeEntryRepository;
    private TimeEntriesController controller;
    private CounterService counterService;
    private GaugeService gaugeService;

    @Before
    public void setUp() throws Exception {
        timeEntryRepository = mock(TimeEntryRepository.class);
        counterService = mock(CounterService.class);
        gaugeService = mock(GaugeService.class);
        
        controller = new TimeEntriesController(timeEntryRepository, counterService, gaugeService);
    }

    @Test
    public void testCreate() throws Exception {
        TimeEntry expected = new TimeEntry(1L, 123, 456, "today", 8);
        doReturn(expected)
            .when(timeEntryRepository)
            .create(any(TimeEntry.class));

        ResponseEntity<TimeEntry> response = controller.create(new TimeEntry(123, 456, "today", 8));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void testRead() throws Exception {
        TimeEntry expected = new TimeEntry(1L, 123, 456, "today", 8);
        doReturn(expected)
            .when(timeEntryRepository)
            .get(1L);

        ResponseEntity<TimeEntry> response = controller.read(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void testList() throws Exception {
        List<TimeEntry> expected = asList(
            new TimeEntry(1, 123, 456, "today", 8),
            new TimeEntry(2, 789, 321, "yesterday", 4)
        );
        doReturn(expected).when(timeEntryRepository).list();

        ResponseEntity<List<TimeEntry>> response = controller.list();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void testUpdate() throws Exception {
        TimeEntry expected = new TimeEntry(1, 987, 654, "yesterday", 4);
        doReturn(expected)
            .when(timeEntryRepository)
            .update(eq(1L), any(TimeEntry.class));

        ResponseEntity<TimeEntry> response = controller.update(1L, expected);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void testDelete() throws Exception {
        ResponseEntity<TimeEntry> response = controller.delete(1L);
        verify(timeEntryRepository).delete(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
	
}
