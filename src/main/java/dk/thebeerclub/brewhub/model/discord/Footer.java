package dk.thebeerclub.brewhub.model.discord;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Footer {
    private String text;
    private String iconUrl;
}
