package project.slowking.feature.catgirl.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CGAUser {
    private String username;
    private String id;
}
