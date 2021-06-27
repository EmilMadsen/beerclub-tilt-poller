package dk.thebeerclub.brewhub.model;

import lombok.Data;

import javax.persistence.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@Table(name = "brew")
@Data
public class Brew {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "brew_name")
    private String brewName;

    @Column(name = "brew_type")
    private String brewType;

    @Column(name = "created")
    private ZonedDateTime created = ZonedDateTime.now(ZoneId.of("UTC"));

    @Column(name = "brewsters")
    private String brewsters;

    @Column(name = "location")
    private String location;

    @Column(name = "recipe")
    private String recipe;

    @Column(name = "description")
    private String description;

    @Column(name = "tilt_url")
    private String tiltUrl;

    @Column(name = "target_start_gravity")
    private Integer targetStartGravity;

    @Column(name = "actual_start_gravity")
    private Integer actualStartGravity;

    @Column(name = "actual_end_gravity")
    private Integer actualEndGravity;

    @Column(name = "target_end_gravity")
    private Integer targetEndGravity;

}
