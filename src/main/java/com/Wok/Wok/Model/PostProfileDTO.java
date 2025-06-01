package com.Wok.Wok.Model;

import lombok.Data;

@SuppressWarnings("unused")
@Data
public class PostProfileDTO {
    
    private Post post;
    private Profile profile;
    // constructor
    public PostProfileDTO(Post post, Profile profile) {
        this.post = post;
        this.profile = profile;
    }
    // getters and setters
}
