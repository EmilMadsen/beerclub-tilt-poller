package dk.thebeerclub.brewhub.service;

import dk.thebeerclub.brewhub.model.Brew;
import dk.thebeerclub.brewhub.model.TiltLog;
import dk.thebeerclub.brewhub.repository.TiltLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TiltService {

    private final static String COMMA_DELIMITER = ",";
    private final static String SLASH = "/";
    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("M/d/yyyy H:mm:ss").withZone(ZoneId.of("Europe/Copenhagen"));

    private final TiltLogRepository tiltLogRepository;
    private final BrewService brewService;
    private final DiscordService discordService;

    public TiltService(TiltLogRepository tiltLogRepository, BrewService brewService, DiscordService discordService) {
        this.tiltLogRepository = tiltLogRepository;
        this.brewService = brewService;
        this.discordService = discordService;
    }

    public void updateAllBrews() {
        List<Brew> brews = brewService.findAllWithTiltUrl();
        brews.forEach(this::fetchAndUpdateTiltData);
    }

    public void updateBrew(Long brewId) {
        Optional<Brew> optional = brewService.findById(brewId);
        optional.ifPresent(this::fetchAndUpdateTiltData);
    }

    public void fetchAndUpdateTiltData(Brew brew) {

        // https://docs.google.com/spreadsheets/d/1AVWW_-WfoUD3cnlcbUGSnqM0RsMypX7rd2QRQC-CQzE/export?format=csv
        // https://docs.google.com/spreadsheets/d/1AVWW_-WfoUD3cnlcbUGSnqM0RsMypX7rd2QRQC-CQzE/edit#gid=734290882

        if (!ObjectUtils.isEmpty(brew.getTiltUrl())) {

            try {

                URL url = buildUrl(brew.getTiltUrl());

                try (InputStream is = url.openStream()){

                    // read from google sheet.
                    List<TiltLog> logs = readCSVStream(is, brew.getId());

                    // get latest log from brew.
                    Optional<TiltLog> optional = tiltLogRepository.findTop1ByParentIdOrderByTimestampDesc(brew.getId());

                    if (optional.isPresent()) {
                        // insert all older than latest.
                        TiltLog latest = optional.get();
                        List<TiltLog> newLogs = logs.stream()
                                .filter(newLog -> newLog.getTimestamp().isAfter(latest.getTimestamp()))
                                .toList();
                        tiltLogRepository.saveAll(newLogs);
                    } else {
                        // or saveAll (no previous data)
                        tiltLogRepository.saveAll(logs);
                    }

                } catch (IOException e) {
                    log.error("failed retrieving data from URL from google sheet url: " + brew.getTiltUrl() + " e");
                }

            } catch (MalformedURLException e) {
                log.error("failed building URL from google sheet url: " + brew.getTiltUrl() + " - " + e.getMessage());
            }

        }

    }

    private URL buildUrl(String tiltUrl) throws MalformedURLException {

        if (tiltUrl.contains("/edit")) {
            tiltUrl = tiltUrl.substring(0, tiltUrl.indexOf("/edit"));
        }
        if (!tiltUrl.endsWith("/")) {
            tiltUrl = tiltUrl + "/";
        }
        if (!tiltUrl.endsWith("export?format=csv")) {
            tiltUrl = tiltUrl + "export?format=csv";
        }

        return new URL(tiltUrl);
    }

    public List<TiltLog> readCSVStream(InputStream inputStream, Long brewId) {

        List<TiltLog> logs = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                if (null != values[2] && values[2].contains(SLASH)) {
                    logs.add(mapCsvEntry(values, brewId));
                }
            }
        } catch (IOException e) {
            log.error("Failed parsing tilt logs: " + e.getMessage());
        }

        return logs;
    }

    public TiltLog mapCsvEntry(String[] entry, Long brewId) {

        String dateString = entry[2];   // "6/13/2021 13:36:37"
        ZonedDateTime date = ZonedDateTime.parse(dateString, format);

        String gravityString = entry[3];      // "1.004"
        Integer gravity = Integer.valueOf(gravityString.replace(".", ""));

        String temperature = entry[4];  // "20.0"
        Double celsius = Double.parseDouble(temperature);

        String id = brewId + "_" + date.toEpochSecond();

        return new TiltLog(id, brewId, date, gravity, celsius);

    }

    public void notifyDiscord() {

        List<Brew> brews = brewService.findAllWithTiltUrl();
        for (Brew brew : brews) {
            if (null == brew.getTiltEnded()) {
                Optional<TiltLog> latest = tiltLogRepository.findTop1ByParentIdOrderByTimestampDesc(brew.getId());
                if (latest.isPresent()) {

                    TiltLog tiltLog = latest.get();
                    String message = String.format("Latest tilt log - Brew: %s - Timestamp: %s - Temp: %s - Gravity: %s", brew.getBrewName(), tiltLog.getTimestamp(), tiltLog.getTemperature(), tiltLog.getGravity());

                    try {
                        log.info("sending discord msg: [{}]", message);
                        discordService.sendMessage(message);
                    } catch (IOException e) {
                        log.error("failed sending discord message - error: {}", e.getMessage());
                    }
                }
            }
        }


    }
}
