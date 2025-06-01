package com.Wok.Wok.Services;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// import com.Wok.Wok.Model.Post;
import com.Wok.Wok.Model.Profile;
// import com.Wok.Wok.Repository.PostRepository;
import com.Wok.Wok.Repository.ProfileRepository;

@Service
public class SearchService {
    @Autowired
    private ProfileRepository profileRepository;

    // @Autowired
    // private PostRepository postRepository;

    @Autowired
    private RestTemplate restTemplate;

    public Map<String, Object> searchAll(String query, int page, int size) {
        Map<String, Object> result = new HashMap<>();
        Pageable pageable = PageRequest.of(page, size);

        // Search profiles with pagination
        Page<Profile> profilesPage = profileRepository.searchByName(query, pageable);
        result.put("profiles", profilesPage.getContent());
        result.put("profilesTotalPages", profilesPage.getTotalPages());

        // Search posts with pagination
        // Page<Post> postsPage = postRepository.findByCaptionContainingIgnoreCase(query, pageable);
        // result.put("posts", postsPage.getContent());
        // result.put("postsTotalPages", postsPage.getTotalPages());

        // External search (MealDB has no pagination, slice manually)
        String mealdbUrl = "https://www.themealdb.com/api/json/v1/1/search.php?s=" + query;
        ResponseEntity<Map> mealResponse = restTemplate.getForEntity(mealdbUrl, Map.class);
        List<Object> allMeals = (List<Object>) mealResponse.getBody().get("meals");

        if (allMeals != null) {
            int fromIndex = page * size;
            int toIndex = Math.min(fromIndex + size, allMeals.size());
            List<Object> paginatedMeals = fromIndex < allMeals.size() ? allMeals.subList(fromIndex, toIndex) : Collections.emptyList();
            result.put("meals", paginatedMeals);
            result.put("mealsTotalPages", (int) Math.ceil((double) allMeals.size() / size));
        } else {
            result.put("meals", Collections.emptyList());
            result.put("mealsTotalPages", 0);
        }

        return result;
    }


}
