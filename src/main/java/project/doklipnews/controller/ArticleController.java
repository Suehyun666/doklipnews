package project.doklipnews.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import project.doklipnews.entity.Article;
import project.doklipnews.service.ArticleService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    // 새 기사 작성 페이지
    @GetMapping("/admin-secret-xzy123/write")
    public String writeForm() {
        return "articles/write";
    }

    // 새 기사 저장
    @PostMapping
    public String create(@ModelAttribute Article article, @RequestParam("imageFile") MultipartFile imageFile) {
        articleService.createArticle(article, imageFile); // 이미지 파일 처리 후 기사 저장
        return "redirect:/articles";
    }

    // 특정 기사 조회
    @GetMapping("/{id}")
    public String viewArticle(@PathVariable Long id, Model model) {
        Article article = articleService.findById(id); // 조회수 증가
        model.addAttribute("article", article);

        List<Article> relatedArticles = articleService.findLatestArticlesByCategory(article.getCategory());
        model.addAttribute("relatedArticles", relatedArticles);

        return "articles/detail";
    }

    // 기사 좋아요 API
    @PostMapping("/{id}/like")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> likeArticle(@PathVariable Long id) {
        Article article = articleService.likeArticle(id);
        Map<String, Object> response = new HashMap<>();
        response.put("likeCount", article.getLikeCount());
        return ResponseEntity.ok(response);
    }

    // 기사 좋아요 취소 API
    @PostMapping("/{id}/unlike")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> unlikeArticle(@PathVariable Long id) {
        Article article = articleService.unlikeArticle(id);
        Map<String, Object> response = new HashMap<>();
        response.put("likeCount", article.getLikeCount());
        return ResponseEntity.ok(response);
    }

    // 인기 기사 API (모든 카테고리)
    @GetMapping("/trending")
    @ResponseBody
    public List<Article> getTrendingArticles() {
        return articleService.findTrendingArticles();
    }

    // 카테고리별 인기 기사 API
    @GetMapping("/trending/{category}")
    @ResponseBody
    public List<Article> getTrendingArticlesByCategory(@PathVariable String category) {
        return articleService.findTrendingArticlesByCategory(category);
    }

    // 좋아요 기준 인기 기사 API
    @GetMapping("/most-liked")
    @ResponseBody
    public List<Article> getMostLikedArticles() {
        return articleService.findMostLikedArticles();
    }

    // 메인 페이지 (추천 기사, 그리드, 인기, 오피니언)
    @GetMapping("/featured")
    public String featuredArticles(Model model) {
        List<Article> headlineArticles = articleService.findFeaturedArticles(PageRequest.of(0, 1)).getContent();
        List<Article> gridArticles = articleService.findAll(PageRequest.of(0, 4)).getContent();
        List<Article> trendingArticles = articleService.findTrendingArticles();
        List<Article> opinionArticles = articleService.findByCategory("opinion", PageRequest.of(0, 4)).getContent();

        model.addAttribute("headline", headlineArticles.isEmpty() ? null : headlineArticles.get(0));
        model.addAttribute("gridArticles", gridArticles);
        model.addAttribute("trendingArticles", trendingArticles);
        model.addAttribute("opinionArticles", opinionArticles);

        return "index"; // 메인 페이지 템플릿 반환
    }

    // 카테고리 페이지
    @GetMapping("/category")
    public String categoryPage(
            @RequestParam(required = false, defaultValue = "politics") String category,
            @RequestParam(required = false, defaultValue = "all") String subcategory,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        // 카테고리 정보 미리 처리
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> categoryInfo = getCategoryData().get(category);
        if (categoryInfo != null) {
            model.addAttribute("categoryTitle", categoryInfo.get("title"));
            model.addAttribute("categoryDescription", categoryInfo.get("description"));
            model.addAttribute("subcategories", categoryInfo.get("subcategories"));
        }

        // 나머지 코드는 그대로 유지
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Article> articles;

        if (category != null && !category.isEmpty()) {
            articles = articleService.findByCategory(category, pageable);
        } else {
            articles = articleService.findAll(pageable);
        }

        // 모델에 데이터 추가
        model.addAttribute("articles", articles.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articles.getTotalPages());
        model.addAttribute("category", category);
        model.addAttribute("subcategory", subcategory);

        // 관련 기사들 추가
        model.addAttribute("trendingArticles", articleService.findTrendingArticlesByCategory(category));
        model.addAttribute("latestArticles", articleService.findLatestArticlesByCategory(category));
        model.addAttribute("opinionArticles", articleService.findByCategory("opinion", PageRequest.of(0, 4)).getContent());

        // 원래 JSON 데이터도 전달 (기존 템플릿 유지)
        model.addAttribute("categoryDataJson", getCategoryData());

        return "articles/category"; // category.html 템플릿 반환
    }

    // 기사 수정 페이지
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("article", articleService.getArticleById(id));
        return "articles/edit";
    }

    // 기사 삭제
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return "redirect:/main";
    }

    // 기사 수정
    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Article article,
                         @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        articleService.updateArticle(id, article, imageFile);
        return "redirect:/articles/" + id;
    }

    // 예외 처리
    @ExceptionHandler(ResponseStatusException.class)
    public String handleNotFound(ResponseStatusException ex, Model model) {
        model.addAttribute("errorMessage", ex.getReason());
        return "error/404";
    }


    public String getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User) {
            return ((User) principal).getUsername();  // 현재 사용자의 이름 반환
        }
        return null;
    }

    // JavaScript 코드에서 사용할 카테고리 데이터 생성
    private Map<String, Map<String, Object>> getCategoryData() {
        Map<String, Map<String, Object>> categoryData = new HashMap<>();

        // 정치 카테고리 데이터
        Map<String, Object> politics = new HashMap<>();
        politics.put("title", "정치");
        politics.put("description", "국내 정치, 국회, 청와대, 정당, 북한 관련 최신 뉴스와 기사를 제공합니다.");

        Map<String, String> politicsSubcategories = new HashMap<>();
        politicsSubcategories.put("all", "전체");
        politicsSubcategories.put("bluehouse", "청와대");
        politicsSubcategories.put("assembly", "국회");
        politicsSubcategories.put("parties", "정당");
        politicsSubcategories.put("northkorea", "북한");

        politics.put("subcategories", politicsSubcategories);
        categoryData.put("politics", politics);

        // 경제 카테고리 데이터 (다른 카테고리도 마찬가지로 추가)

        return categoryData;
    }
}