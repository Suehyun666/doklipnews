package project.doklipnews.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import project.doklipnews.entity.Article;
import project.doklipnews.service.ArticleService;

@Controller
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    // 새 기사 작성 페이지
    @GetMapping("/write")
    public String writeForm() {
        return "articles/write";
    }

    // 새 기사 저장
    @PostMapping
    public String create(@ModelAttribute Article article, @RequestParam("imageFile") MultipartFile imageFile) {
        articleService.createArticle(article, imageFile); // 이미지 파일 처리 후 기사 저장
        return "redirect:/articles";
    }

    // 전체 기사 목록 조회
    @GetMapping
    public String list(@RequestParam(value = "category", required = false) String category, Model model) {
        model.addAttribute("articles", articleService.getArticlesByCategory(category));
        model.addAttribute("category", category);
        return "articles/list";
    }

    // 특정 기사 조회
    @GetMapping("/{id}")
    public String viewArticle(@PathVariable Long id, Model model) {
        Article article = articleService.findById(id);
        //String currentUser = getCurrentUser();
        model.addAttribute("article", article);
        //model.addAttribute("currentUser", currentUser);
        return "articles/detail";
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
        return "redirect:/articles";
    }

    // 기사 수정
    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Article article) {
        articleService.updateArticle(id, article);
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

}
