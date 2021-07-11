package dk.thebeerclub.brewhub.controller;

import dk.thebeerclub.brewhub.service.DiscordService;
import dk.thebeerclub.brewhub.service.TiltService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;


@RestController
@RequestMapping("/tilt")
public class TiltController {

    private final TiltService tiltService;
    private final DiscordService discordService;

    public TiltController(TiltService tiltService, DiscordService discordService) {
        this.tiltService = tiltService;
        this.discordService = discordService;
    }

    @GetMapping("/check")
    public String check() {
        return "check - " + new Date();
    }

    @GetMapping("/{id}")
    public String fetchForBrew(@PathVariable String id) {
        tiltService.updateBrew(Long.valueOf(id));
        return "success";
    }

    @GetMapping("/test")
    public void test() throws IOException {
        discordService.sendMessage("test message 123");
    }

    @GetMapping("/test/notify")
    public void notifyDiscord() {
        tiltService.notifyDiscord();
    }


}
