package com.Wok.Wok.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "app_comments")
public class Comment {

    @Id
    private String id;

    private String postId;
    private String username;
    private String text;
    private Date createdAt = new Date();
}
