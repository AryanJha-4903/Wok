package com.Wok.Wok.Model;

import lombok.Data;

@Data
public class PostLikeRequest {
    private String postId;
    private boolean increment;
    private String userId; // true to increment, false to decrement
}
