package project.doklipnews.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import project.doklipnews.entity.Article;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    // 카테고리별 기사 찾기 (날짜 내림차순)
    List<Article> findByCategoryOrderByCreatedAtDesc(String category);

    // 모든 기사 날짜 내림차순 정렬
    List<Article> findAllByOrderByCreatedAtDesc();

    // 페이징과 함께 카테고리별 기사 찾기
    Page<Article> findByCategory(String category, Pageable pageable);

    // 조회수 기준 인기 기사 (페이징 없이)
    List<Article> findTop10ByOrderByViewCountDesc();

    // 카테고리별 조회수 기준 인기 기사
    List<Article> findTop10ByCategoryOrderByViewCountDesc(String category);

    // 추천 기사 찾기
    Page<Article> findByFeaturedTrue(Pageable pageable);

    // 카테고리별 추천 기사 찾기
    Page<Article> findByCategoryAndFeaturedTrue(String category, Pageable pageable);

    // 최신 기사
    List<Article> findTop10ByOrderByCreatedAtDesc();

    // 카테고리별 최신 기사
    List<Article> findTop10ByCategoryOrderByCreatedAtDesc(String category);

    // 좋아요 기준 인기 기사
    List<Article> findTop10ByOrderByLikeCountDesc();

    List<Article> findByFeaturedTrue();


}