package project.doklipnews.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import project.doklipnews.controller.dto.CategoryPageData;
import project.doklipnews.controller.dto.MainPageData;
import project.doklipnews.service.MainPageService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final MainPageService mainPageService;

    @GetMapping("/")
    public String home() {
        return "redirect:/main";
    }

    @GetMapping("/main")
    public String mainPage(
            @RequestParam(required = false) String category,
            Model model) {

        // 한 번의 서비스 호출로 모든 데이터 가져오기
        MainPageData data = mainPageService.getMainPageData(category);

        // 모델에 데이터 추가
        model.addAttribute("headline", data.getHeadline());
        model.addAttribute("trendingArticles", data.getTrendingArticles());
        model.addAttribute("mostLikedArticles", data.getMostLikedArticles());
        model.addAttribute("latestArticles", data.getLatestArticles());
        model.addAttribute("opinionArticles", data.getOpinionArticles());
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

        if (!isValidCategory(category)) {
            return "redirect:/main";
        }

        // 한 번의 서비스 호출로 모든 데이터 가져오기
        CategoryPageData data = mainPageService.getCategoryPageData(category, page, size);

        // 카테고리 정보 설정
        var categoryInfo = getCategoryData().get(category);
        if (categoryInfo != null) {
            model.addAttribute("categoryTitle", categoryInfo.get("title"));
            model.addAttribute("categoryDescription", categoryInfo.get("description"));
        }

        model.addAttribute("capitalizedCategory",
                category.substring(0, 1).toUpperCase() + category.substring(1));
        model.addAttribute("articles", data.getArticles());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", data.getTotalPages());
        model.addAttribute("category", category);
        model.addAttribute("subcategory", subcategory);
        model.addAttribute("trendingArticles", data.getTrendingArticles());
        model.addAttribute("latestArticles", data.getLatestArticles());
        model.addAttribute("opinionArticles", data.getOpinionArticles());

        return "articles/category";
    }

    // 카테고리 유효성 검사 메서드
    private boolean isValidCategory(String category) {
        return Arrays.asList("politic", "economy", "industry", "world",
                "culture", "column","video","foreign").contains(category);
    }

    // 캐시된 카테고리 데이터 (static으로 만들어서 메모리에서 처리)
    private static final Map<String, Map<String, Object>> CATEGORY_DATA = initializeCategoryData();

    private static Map<String, Map<String, Object>> initializeCategoryData() {
        Map<String, Map<String, Object>> categoryData = new HashMap<>();

        // 정치 카테고리 데이터
        Map<String, Object> politic = new HashMap<>();
        politic.put("title", "정치");
        politic.put("description", "국내 정치, 국회, 청와대, 정당, 북한 관련 최신 뉴스와 기사를 제공합니다.");
        categoryData.put("politic", politic);

        // 경제 카테고리 데이터
        Map<String, Object> economy = new HashMap<>();
        economy.put("title", "경제");
        economy.put("description", "국내외 경제 동향, 금융, 부동산, 산업 관련 최신 뉴스와 기사를 제공합니다.");
        categoryData.put("economy", economy);

        // 나머지 카테고리들...
        Map<String, Object> industry = new HashMap<>();
        industry.put("title", "산업");
        industry.put("description", "산업 이슈, 교육, 환경, 건강 관련 최신 뉴스와 기사를 제공합니다.");
        categoryData.put("industry", industry);

        Map<String, Object> world = new HashMap<>();
        world.put("title", "국제");
        world.put("description", "세계 각국의 정치, 경제, 사회 분야 소식과 국제 관계에 관한 최신 뉴스를 제공합니다.");
        categoryData.put("world", world);

        Map<String, Object> culture = new HashMap<>();
        culture.put("title", "문화");
        culture.put("description", "영화, 음악, 미술, 공연 등 다양한 문화 예술 분야의 소식과 리뷰를 제공합니다.");
        categoryData.put("culture", culture);

        Map<String, Object> opinion = new HashMap<>();
        opinion.put("title", "칼럼");
        opinion.put("description", "시사 이슈에 대한 전문가 칼럼, 사설, 기고문 등을 제공합니다.");
        categoryData.put("column", opinion);

        Map<String, Object> video = new HashMap<>();
        video.put("title", "영상");
        video.put("description", "영상이 첨부된 최신 뉴스와 기사를 제공합니다.");
        categoryData.put("video", video);

        Map<String, Object> foreign = new HashMap<>();
        foreign.put("title", "외신");
        foreign.put("description", "중요하고 긴급한 외신 기사들을 신속하게 제공합니다.");
        categoryData.put("foreign", foreign);

        return categoryData;
    }

    Map<String, Map<String, Object>> getCategoryData() {
        return CATEGORY_DATA;
    }

    // 나머지 매핑들은 그대로 유지...
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

    @GetMapping("/example")
    public String example() {return "etc/example";}

    @GetMapping("/example2")
    public String example2() {return "etc/example2";}

    @GetMapping("/download")
    public String download() {return "etc/download";}
}