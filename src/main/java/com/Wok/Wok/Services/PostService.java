package com.Wok.Wok.Services;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.web.multipart.MultipartFile;

import com.Wok.Wok.Controller.PostController;
import com.Wok.Wok.Model.Post;
import com.Wok.Wok.Model.PostProfileDTO;
import com.Wok.Wok.Model.Profile;
import com.Wok.Wok.Model.user;
import com.Wok.Wok.Repository.PostRepository;
import com.Wok.Wok.Repository.ProfileRepository;
import com.Wok.Wok.Repository.UserRepository;
// import com.Wok.Wok.Model.user;
// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;
// import org.springframework.data.mongodb.core.MongoTemplate;
// import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.io.File;
import java.io.IOException;
// import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);
    @Value("${upload.dir}")
    private String uploadDir;

    // private final String UPLOAD_DIR = uploadDir + "uploads/";
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public Post findPostByObjectId(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(id)));

        return mongoTemplate.findOne(query, Post.class);
    }

    public Post createPost(String username, String caption, List<MultipartFile> files) throws IOException {
        List<String> mediaUrls = new ArrayList<>();
        String finalUploadPath = uploadDir ;
        // String finalUploadPath = Paths.get(uploadDir).toString()  ;
        logger.info("mediafile path= {}",finalUploadPath);
        for (MultipartFile file : files) {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File dest = new File(finalUploadPath + fileName);
        System.out.println("File exists: " + dest.exists());
        System.out.println("Absolute path: " + dest.getAbsolutePath());
        dest.getParentFile().mkdirs();
        file.transferTo(dest);
        mediaUrls.add("/media/" + fileName);
    }

        Post post = new Post();
        post.setUsername(username);
        post.setCaption(caption);
        post.setMediaUrls(mediaUrls);
        return postRepository.save(post);
    }


    public Page<PostProfileDTO> getAllPosts(Pageable pageable) {
         Pageable sortedPageable = PageRequest.of(
        pageable.getPageNumber(),
        pageable.getPageSize(),
        Sort.by(Sort.Direction.DESC, "createdDate")
    );
        Page<Post> postsPage = postRepository.findAll(sortedPageable);
        List<PostProfileDTO> dtoList = new ArrayList<>();

        for (Post post : postsPage.getContent()) {
            String username = post.getUsername();
            Optional<user> userOptional = userRepository.findByUsername(username);

            if (userOptional.isPresent()) {
                user userObj = userOptional.get();
                Profile profile = profileRepository.findByUserId(userObj.getId());
                if (profile != null) {
                    dtoList.add(new PostProfileDTO(post, profile));
                }
            }
        }

        return new PageImpl<>(dtoList, pageable, postsPage.getTotalElements());
    }

    public Post updateLikeCount(String postIdStr, boolean increment, String userId) {
        logger.info("Updating like count for postId: {}, increment: {}, userId: {}", postIdStr, increment, userId);

        Post post = postRepository.findById(postIdStr)
                .orElseThrow(() -> {
                    logger.error("Post not found with ID: {}", postIdStr);
                    return new RuntimeException("Post not found");
                });

        int currentLikes = post.getLike();
        int updatedLikes = increment ? currentLikes + 1 : Math.max(0, currentLikes - 1);
        post.setLike(updatedLikes);
        Post updatedPost = postRepository.save(post);

        try {
            Query query = new Query(Criteria.where("userId").is(userId));

            Update update = new Update();
            if (increment) {
                update.addToSet("likedPostIds", postIdStr); // Adds if not already in list
            } else {
                update.pull("likedPostIds", postIdStr); // Removes if exists
            }

            Profile result = mongoTemplate.findAndModify(query, update, Profile.class);

            if (result == null) {
                logger.warn("No profile found for userId: {}, creating a new one.", userId);
                Profile newProfile = new Profile();
                newProfile.setUserId(userId);
                newProfile.getLikedPostIds().add(postIdStr);
                profileRepository.save(newProfile);
            }

        } catch (Exception e) {
            logger.error("Failed to update likedPostIds for userId: {}", userId, e);
        }

        logger.info("Updated likes for postId: {} to {}", postIdStr, updatedLikes);
        return updatedPost;
    }
    
    public Page<PostProfileDTO> getPostsByUsername(String username, Pageable pageable) {
        Optional<user> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
        return Page.empty(); // or throw custom exception
        }
        user userObj = userOptional.get();
        Profile profile = profileRepository.findByUserId(userObj.getId());
        if (profile == null) {
            return Page.empty();
        }

        Page<Post> postsPage = postRepository.findByUsername(username, pageable);
        List<PostProfileDTO> dtoList = postsPage.getContent().stream()
                .map(post -> new PostProfileDTO(post, profile))
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, postsPage.getTotalElements());
    }


}
