package dk.thebeerclub.brewhub.cron;

import dk.thebeerclub.brewhub.service.TiltService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ScheduledJob {

    private final TiltService tiltService;

    public ScheduledJob(TiltService tiltService) {
        this.tiltService = tiltService;
    }

    @Scheduled(cron = "0 0/30 * * * *", zone = "Europe/Paris")
    public void fetchTiltData() {
        log.info("starting scheduled tilt job.");
        tiltService.updateAllBrews();
    }
}
