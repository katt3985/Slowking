package project.slowking.feature.catgirl.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CatGirl {

    private String id;
    private String originalHash;

    private String artist;

    private CGAUser uploader;
    private CGAUser approver;

    private long favorites;
    private long likes;

    private List<String> tags;
    private boolean nsfw;

    @JsonFormat                     //2018-02-23T03:56:44.116Z
            (shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date createdAt;




}
