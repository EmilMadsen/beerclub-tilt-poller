package dk.thebeerclub.brewhub.service;

import com.google.gson.Gson;
import dk.thebeerclub.brewhub.model.discord.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

@Service
public class DiscordService {

    @Value("${discord.webhook.url}")
    private String webhookUrl;

    private final Gson gson = new Gson();

    public void sendMessage(String message) throws IOException {
        RequestObject request = buildRequest(message);
        sendRequest(request);
    }

    private RequestObject buildRequest(String message) {

        RequestObject request = new RequestObject();
        if (ObjectUtils.isEmpty(message)) {
            throw new IllegalArgumentException("Set content or add at least one EmbedObject");
        }

        request.setContent(message);
        request.setTts(true);
        request.setUsername("Tilt-Bot");

        return request;
    }

    private void sendRequest(RequestObject request) throws IOException {

        URL url = new URL(webhookUrl);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.addRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        OutputStream stream = connection.getOutputStream();
        stream.write(gson.toJson(request).getBytes());
        stream.flush();
        stream.close();

        connection.getInputStream().close(); //I'm not sure why but it doesn't work without getting the InputStream
        connection.disconnect();

    }


}
