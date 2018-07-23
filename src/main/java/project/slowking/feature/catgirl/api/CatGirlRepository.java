package project.slowking.feature.catgirl.api;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Component
public class CatGirlRepository {

    private RestTemplate client;

    private final String api;
    private final String base;

    @Autowired
    public CatGirlRepository(RestTemplateBuilder builder,
                             @Value("${catgirl-api.base-address}") String base,
                             @Value("${catgirl-api.api-base}") String api){
        this.base = base;
        this.api = api;
        this.client = builder.build();
    }

    public CatGirl getImage(String id){
        return client.getForObject(base + api + "images/"+id,image.class, Collections.emptyMap())
                .getImage();
    }

    public CatGirl getRandomImage(){
        return client.getForObject(base + api + "random/image",images.class, Collections.emptyMap())
                .getImages().stream().findFirst().orElse(null);
    }

    public List<CatGirl> findByTags(List<String> tags, boolean nsfw, int count,  int skip)
    {
        TagSearch search = new TagSearch();
        search.setLimit(count);
        search.setSkip(skip);
        search.setNsfw(nsfw? null: false);
        search.setTags(tags);
        return client.postForObject(base + api + "images/search", search,images.class)
                .getImages();
    }


    @Data
    private static class image {
        private CatGirl image;
    }
    @Data
    private static class images {
        private List<CatGirl> images;
    }
    @Data
    private static class  TagSearch{
        private Boolean nsfw;
        private List<String> tags;
        private int limit;
        private int skip;
    }
}
