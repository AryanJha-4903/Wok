package com.Wok.Wok.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.Wok.Wok.Model.Post;
import com.Wok.Wok.Model.PostLikeRequest;
import com.Wok.Wok.Model.PostProfileDTO;
import com.Wok.Wok.Services.PostService;
// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
@RestController
@RequestMapping("/api/post")
public class PostController {
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);
	 @Autowired
    private PostService postService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Post createPost(
            @RequestParam String username,
            @RequestParam String caption,
            @RequestParam("files") List<MultipartFile> files
    ) throws IOException {
        return postService.createPost(username, caption, files);
    }

    @GetMapping("/getPost")
    public Page<PostProfileDTO> getPosts(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
    return postService.getAllPosts(pageable);
}

    @PostMapping("/like")
    public ResponseEntity<Post> updateLikeCount(@RequestBody PostLikeRequest request) {
        logger.info("Received like request: postId={}, increment={}, userId={}", request.getPostId(), request.isIncrement(), request.getUserId());
        Post updatedPost = postService.updateLikeCount(request.getPostId(), request.isIncrement(), request.getUserId());
        
        return ResponseEntity.ok(updatedPost);
    }

    @GetMapping("/username")
    public Page<PostProfileDTO> getPostsByUsername(
            @RequestParam String username,
            @PageableDefault(size = 10) Pageable pageable) {
        return postService.getPostsByUsername(username, pageable);
    }
}
