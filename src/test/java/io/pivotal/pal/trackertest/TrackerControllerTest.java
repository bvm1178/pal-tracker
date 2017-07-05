package io.pivotal.pal.trackertest;

import io.pivotal.pal.tracker.TrackerController;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TrackerControllerTest {
    @Test
    public void itSaysHello() throws Exception {
        TrackerController controller = new TrackerController();

        assertThat(controller.sayHello()).isEqualTo("hello");
    }

}