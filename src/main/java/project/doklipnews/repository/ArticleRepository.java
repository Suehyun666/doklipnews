package project.doklipnews.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    //앞에 뜨기 리셋
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Article a SET a.featured = false WHERE a.featured = true")
    void updateAllFeaturedToFalse();

    // 검색 관련 메소드 추가

    // 키워드로 제목 또는 내용 검색
    @Query("SELECT a FROM Article a WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Article> searchByTitleOrContent(@Param("keyword") String keyword, Pageable pageable);

    // 카테고리별 키워드 검색 (제목 또는 내용)
    @Query("SELECT a FROM Article a WHERE (LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND a.category = :category")
    Page<Article> searchByTitleOrContentAndCategory(
            @Param("keyword") String keyword,
            @Param("category") String category,
            Pageable pageable);

    // 이미지가 있는 기사 검색 (최신순)
    @Query("SELECT a FROM Article a WHERE (LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND a.imageUrl IS NOT NULL AND a.imageUrl <> '' ORDER BY a.createdAt DESC")
    List<Article> findRecentArticlesWithImagesByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 카테고리별로 이미지가 있는 기사 검색 (최신순)
    @Query("SELECT a FROM Article a WHERE (LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND a.category = :category AND a.imageUrl IS NOT NULL AND a.imageUrl <> '' ORDER BY a.createdAt DESC")
    List<Article> findRecentArticlesWithImagesByKeywordAndCategory(
            @Param("keyword") String keyword,
            @Param("category") String category,
            Pageable pageable);

    // 특정 ID 목록을 제외한 기사 검색 (키워드 기준)
    @Query("SELECT a FROM Article a WHERE (LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND a.id NOT IN :excludeIds ORDER BY a.createdAt DESC")
    List<Article> findArticlesByKeywordExcludingIds(
            @Param("keyword") String keyword,
            @Param("excludeIds") List<Long> excludeIds,
            Pageable pageable);

    // 카테고리와 키워드로 특정 ID 목록을 제외한 기사 검색
    @Query("SELECT a FROM Article a WHERE (LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND a.category = :category AND a.id NOT IN :excludeIds ORDER BY a.createdAt DESC")
    List<Article> findArticlesByKeywordAndCategoryExcludingIds(
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("excludeIds") List<Long> excludeIds,
            Pageable pageable);


}