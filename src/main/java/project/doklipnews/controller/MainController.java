package project.doklipnews.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import project.doklipnews.controller.dto.ArticleListDTO;
import project.doklipnews.entity.Article;
import project.doklipnews.service.ArticleService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {
    private ArticleService articleService;
    @Autowired
    public MainController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/main";
    }
    @GetMapping("/main")
    public String mainPage(
            @RequestParam(required = false) String category,
            Model model) {

        // 헤드라인 기사 (추천 기사 중 최신)
        List<ArticleListDTO> headlineArticles = articleService.findFeaturedArticlesDTO(PageRequest.of(0, 1)).getContent();

        // 조회수 기준 인기 기사
        List<ArticleListDTO> trendingArticles;
        if (category != null && !category.isEmpty()) {
            trendingArticles = articleService.findTrendingArticlesByCategoryDTO(category);
        } else {
            trendingArticles = articleService.findTrendingArticlesDTO();
        }

        // 좋아요 기준 인기 기사
        List<ArticleListDTO> mostLikedArticles = articleService.findMostLikedArticlesDTO();

        // 최신 기사
        List<ArticleListDTO> latestArticles;
        if (category != null && !category.isEmpty()) {
            latestArticles = articleService.findLatestArticlesByCategoryDTO(category);
        } else {
            latestArticles = articleService.findLatestArticlesDTO();
        }

        // 오피니언 기사
        List<Article> opinionArticles = articleService.findByCategory("opinion", PageRequest.of(0, 4)).getContent();

        model.addAttribute("headline", headlineArticles.isEmpty() ? null : headlineArticles.get(0));
        model.addAttribute("trendingArticles", trendingArticles);
        model.addAttribute("mostLikedArticles", mostLikedArticles);
        model.addAttribute("latestArticles", latestArticles);
        model.addAttribute("opinionArticles", opinionArticles);
        model.addAttribute("selectedCategory", category);

        return "index";
    }

    @GetMapping("/{category}")
    public String categoryPage(
            @PathVariable String category,
            @RequestParam(required = false, defaultValue = "all") String subcategory,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        // 카테고리 유효성 검사
        if (!isValidCategory(category)) {
            return "redirect:/main";
        }

        // 카테고리 정보 설정
        Map<String, Object> categoryInfo = getCategoryData().get(category);
        if (categoryInfo != null) {
            model.addAttribute("categoryTitle", categoryInfo.get("title"));
            model.addAttribute("categoryDescription", categoryInfo.get("description"));
            model.addAttribute("subcategories", categoryInfo.get("subcategories"));
        }

        // 카테고리 이름 첫 글자 대문자로 변환 (StringUtils.capitalize 대체)
        String capitalizedCategory = category.substring(0, 1).toUpperCase() + category.substring(1);
        model.addAttribute("capitalizedCategory", capitalizedCategory);

        // 카테고리별 기사 로드
        PageRequest pageable = PageRequest.of(page, size);
        model.addAttribute("articles", articleService.findByCategoryDTO(category, pageable).getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articleService.findByCategoryDTO(category, pageable).getTotalPages());
        model.addAttribute("category", category);
        model.addAttribute("subcategory", subcategory);

        // 관련 기사 로드
        model.addAttribute("trendingArticles", articleService.findTrendingArticlesByCategoryDTO(category));
        model.addAttribute("latestArticles", articleService.findLatestArticlesByCategoryDTO(category));
        model.addAttribute("opinionArticles", articleService.findByCategoryDTO("column", PageRequest.of(0, 4)).getContent());

        return "articles/category";
    }

    // 카테고리 유효성 검사 메서드
    private boolean isValidCategory(String category) {
        return Arrays.asList("politic", "economy", "industry", "world",
                "culture", "column","video").contains(category);
    }

    // 카테고리 데이터 생성 메서드
    Map<String, Map<String, Object>> getCategoryData() {
        Map<String, Map<String, Object>> categoryData = new HashMap<>();

        // 정치 카테고리 데이터
        Map<String, Object> politic = new HashMap<>();
        politic.put("title", "정치");
        politic.put("description", "국내 정치, 국회, 청와대, 정당, 북한 관련 최신 뉴스와 기사를 제공합니다.");

        Map<String, String> politicsSubcategories = new HashMap<>();
        categoryData.put("politic", politic);

        // 경제 카테고리 데이터
        Map<String, Object> economy = new HashMap<>();
        economy.put("title", "경제");
        economy.put("description", "국내외 경제 동향, 금융, 부동산, 산업 관련 최신 뉴스와 기사를 제공합니다.");
        categoryData.put("economy", economy);

        // 사회 카테고리
        Map<String, Object> industry = new HashMap<>();
        industry.put("title", "산업");
        industry.put("description", "산업 이슈, 교육, 환경, 건강 관련 최신 뉴스와 기사를 제공합니다.");
        categoryData.put("industry", industry);

        // 국제 카테고리
        Map<String, Object> world = new HashMap<>();
        world.put("title", "국제");
        world.put("description", "세계 각국의 정치, 경제, 사회 분야 소식과 국제 관계에 관한 최신 뉴스를 제공합니다.");
        categoryData.put("world", world);

        // 문화 카테고리
        Map<String, Object> culture = new HashMap<>();
        culture.put("title", "문화");
        culture.put("description", "영화, 음악, 미술, 공연 등 다양한 문화 예술 분야의 소식과 리뷰를 제공합니다.");
        categoryData.put("culture", culture);

        // 칼럼 카테고리
        Map<String, Object> opinion = new HashMap<>();
        opinion.put("title", "칼럼");
        opinion.put("description", "시사 이슈에 대한 전문가 칼럼, 사설, 기고문 등을 제공합니다.");
        categoryData.put("column", opinion);

        // 영상 카테고리 데이터
        Map<String, Object> video = new HashMap<>();
        video.put("title", "영상");
        video.put("description", "영상이 첨부된 최신 뉴스와 기사를 제공합니다.");
        categoryData.put("video", video);

        return categoryData;
    }
    //Introduce
    @GetMapping("/team")
    public String team(){return "introduce/team";}
    @GetMapping("/history")
    public String history(){return "introduce/history";}
    @GetMapping("/mission")
    public String mission(){ return "introduce/mission";}
    @GetMapping("/jobs")
    public String jobs(){ return "introduce/jobs";}
    @GetMapping("/about")
    public String about() {return "introduce/about";}

    //Customer Service
    @GetMapping("/contact")
    public String contact() {return "customer/contact";}
    @GetMapping("/contact#donation")
    public String donation() {return "customer/donation";}
    @GetMapping("/faq")
    public String faq() {return "customer/faq";}
    @GetMapping("/privacy")
    public String privacy() {return "customer/privacy";}
    @GetMapping("/terms")
    public String terms() {return "customer/terms";}



} 