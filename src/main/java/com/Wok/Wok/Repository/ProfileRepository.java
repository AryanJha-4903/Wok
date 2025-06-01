package com.Wok.Wok.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.Wok.Wok.Model.Profile;
import org.springframework.data.mongodb.repository.Query;
public interface ProfileRepository extends MongoRepository<Profile, String> {

    Optional<Profile> findByProfileId(String profileId);  // Optional is safer

    Profile findByUserId(String userId);

    Optional<Profile> findByEmail(String email);

    List<Profile> findByFirstName(String firstName);

    List<Profile> findByLastName(String lastName);

    List<Profile> findByDescription(String description);
    
    Page<Profile> findByUserIdIn(List<String> userIds, Pageable pageable);

    // Page<Profile> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
    @Query("{ '$or': [ {'firstName': { $regex: ?0, $options: 'i' }}, {'lastName': { $regex: ?0, $options: 'i' }} ] }")
    Page<Profile> searchByName(String keyword, Pageable pageable);
}
    