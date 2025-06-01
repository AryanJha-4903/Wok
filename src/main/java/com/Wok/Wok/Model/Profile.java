package com.Wok.Wok.Model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.Data;

@Data
@Document(collection = "app_userProfile")
public class Profile {

    @Id
    @Field("_id")
    private String profileId;

    public String firstName;
    private String lastName;
    private String description;
    private String profilePicture;
    private String email;
    private List<String> likedPostIds = new ArrayList<>();
    private List<String> savedPostIds = new ArrayList<>();
    private List<String> friendUserIds  = new ArrayList<>();

    @Indexed(unique = true)
    private String userId;
}
