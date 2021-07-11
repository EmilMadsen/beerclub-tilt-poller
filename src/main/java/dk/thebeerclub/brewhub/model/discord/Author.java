package dk.thebeerclub.brewhub.model.discord;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Author {
    private String name;
    private String url;
    private String iconUrl;
}
