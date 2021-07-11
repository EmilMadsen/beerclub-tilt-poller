package dk.thebeerclub.brewhub.service;

import dk.thebeerclub.brewhub.model.TiltLog;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TiltServiceTests {

    private TiltService tiltService = new TiltService(null, null);

    @Test
    public void parse_csv() throws FileNotFoundException {

        File file = new File("src/test/resources/smash-ipa.csv");

        List<TiltLog> logs = tiltService.readCSVStream(new FileInputStream(file), 123L);

        assertEquals(2086, logs.size());
        assertEquals("123_1623580738", logs.get(2).getId());
        assertEquals(123L, logs.get(2).getParentId());
        assertEquals("2021-06-13T12:38:58+02:00[Europe/Copenhagen]", logs.get(2).getTimestamp().toString());
        assertEquals(1012, logs.get(2).getGravity());
        assertEquals(20.0, logs.get(2).getTemperature());

    }

}
