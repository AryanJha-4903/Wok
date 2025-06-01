package com.Wok.Wok.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Wok.Wok.Services.SearchService;
@RestController
@RequestMapping("api")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> search(
    @RequestParam String query,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
    ) {
    Map<String, Object> result = searchService.searchAll(query, page, size);
    return ResponseEntity.ok(result);
    }
}
