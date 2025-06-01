package com.Wok.Wok.Services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// import java.util.Optional;

// import org.hibernate.engine.jdbc.env.internal.LobCreationLogging_.logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
// import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;


import com.Wok.Wok.Model.Profile;  // Import the correct Profile class
import com.Wok.Wok.Repository.ProfileRepository;

@Service
public class ProfileService {
     private static final Logger logger = LoggerFactory.getLogger(ProfileService.class);
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired 
    private ProfileRepository profileRepository;

    public ResponseEntity<?> createProfile(Profile userProfile) {
        try {
            // Check if profile exists by userId
            Query query = new Query();
            query.addCriteria(Criteria.where("userId").is(userProfile.getUserId()));

            // Prepare update
            Update update = new Update()
                    .set("firstName", userProfile.getFirstName())
                    .set("lastName", userProfile.getLastName())
                    .set("email", userProfile.getEmail())
                    .set("description", userProfile.getDescription())
                    .set("profilePicture", userProfile.getProfilePicture());

            // Atomic update, will insert if not found
            Profile updatedProfile = mongoTemplate.findAndModify(query, update, Profile.class);

            if (updatedProfile == null) {
                // If no profile found, insert the new one
                userProfile.setProfileId(null); // clear profileId to let MongoDB generate it
                updatedProfile = profileRepository.save(userProfile);
            }

            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong: " + e.getMessage());
        }
    }


    public void updateFriendList(String currentUserId, String friendUserId, boolean add) {
        logger.info("Updating friend list for userId: {}, action: {}, targetFriendId: {}", currentUserId, add ? "ADD" : "REMOVE", friendUserId);
        try {
            Query query = new Query(Criteria.where("userId").is(currentUserId));
            Update update = new Update();

            if (add) {
                update.addToSet("friendUserIds", friendUserId); // add without duplicates
            } else {
                update.pull("friendUserIds", friendUserId); // remove if exists
            }

            Profile result = mongoTemplate.findAndModify(query, update, Profile.class);

            if (result == null) {
                logger.warn("No profile found for userId: {}, creating a new one.", currentUserId);
                Profile newProfile = new Profile();
                newProfile.setUserId(currentUserId);

                if (add) {
                    newProfile.getFriendUserIds().add(friendUserId);
                }

                profileRepository.save(newProfile);
            }

            logger.info("Successfully {} friendId: {} for userId: {}", add ? "added" : "removed", friendUserId, currentUserId);

        } catch (Exception e) {
            logger.error("Failed to update friend list for userId: {}", currentUserId, e);
        }
    }

    public Page<Profile> getFriendsProfiles(String currentUserId, Pageable pageable) {
        Profile profile = profileRepository.findByUserId(currentUserId);
        if (profile == null || profile.getFriendUserIds() == null) {
        return Page.empty(pageable);
        }
        List<String> friendIds = profile.getFriendUserIds();
        return profileRepository.findByUserIdIn(friendIds, pageable);   
    }



    
}
