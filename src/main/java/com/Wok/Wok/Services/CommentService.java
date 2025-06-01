package com.Wok.Wok.Services;

// import com.Wok.Wok.Controller.PostController;
import com.Wok.Wok.Model.Comment;
import com.Wok.Wok.Model.CommentProfileDTO;
import com.Wok.Wok.Model.Profile;
import com.Wok.Wok.Model.user;
import com.Wok.Wok.Repository.CommentRepository;
import com.Wok.Wok.Repository.ProfileRepository;
import com.Wok.Wok.Repository.UserRepository;

// import org.hibernate.engine.jdbc.env.internal.LobCreationLogging_.logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    // @Autowired
    // private CommentProfileDTO 
    // private final CommentRepository commentRepository;

    // public CommentService(CommentRepository commentRepository) {
    //     this.commentRepository = commentRepository;
    // }

    // public Comment addComment(Comment comment) {
    //     return commentRepository.save(comment);
    // }

    public CommentProfileDTO addComment(Comment comment) {
    Comment savedComment = commentRepository.save(comment);
    logger.info("Comment saved: {}", savedComment);

    Optional<user> userOptional = userRepository.findByUsername(savedComment.getUsername());

    if (userOptional.isPresent()) {
        user userObj = userOptional.get();
        Profile profile = profileRepository.findByUserId(userObj.getId());

        if (profile != null) {
            return new CommentProfileDTO(savedComment, profile);
        }
    }

    // Return with null profile or handle as needed
    return new CommentProfileDTO(savedComment, null);
}

    public List<CommentProfileDTO> getCommentsByPostId(String postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        List<CommentProfileDTO> result = new ArrayList<>();

        for (Comment comment : comments) {
            String username = comment.getUsername();
            Optional<user> userOptional = userRepository.findByUsername(username);

            if (userOptional.isPresent()) {
                user userObj = userOptional.get();
                Profile profile = profileRepository.findByUserId(userObj.getId());
                if (profile != null) {
                    result.add(new CommentProfileDTO(comment, profile));
                }
            }
        }

        return result;
    }
}
