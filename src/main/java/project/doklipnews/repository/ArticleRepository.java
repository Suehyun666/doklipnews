package project.doklipnews.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.doklipnews.controller.dto.ArticleDetailDTO;
import project.doklipnews.controller.dto.ArticleListDTO;
import project.doklipnews.entity.Article;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    // 목록 페이지용 DTO 조회 (카테고리별)
    @Query("SELECT new project.doklipnews.controller.dto.ArticleListDTO(a.id, a.title, a.author, a.createdAt, a.category, a.imageUrl, a.viewCount, a.likeCount, a.summary) FROM Article a WHERE a.category = ?1 ORDER BY a.createdAt DESC")
    Page<ArticleListDTO> findArticleListByCategory(String category, Pageable pageable);

    // 모든 기사 목록용 DTO 조회
    @Query("SELECT new project.doklipnews.controller.dto.ArticleListDTO(a.id, a.title, a.author, a.createdAt, a.category, a.imageUrl, a.viewCount, a.likeCount, a.summary) FROM Article a ORDER BY a.createdAt DESC")
    Page<ArticleListDTO> findAllArticleList(Pageable pageable);

    // 인기 기사 (조회수 기준) - DTO 반환
    @Query("SELECT new project.doklipnews.controller.dto.ArticleListDTO(a.id, a.title, a.author, a.createdAt, a.category, a.imageUrl, a.viewCount, a.likeCount, a.summary) FROM Article a ORDER BY a.viewCount DESC")
    List<ArticleListDTO> findTop10DTOByOrderByViewCountDesc(Pageable pageable);

    // 카테고리별 인기 기사 (조회수 기준) - DTO 반환
    @Query("SELECT new project.doklipnews.controller.dto.ArticleListDTO(a.id, a.title, a.author, a.createdAt, a.category, a.imageUrl, a.viewCount, a.likeCount, a.summary) FROM Article a WHERE a.category = :category ORDER BY a.viewCount DESC")
    List<ArticleListDTO> findTop10DTOByCategoryOrderByViewCountDesc(@Param("category") String category, Pageable pageable);

    // 추천 기사 - DTO 반환
    @Query("SELECT new project.doklipnews.controller.dto.ArticleListDTO(a.id, a.title, a.author, a.createdAt, a.category, a.imageUrl, a.viewCount, a.likeCount, a.summary) FROM Article a WHERE a.featured = true ORDER BY a.createdAt DESC")
    Page<ArticleListDTO> findDTOByFeaturedTrue(Pageable pageable);

    // 최신 기사 DTO 반환
    @Query("SELECT new project.doklipnews.controller.dto.ArticleListDTO(a.id, a.title, a.author, a.createdAt, a.category, a.imageUrl, a.viewCount, a.likeCount, a.summary) FROM Article a ORDER BY a.createdAt DESC")
    List<ArticleListDTO> findTop10DTOByOrderByCreatedAtDesc(Pageable pageable);

    // 카테고리별 최신 기사 DTO 반환
    @Query("SELECT new project.doklipnews.controller.dto.ArticleListDTO(a.id, a.title, a.author, a.createdAt, a.category, a.imageUrl, a.viewCount, a.likeCount, a.summary) FROM Article a WHERE a.category = :category ORDER BY a.createdAt DESC")
    List<ArticleListDTO> findTop10DTOByCategoryOrderByCreatedAtDesc(@Param("category") String category, Pageable pageable);

    // 좋아요 기준 인기 기사 DTO 반환
    @Query("SELECT new project.doklipnews.controller.dto.ArticleListDTO(a.id, a.title, a.author, a.createdAt, a.category, a.imageUrl, a.viewCount, a.likeCount, a.summary) FROM Article a ORDER BY a.likeCount DESC")
    List<ArticleListDTO> findTop10DTOByOrderByLikeCountDesc(Pageable pageable);

    // 키워드로 검색 - DTO 반환
    @Query("SELECT new project.doklipnews.controller.dto.ArticleListDTO(a.id, a.title, a.author, a.createdAt, a.category, a.imageUrl, a.viewCount, a.likeCount, a.summary) FROM Article a WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<ArticleListDTO> searchDTOByTitleOrContent(@Param("keyword") String keyword, Pageable pageable);

    // 카테고리별 키워드 검색 - DTO 반환
    @Query("SELECT new project.doklipnews.controller.dto.ArticleListDTO(a.id, a.title, a.author, a.createdAt, a.category, a.imageUrl, a.viewCount, a.likeCount, a.summary) FROM Article a WHERE (LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND a.category = :category")
    Page<ArticleListDTO> searchDTOByTitleOrContentAndCategory(
            @Param("keyword") String keyword,
            @Param("category") String category,
            Pageable pageable);

    // 페이징과 함께 카테고리별 기사 찾기
    Page<Article> findByCategory(String category, Pageable pageable);

    // 조회수 기준 인기 기사 (페이징 없이)
    List<Article> findTop10ByOrderByViewCountDesc();

    // 카테고리별 조회수 기준 인기 기사
    List<Article> findTop10ByCategoryOrderByViewCountDesc(String category);

    // 추천 기사 찾기
    Page<Article> findByFeaturedTrue(Pageable pageable);

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
    /**
     * 메인페이지에 필요한 모든 데이터를 한 번의 쿼리로 가져오기
     * UNION ALL 사용으로 여러 쿼리를 하나로 통합
     */
    @Query(value = """
        (SELECT 'featured' as type, id, title, author, created_at, category, image_url, view_count, like_count, summary 
         FROM article WHERE featured = 1 ORDER BY created_at DESC LIMIT 1)
        UNION ALL
        (SELECT 'trending' as type, id, title, author, created_at, category, image_url, view_count, like_count, summary 
         FROM article ORDER BY view_count DESC LIMIT 10)
        UNION ALL
        (SELECT 'liked' as type, id, title, author, created_at, category, image_url, view_count, like_count, summary 
         FROM article ORDER BY like_count DESC LIMIT 10)
        UNION ALL
        (SELECT 'latest' as type, id, title, author, created_at, category, image_url, view_count, like_count, summary 
         FROM article ORDER BY created_at DESC LIMIT 10)
        """, nativeQuery = true)
    List<Object[]> findMainPageDataUnified();

    /**
     * 카테고리별 메인페이지 데이터 한 번에 가져오기
     */
    @Query(value = """
        (SELECT 'featured' as type, id, title, author, created_at, category, image_url, view_count, like_count, summary 
         FROM article WHERE featured = 1 ORDER BY created_at DESC LIMIT 1)
        UNION ALL
        (SELECT 'trending' as type, id, title, author, created_at, category, image_url, view_count, like_count, summary 
         FROM article WHERE category = :category ORDER BY view_count DESC LIMIT 10)
        UNION ALL
        (SELECT 'liked' as type, id, title, author, created_at, category, image_url, view_count, like_count, summary 
         FROM article ORDER BY like_count DESC LIMIT 10)
        UNION ALL
        (SELECT 'latest' as type, id, title, author, created_at, category, image_url, view_count, like_count, summary 
         FROM article WHERE category = :category ORDER BY created_at DESC LIMIT 10)
        """, nativeQuery = true)
    List<Object[]> findMainPageDataByCategoryUnified(@Param("category") String category);

    /**
     * 배치로 여러 카테고리의 최신 기사들을 한 번에 가져오기
     */
    @Query(value = """
        SELECT DISTINCT ON (category) category, id, title, author, created_at, image_url, view_count, like_count, summary
        FROM article 
        WHERE category IN :categories 
        ORDER BY category, created_at DESC
        """, nativeQuery = true)
    List<Object[]> findLatestByCategories(@Param("categories") List<String> categories);

    /**
     * 사이드바용 차트 데이터 한 번에 가져오기
     */
    @Query(value = """
        SELECT id, title, image_url, 'chart' as type
        FROM article 
        WHERE image_url IS NOT NULL 
        ORDER BY view_count DESC 
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> findChartData();
}