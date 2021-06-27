package dk.thebeerclub.brewhub.controller;

import dk.thebeerclub.brewhub.service.TiltService;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


@RestController
@RequestMapping("/tilt")
public class TiltController {

    private final TiltService tiltService;

    public TiltController(TiltService tiltService) {
        this.tiltService = tiltService;
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


}
