package com.Wok.Wok.Repository;

import com.Wok.Wok.Model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByPostId(String postId);
}
