package project.doklipnews.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SummaryService {
    private static final Logger logger = LoggerFactory.getLogger(SummaryService.class);

    @Value("${openai.api.key:}")
    private String apiKey;

    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public SummaryService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * 기사 내용을 OpenAI API를 통해 3줄 요약
     * @param content 기사 내용
     * @return 3줄로 요약된 텍스트
     */
    public String generateSummary(String content) {
        // API 키가 설정되지 않은 경우
//
//        if (apiKey == null || apiKey.isEmpty()) {
//            logger.warn("OpenAI API key is not set. Using fallback summary method.");
//            return generateFallbackSummary(content);
//        }
//
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.setBearerAuth(apiKey);
//
//            Map<String, Object> requestBody = new HashMap<>();
//            requestBody.put("model", "gpt-3.5-turbo");
//
//            Map<String, Object> systemMessage = new HashMap<>();
//            systemMessage.put("role", "system");
//            systemMessage.put("content", "당신은 뉴스 기사 요약 전문가입니다. 주어진 기사를 핵심만 포함해 3줄로 요약하세요.");
//
//            Map<String, Object> userMessage = new HashMap<>();
//            userMessage.put("role", "user");
//            userMessage.put("content", "다음 기사를 3줄로 요약해주세요. 각 문장은 최대 30자 내외로 작성해주세요: " + content);
//
//            requestBody.put("messages", List.of(systemMessage, userMessage));
//            requestBody.put("temperature", 0.3);
//            requestBody.put("max_tokens", 150);
//
//            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
//            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);
//
//            Map responseBody = response.getBody();
//            if (responseBody != null && responseBody.containsKey("choices")) {
//                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
//                if (!choices.isEmpty()) {
//                    Map<String, Object> choice = choices.get(0);
//                    Map<String, String> message = (Map<String, String>) choice.get("message");
//                    return message.get("content").trim();
//                }
//            }
//
//            logger.error("Failed to parse OpenAI API response: {}", responseBody);
//            return generateFallbackSummary(content);
//
//        } catch (Exception e) {
//            logger.error("Error generating summary with OpenAI API", e);
//            return generateFallbackSummary(content);
//        }

        return generateFallbackSummary(content);
    }

    /**
     * API 호출에 실패한 경우 사용할 대체 요약 방법
     * @param content 기사 내용
     * @return 간단히 처리된 요약 텍스트
     */
    private String generateFallbackSummary(String content) {
        // 단순히 처음 3문장 또는 200자 이내 추출
        String[] sentences = content.split("[.!?]");
        StringBuilder summary = new StringBuilder();

        int count = 0;
        for (String sentence : sentences) {
            if (sentence.trim().isEmpty()) continue;
            summary.append(sentence.trim()).append(". ");
            count++;
            if (count >= 2) break;
        }

        if (summary.length() > 200) {
            return summary.substring(0, 197) + "...";
        }
        return summary.toString();
    }
}