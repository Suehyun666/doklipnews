package project.doklipnews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.doklipnews.entity.Article;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByCategory(String category);
    List<Article> findByCategoryOrderByCreatedAtDesc(String category);
    List<Article> findAllByOrderByCreatedAtDesc();
}