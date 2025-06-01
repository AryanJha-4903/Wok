package com.Wok.Wok.Controller;

import com.Wok.Wok.Model.Comment;
import com.Wok.Wok.Model.CommentProfileDTO;
// import com.Wok.Wok.Model.GetCommentsRequest;
import com.Wok.Wok.Services.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentProfileDTO> addComment(@RequestBody Comment comment) {
        CommentProfileDTO saved = commentService.addComment(comment);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/getComments")
    public ResponseEntity<List<CommentProfileDTO>> getComments(@RequestBody Map<String, String> payload) {
        String postId = payload.get("postId");
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }
}
