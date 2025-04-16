package project.doklipnews.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import project.doklipnews.entity.Article;
import project.doklipnews.service.SearchService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class SearchController {

    private final SearchService searchService;
    private final MainController mainController;

    @Autowired
    public SearchController(SearchService searchService, MainController mainController) {
        this.searchService = searchService;
        this.mainController = mainController;
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            Model model
    ) {
        // 검색어가 없으면 빈 결과 페이지 표시
        if (query == null || query.trim().isEmpty()) {
            model.addAttribute("query", "");
            model.addAttribute("totalResults", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("currentPage", 1);
            model.addAttribute("categories", getCategoryData());
            return "search";
        }

        // 페이지는 0부터 시작하므로 1을 빼줍니다.
        int pageIndex = page - 1;
        if (pageIndex < 0) pageIndex = 0;

        Pageable pageable = PageRequest.of(pageIndex, 10, Sort.by("createdAt").descending());

        // 검색 결과 페이징 처리
        Page<Article> searchResultPage;
        if (category != null && !category.isEmpty()) {
            // 카테고리별 검색 - SearchService 사용
            searchResultPage = searchService.searchArticlesByCategory(query, category, pageable);
        } else {
            // 전체 검색 - SearchService 사용
            searchResultPage = searchService.searchArticles(query, pageable);
        }

        // 검색 결과 총 개수 - SearchService 사용
        long totalResults = searchService.getTotalSearchResultCount(query, category);

        // 검색 결과가 없는 경우 빈 결과 페이지 표시
        if (totalResults == 0) {
            model.addAttribute("query", query);
            model.addAttribute("selectedCategory", category);
            model.addAttribute("totalResults", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("currentPage", 1);
            model.addAttribute("categories", getCategoryData());
            return "search";
        }

        // 검색 결과 존재하는 경우

        // 메인 주요 기사 (첫 번째 검색 결과) - SearchService 사용
        Article mainArticle = searchService.getMainSearchArticle(query, category);

        // 제외할 ID 목록 (메인 기사가 있으면 해당 ID 추가)
        List<Long> excludeIds = new ArrayList<>();
        if (mainArticle != null) {
            excludeIds.add(mainArticle.getId());
        }

        // 이미지 그리드용 기사 (이미지가 있는 기사 최대 3개) - SearchService 사용
        List<Article> imageGridArticles = searchService.getImageGridArticles(query, category, 3);

        // 이미지 그리드 기사 ID도 제외 목록에 추가
        excludeIds.addAll(imageGridArticles.stream().map(Article::getId).collect(Collectors.toList()));

        // 리스트 기사 (메인, 이미지 그리드에 없는 기사 최대 4개) - SearchService 사용
        List<Article> listArticles = searchService.getListArticles(query, category, excludeIds, 4);

        // 의견/칼럼 기사 (컬럼 카테고리 기사 최대 4개) - SearchService 사용
        List<Article> opinionArticles = searchService.getOpinionArticles(query, 4);

        // 페이지네이션 관련 정보
        int totalPages = searchResultPage.getTotalPages();

        // 모델에 데이터 추가
        model.addAttribute("query", query);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("mainArticle", mainArticle);
        model.addAttribute("imageGridArticles", imageGridArticles);
        model.addAttribute("listArticles", listArticles);
        model.addAttribute("opinionArticles", opinionArticles);
        model.addAttribute("totalResults", totalResults);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        // 카테고리 목록 가져오기
        model.addAttribute("categories", getCategoryData());

        return "search";
    }

    // MainController의 카테고리 데이터 메소드 재사용
    private Map<String, Map<String, Object>> getCategoryData() {
        // 실제 MainController의 메소드를 호출
        try {
            return mainController.getCategoryData();
        } catch (Exception e) {
            // 예외 발생 시 빈 맵 반환
            return Map.of();
        }
    }
    @ModelAttribute("categoryUtil")
    public Map<String, String> categoryUtil() {
        Map<String, String> categoryMap = new HashMap<>();
        categoryMap.put("politic", "정치");
        categoryMap.put("economy", "경제");
        categoryMap.put("industry", "산업");
        categoryMap.put("world", "국제");
        categoryMap.put("culture", "문화");
        categoryMap.put("column", "칼럼");
        categoryMap.put("video", "영상");
        categoryMap.put("foreign", "외신");
        return categoryMap;
    }
}