package project.doklipnews.util;

import org.springframework.stereotype.Component;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 기사 내용을 HTML로 변환하는 유틸리티 클래스
 * 줄바꿈, 이미지 태그 처리, HTML 태그 등을 적절히 처리합니다.
 */
@Component
public class ArticleContentFormatter {

    // 이미지 태그 패턴 (예: <img src="파일경로" />)
    private static final Pattern IMG_PATTERN = Pattern.compile("<img\\s+src=[\"'](.*?)[\"']\\s*/?>");

    /**
     * 기사 내용을 HTML로 포맷팅합니다.
     *
     * @param content 원본 기사 내용
     * @return HTML 형식으로 포맷팅된 내용
     */
    public String format(String content) {
        if (content == null) {
            return "";
        }

        // Step 1: HTML 특수문자 이스케이프 (보안상 필요)
        String escaped = escapeHtml(content);

        // Step 2: 줄바꿈 처리
        String withLineBreaks = escaped.replace("\n", "<br/>");

        // Step 3: 이미지 태그 복원 (이미지 태그는 이스케이프 하지 않음)
        String withImages = restoreImageTags(content, withLineBreaks);

        // Step 4: 다른 HTML 태그 복원 (예: <strong>, <em>, <h2> 등)
        String formattedContent = restoreHtmlTags(withImages);

        // 단락 표시 - 연속된 <br/> 태그를 </p><p>로 변환 (빈 줄은 새 단락)
        formattedContent = handleParagraphs(formattedContent);

        return "<p>" + formattedContent + "</p>";
    }

    /**
     * HTML 특수문자를 이스케이프합니다.
     */
    private String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * 이미지 태그를 복원합니다.
     */
    private String restoreImageTags(String original, String escaped) {
        Matcher matcher = IMG_PATTERN.matcher(original);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String imgTag = matcher.group(0);
            String imgSrc = matcher.group(1);

            // 이미지 태그를 중앙 정렬 스타일과 함께 추가
            String replacement = "<div style=\"text-align:center;\"><img src=\"" + imgSrc +
                    "\" alt=\"기사 이미지\" style=\"max-width:100%;height:auto;margin:20px auto;\"></div>";

            // HTML 특수문자를 이스케이프하지 않도록 처리
            replacement = Matcher.quoteReplacement(replacement);

            // 이스케이프된 텍스트에서 이미지 태그 문자열 찾기
            String escapedImgTag = escapeHtml(imgTag);
            escaped = escaped.replace(escapedImgTag, replacement);
        }

        return escaped;
    }

    /**
     * HTML 태그를 복원합니다. (strong, em, h2, h3 등)
     */
    private String restoreHtmlTags(String content) {
        // 볼드체 복원 <strong>
        content = restoreTag(content, "strong");
        content = restoreTag(content, "b");

        // 이탤릭체 복원 <em>
        content = restoreTag(content, "em");
        content = restoreTag(content, "i");

        // 밑줄 복원 <u>
        content = restoreTag(content, "u");

        // 제목 태그 복원
        content = restoreTag(content, "h2");
        content = restoreTag(content, "h3");
        content = restoreTag(content, "h4");

        return content;
    }

    /**
     * 특정 HTML 태그를 복원합니다.
     */
    private String restoreTag(String content, String tagName) {
        // 여는 태그 복원
        String escapedOpenTag = "&lt;" + tagName + "&gt;";
        String openTag = "<" + tagName + ">";
        content = content.replace(escapedOpenTag, openTag);

        // 닫는 태그 복원
        String escapedCloseTag = "&lt;/" + tagName + "&gt;";
        String closeTag = "</" + tagName + ">";
        content = content.replace(escapedCloseTag, closeTag);

        return content;
    }

    /**
     * 단락을 처리합니다. 빈 줄은 새 단락으로 처리합니다.
     */
    private String handleParagraphs(String content) {
        // 연속된 <br/> 태그를 </p><p>로 변환
        return content.replace("<br/><br/>", "</p><p>");
    }
}