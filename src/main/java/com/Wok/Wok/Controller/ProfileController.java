package com.Wok.Wok.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.Wok.Wok.Model.AuthResponse;
import com.Wok.Wok.Model.Profile;
import com.Wok.Wok.Model.userIdRequest;
import com.Wok.Wok.Repository.ProfileRepository;
import com.Wok.Wok.Services.JwtService;
import com.Wok.Wok.Services.ProfileService;
import io.jsonwebtoken.Claims;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("api")
public class ProfileController {

  
    // @Autowired
    // private UserRepository userRepository;
    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private ProfileService profileService;
    
    @PostMapping("/createProfile")
    public ResponseEntity<?> createProfile(@RequestBody Profile profile) {
      return profileService.createProfile(profile);
    }


    @PostMapping("/getClaims")
    private Claims getUserIdFromToken(@RequestBody AuthResponse token) {
        return jwtService.extractClaims(token.getToken());
    }

    @PostMapping("/getProfile")
    public ResponseEntity<Profile> getProfile(@RequestBody userIdRequest request) {
        Profile profile = profileRepository.findByUserId(request.getUserId());
        // System.out.printf("profile obj=>", profile.getLastName())
        if (profile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(profile);      
    }

    @PostMapping("/friends/update")
    public ResponseEntity<String> updateFriendList(
            @RequestParam String currentUserId,
            @RequestParam String friendUserId,
            @RequestParam boolean add) {

        profileService.updateFriendList(currentUserId, friendUserId, add);

        String action = add ? "added to" : "removed from";
        return ResponseEntity.ok("Friend " + friendUserId + " " + action + " user " + currentUserId + "'s friend list.");
    }

    @GetMapping("/friends")
    public ResponseEntity<Page<Profile>> getFriendsProfiles(
        @RequestParam String userId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Profile> profiles = profileService.getFriendsProfiles(userId, pageable);
        return ResponseEntity.ok(profiles);
    }
    
    
    


}
