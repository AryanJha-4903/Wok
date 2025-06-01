package com.Wok.Wok.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.Wok.Wok.Model.Post;



public interface PostRepository extends MongoRepository<Post, String>{
    Page<Post> findAll(Pageable pageable);
    Page<Post> findByUsername(String username, Pageable pageable);
    Page<Post> findByCaptionContainingIgnoreCase(String caption, Pageable pageable);

}
