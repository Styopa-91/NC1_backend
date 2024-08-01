package com.project.nc1_backend.controller;

import com.project.nc1_backend.entity.NewsArticle;
import com.project.nc1_backend.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping
    public ResponseEntity<List<NewsArticle>> getAllNews() {
        return ResponseEntity.ok(newsService.getAllNews());
    }

    @PostMapping
    public ResponseEntity<NewsArticle> saveNewsArticle(@RequestBody NewsArticle newsArticle) {
        return ResponseEntity.ok(newsService.saveNews(newsArticle));
    }

    @GetMapping("/byPeriod/{period}")
    public ResponseEntity<List<NewsArticle>> getNewsArticlesByTimePeriod(@PathVariable("period") String period) {
        return ResponseEntity.ok(newsService.getArticlesByTimePeriod(period));
    }

    @PutMapping("{id}")
    public ResponseEntity<NewsArticle> updateNewsArticle(@PathVariable("id") long  id,
                                                         @RequestBody NewsArticle updatedNewsArticle) throws IOException {
        NewsArticle newsArticle = newsService.updateNewsArticle(id, updatedNewsArticle);
        return ResponseEntity.ok(newsArticle);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteNewsArticleById(@PathVariable("id") long  id) {
        newsService.deleteNewsArticle(id);
        return ResponseEntity.ok("News article deleted successfully!");
    }

}

