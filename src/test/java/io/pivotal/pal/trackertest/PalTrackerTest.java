package io.pivotal.pal.trackertest;

import com.jayway.jsonpath.DocumentContext;
import io.pivotal.pal.tracker.PalTrackerApplication;
import io.pivotal.pal.tracker.TimeEntry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;

import static com.jayway.jsonpath.JsonPath.parse;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PalTrackerApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class PalTrackerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void crudTest() throws Exception {
        // List
        ResponseEntity<String> listResponse = restTemplate.getForEntity("/timeEntries", String.class);
        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext listJson = parse(listResponse.getBody());
        Collection<TimeEntry> timeEntries = listJson.read("$[*]", Collection.class);
        assertThat(timeEntries).isEmpty();

        TimeEntry timeEntry = new TimeEntry(123, 456, "today", 8);

        // Create
        ResponseEntity<String> createResponse = restTemplate.postForEntity("/timeEntries", timeEntry, String.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        DocumentContext createJson = parse(createResponse.getBody());
        assertThat(createJson.read("$.id", Long.class)).isEqualTo(1L);
        assertThat(createJson.read("$.projectId", Long.class)).isEqualTo(123L);
        assertThat(createJson.read("$.userId", Long.class)).isEqualTo(456L);
        assertThat(createJson.read("$.date", String.class)).isEqualTo("today");
        assertThat(createJson.read("$.hours", Long.class)).isEqualTo(8);

        // Read
        ResponseEntity<String> readResponse = this.restTemplate.getForEntity("/timeEntries/1", String.class);
        assertThat(readResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext readJson = parse(createResponse.getBody());
        assertThat(readJson.read("$.id", Long.class)).isEqualTo(1L);
        assertThat(readJson.read("$.projectId", Long.class)).isEqualTo(123L);
        assertThat(readJson.read("$.userId", Long.class)).isEqualTo(456L);
        assertThat(readJson.read("$.date", String.class)).isEqualTo("today");
        assertThat(readJson.read("$.hours", Long.class)).isEqualTo(8);

        // Update
        TimeEntry timeEntryUpdates = new TimeEntry(1, 2, 3, "tomorrow", 9);

        ResponseEntity<String> updateResponse = restTemplate.exchange("/timeEntries/1", HttpMethod.PUT, new HttpEntity(timeEntryUpdates, null), String.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext updateJson = parse(updateResponse.getBody());
        assertThat(updateJson.read("$.id", Long.class)).isEqualTo(1L);
        assertThat(updateJson.read("$.projectId", Long.class)).isEqualTo(2L);
        assertThat(updateJson.read("$.userId", Long.class)).isEqualTo(3L);
        assertThat(updateJson.read("$.date", String.class)).isEqualTo("tomorrow");
        assertThat(updateJson.read("$.hours", Long.class)).isEqualTo(9);

        ResponseEntity<String> updatedReadResponse = this.restTemplate.getForEntity("/timeEntries/1", String.class);
        assertThat(updatedReadResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext updatedReadJson = parse(updatedReadResponse.getBody());
        assertThat(updatedReadJson.read("$.id", Long.class)).isEqualTo(1L);
        assertThat(updatedReadJson.read("$.projectId", Long.class)).isEqualTo(2L);
        assertThat(updatedReadJson.read("$.userId", Long.class)).isEqualTo(3L);
        assertThat(updatedReadJson.read("$.date", String.class)).isEqualTo("tomorrow");
        assertThat(updatedReadJson.read("$.hours", Long.class)).isEqualTo(9);

        // Delete
        ResponseEntity<String> deleteResponse = restTemplate.exchange("/timeEntries/1", HttpMethod.DELETE, null, String.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> deletedReadResponse = this.restTemplate.getForEntity("/timeEntries/1", String.class);
        assertThat(deletedReadResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
