package com.project.nc1_backend.service;

import com.project.nc1_backend.entity.NewsArticle;
import com.project.nc1_backend.repository.NewsArticleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@EnableScheduling
public class NewsService {

    @Autowired
    private NewsArticleRepository newsArticleRepository;

    public List<NewsArticle> getAllNews() {
        return newsArticleRepository.findAllByOrderByPublicationTimeDesc();
    }

    public NewsArticle saveNews(NewsArticle newsArticle) {
        return newsArticleRepository.save(newsArticle);
    }

    public List<NewsArticle> getArticlesByTimePeriod(String period) {
        LocalTime startTime;
        LocalTime endTime;

        switch (period) {
            case "Morning":
                startTime = LocalTime.of(0, 0);  // 00:00
                endTime = LocalTime.of(12, 0);   // 12:00
                break;
            case "Day":
                startTime = LocalTime.of(12, 0); // 12:00
                endTime = LocalTime.of(18, 0);   // 18:00
                break;
            case "Evening":
                startTime = LocalTime.of(18, 0); // 18:00
                endTime = LocalTime.of(23, 59);  // 23:59
                break;
            default:
                throw new IllegalArgumentException("Invalid time period: " + period);
        }

        return newsArticleRepository.findArticlesByTimeRange(startTime, endTime);
    }

    @Scheduled(fixedRate = 1200000) // 20 minutes
    @Transactional
    public void parseNews() throws IOException {
        // Fetch and parse HTML document
        Document doc = Jsoup.connect("https://www.epravda.com.ua/news/").get();

        // Select the news
        Elements articles = doc.select(".article.article_news");
        List<NewsArticle> newsArticles = new ArrayList<>();
        for (Element article : articles) {
            NewsArticle newsArticle = new NewsArticle();
            newsArticle.setNewsHeadline(article.select("a").text());
            newsArticle.setNewsDescription(article.select(".article__subtitle").text());
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            LocalTime time = LocalTime.parse(article.select(".article__time").text(), timeFormatter);

            newsArticle.setPublicationTime(time);
            if (!newsArticles.isEmpty()) {
                NewsArticle firstDailyArticle = newsArticles.get(newsArticles.size() - 1);
                if (firstDailyArticle.getPublicationTime().isBefore(time)) {
                    break;
                }
            }

            newsArticles.add(newsArticle);
        }
        newsArticleRepository.deleteAll();
        newsArticleRepository.saveAll(newsArticles);
    }

    public NewsArticle updateNewsArticle(long id, NewsArticle updatedNewsArticle) {
        Optional<NewsArticle> optionalExistingArticle = newsArticleRepository.findById(id);

        if (optionalExistingArticle.isPresent()) {
            NewsArticle existingArticle = optionalExistingArticle.get();

            // Update the fields with the new data
            existingArticle.setNewsHeadline(updatedNewsArticle.getNewsHeadline());
            existingArticle.setNewsDescription(updatedNewsArticle.getNewsDescription());
            existingArticle.setPublicationTime(updatedNewsArticle.getPublicationTime());

            // Save the updated article back to the repository
            return newsArticleRepository.save(existingArticle);
        } else {
            // Handle the case where the article was not found
            throw new EntityNotFoundException("NewsArticle not found with id: " + id);
        }
    }

    public void deleteNewsArticle(long id) {
        newsArticleRepository.deleteById(id);
    }

}
