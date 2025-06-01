package com.Wok.Wok.Model;

// import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
// import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;


@Data
@Document(collection = "app_posts")
public class Post {

    @Id
    // @Field("_id")
    private String postId;
    // private String userId;
    private String username;
    private String caption;
    private List<String> mediaUrls;
    private Date createdAt = new Date();
    private int like = 0;


}
