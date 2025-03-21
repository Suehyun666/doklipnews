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
import project.doklipnews.controller.dto.ArticleDetailDTO;
import project.doklipnews.controller.dto.ArticleListDTO;
import project.doklipnews.entity.Article;
import project.doklipnews.entity.Comment;
import project.doklipnews.service.ArticleService;
import project.doklipnews.service.CommentService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final CommentService commentService;

    public ArticleController(ArticleService articleService, CommentService commentService) {
        this.articleService = articleService;
        this.commentService = commentService;
    }

    // 새 기사 작성 페이지
    @GetMapping("/admin-secret-xzy123/write")
    public String writeForm() {
        return "articles/write";
    }

    // 새 기사 저장
    @PostMapping
    public String create(@ModelAttribute Article article, @RequestParam("imageFile") MultipartFile imageFile) {
        // 먼저 기사를 저장
        Article savedArticle = articleService.createArticle(article, imageFile);

        // 특집 기사로 체크된 경우 setAsFeatured 메서드 호출
        if (article.getFeatured() != null && article.getFeatured()) {
            articleService.setAsFeatured(savedArticle.getId());
        }
        return "redirect:/articles";
    }

    // 특정 기사 조회 - DTO 사용으로 성능 개선
    @GetMapping("/{id}")
    public String viewArticle(@PathVariable Long id, Model model) {
        ArticleDetailDTO article = articleService.findByIdDTO(id); // DTO 사용
        model.addAttribute("article", article);

        List<ArticleListDTO> relatedArticles = articleService.findLatestArticlesByCategoryDTO(article.getCategory());
        model.addAttribute("relatedArticles", relatedArticles);

        List<Comment> comments = commentService.getCommentsByArticleId(id);
        model.addAttribute("comments", comments);

        return "articles/detail";
    }

    // 기사 좋아요 API - 필요한 정보만 반환
    @PostMapping("/{id}/like")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> likeArticle(@PathVariable Long id) {
        Article article = articleService.likeArticle(id);
        Map<String, Object> response = new HashMap<>();
        response.put("likeCount", article.getLikeCount());
        return ResponseEntity.ok(response);
    }

    // 기사 좋아요 취소 API - 필요한 정보만 반환
    @PostMapping("/{id}/unlike")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> unlikeArticle(@PathVariable Long id) {
        Article article = articleService.unlikeArticle(id);
        Map<String, Object> response = new HashMap<>();
        response.put("likeCount", article.getLikeCount());
        return ResponseEntity.ok(response);
    }

    // 인기 기사 API (모든 카테고리) - DTO 사용으로 성능 개선
    @GetMapping("/trending")
    @ResponseBody
    public List<ArticleListDTO> getTrendingArticles() {
        return articleService.findTrendingArticlesDTO();
    }

    // 카테고리별 인기 기사 API - DTO 사용으로 성능 개선
    @GetMapping("/trending/{category}")
    @ResponseBody
    public List<ArticleListDTO> getTrendingArticlesByCategory(@PathVariable String category) {
        return articleService.findTrendingArticlesByCategoryDTO(category);
    }

    // 좋아요 기준 인기 기사 API - DTO 사용으로 성능 개선
    @GetMapping("/most-liked")
    @ResponseBody
    public List<ArticleListDTO> getMostLikedArticles() {
        return articleService.findMostLikedArticlesDTO();
    }

    // 메인 페이지 (추천 기사, 그리드, 인기, 오피니언) - DTO 사용으로 성능 개선
    @GetMapping("/featured")
    public String featuredArticles(Model model) {
        List<ArticleListDTO> headlineArticles = articleService.findFeaturedArticlesDTO(PageRequest.of(0, 1)).getContent();
        List<ArticleListDTO> gridArticles = articleService.findAllDTO(PageRequest.of(0, 4)).getContent();
        List<ArticleListDTO> trendingArticles = articleService.findTrendingArticlesDTO();
        List<ArticleListDTO> opinionArticles = articleService.findByCategoryDTO("opinion", PageRequest.of(0, 4)).getContent();

        model.addAttribute("headline", headlineArticles.isEmpty() ? null : headlineArticles.get(0));
        model.addAttribute("gridArticles", gridArticles);
        model.addAttribute("trendingArticles", trendingArticles);
        model.addAttribute("opinionArticles", opinionArticles);

        return "index"; // 메인 페이지 템플릿 반환
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
        Article updatedArticle = articleService.updateArticle(id, article, imageFile);
        // 특집 기사로 체크된 경우 setAsFeatured 메서드 호출
        if (article.getFeatured() != null && article.getFeatured()) {
            articleService.setAsFeatured(id);
        }
        return "redirect:/articles/" + id;
    }

    // 예외 처리
    @ExceptionHandler(ResponseStatusException.class)
    public String handleNotFound(ResponseStatusException ex, Model model) {
        model.addAttribute("errorMessage", ex.getReason());
        return "error/404";
    }

    @PostMapping("/{id}/set-featured")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> setFeatured(@PathVariable Long id) {
        Article article = articleService.setAsFeatured(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("featured", article.getFeatured());
        return ResponseEntity.ok(response);
    }
}