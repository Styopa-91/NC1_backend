package com.project.nc1_backend.repository;

import com.project.nc1_backend.entity.NewsArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {

    List<NewsArticle> findAllByOrderByPublicationTimeDesc();

    @Query("SELECT n FROM NewsArticle n WHERE n.publicationTime BETWEEN :startTime AND :endTime ORDER BY n.publicationTime DESC")
    List<NewsArticle> findArticlesByTimeRange(@Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime);

}
