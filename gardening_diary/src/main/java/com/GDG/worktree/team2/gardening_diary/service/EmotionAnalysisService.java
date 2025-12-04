package com.GDG.worktree.team2.gardening_diary.service;

import com.GDG.worktree.team2.gardening_diary.entity.EmotionAnalysis;
import com.GDG.worktree.team2.gardening_diary.repository.EmotionAnalysisRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 감정 분석 AI API 연동을 담당하는 서비스.
 */
@Service
public class EmotionAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(EmotionAnalysisService.class);

    private final EmotionAnalysisRepository emotionAnalysisRepository;
    private final RestTemplate restTemplate;
    private final String emotionApiUrl;
    private final String emotionApiKey;
    private final String emotionApiHealthUrl;

    @Autowired
    public EmotionAnalysisService(EmotionAnalysisRepository emotionAnalysisRepository,
                                  RestTemplateBuilder restTemplateBuilder,
                                  @Value("${emotion.api.url:}") String emotionApiUrl,
                                  @Value("${emotion.api.key:}") String emotionApiKey,
                                  @Value("${emotion.api.health-url:}") String emotionApiHealthUrl) {
        this.emotionAnalysisRepository = emotionAnalysisRepository;
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
        this.emotionApiUrl = emotionApiUrl;
        this.emotionApiKey = emotionApiKey;
        this.emotionApiHealthUrl = emotionApiHealthUrl;
    }

    /**
     * 다이어리 내용을 분석하고 결과를 저장 또는 갱신한다.
     */
    public EmotionAnalysis analyzeAndSave(String diaryId, String content)
            throws ExecutionException, InterruptedException {
        EmotionResult emotionResult = requestEmotionAnalysis(diaryId, content);

        EmotionAnalysis analysis = emotionAnalysisRepository.findByDiaryId(diaryId);
        if (analysis == null) {
            analysis = new EmotionAnalysis();
            analysis.setDiaryId(diaryId);
        }
        analysis.setResult(emotionResult.scores());
        analysis.setComment(emotionResult.comment());
        analysis.setDominantEmotion(emotionResult.dominantEmotion());
        return emotionAnalysisRepository.save(analysis);
    }

    /**
     * 다이어리의 감정 분석 결과를 조회한다.
     */
    public EmotionAnalysis getByDiaryId(String diaryId)
            throws ExecutionException, InterruptedException {
        return emotionAnalysisRepository.findByDiaryId(diaryId);
    }

    /**
     * 다이어리 삭제 시 분석 결과도 함께 제거한다.
     */
    public void deleteByDiaryId(String diaryId) throws ExecutionException, InterruptedException {
        EmotionAnalysis existing = emotionAnalysisRepository.findByDiaryId(diaryId);
        if (existing != null) {
            emotionAnalysisRepository.deleteById(existing.getId());
        }
    }

    /**
     * AI 팀에서 제공할 감정 분석 API를 호출하는 스켈레톤 로직.
     * 실 서비스 연동 시 이 메서드만 수정하면 된다.
     */
    private EmotionResult requestEmotionAnalysis(String diaryId, String content) {
        if (content == null || content.isBlank()) {
            logger.warn("다이어리 내용이 비어 있어 기본 감정으로 대체합니다.");
            return EmotionResult.empty();
        }

        if (emotionApiUrl == null || emotionApiUrl.isBlank()) {
            logger.warn("emotion.api.url이 설정되지 않아 감정 분석을 건너뜁니다.");
            return EmotionResult.empty();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (emotionApiKey != null && !emotionApiKey.isBlank()) {
            headers.set("X-API-KEY", emotionApiKey);
        }

        InferenceRequest payload = new InferenceRequest(
                buildTitle(content),
                content,
                buildMetadata(diaryId)
        );
        HttpEntity<InferenceRequest> entity = new HttpEntity<>(payload, headers);

        try {
            InferenceResponse response = restTemplate.postForObject(
                    emotionApiUrl,
                    entity,
                    InferenceResponse.class
            );

            if (response != null && response.isValid()) {
                return new EmotionResult(
                        response.getEmotionScores(),
                        response.getDominantEmotion(),
                        response.getComment()
                );
            }

            logger.warn("감정 분석 API에서 빈 결과를 반환했습니다.");
        } catch (RestClientException ex) {
            logger.error("감정 분석 API 호출 실패", ex);
            logHealthStatus();
        }

        return EmotionResult.empty();
    }

    private void logHealthStatus() {
        if (emotionApiHealthUrl == null || emotionApiHealthUrl.isBlank()) {
            return;
        }
        try {
            String status = restTemplate.getForObject(emotionApiHealthUrl, String.class);
            logger.debug("Emotion API health 응답: {}", status);
        } catch (RestClientException healthEx) {
            logger.warn("Emotion API health 체크 실패", healthEx);
        }
    }

    private Map<String, Object> buildMetadata(String diaryId) {
        if (diaryId == null || diaryId.isBlank()) {
            return Collections.emptyMap();
        }
        return Collections.singletonMap("diaryId", diaryId);
    }

    private String buildTitle(String content) {
        String normalized = content.strip();
        if (normalized.isEmpty()) {
            return "Diary entry";
        }
        return normalized.length() > 20
                ? normalized.substring(0, 20) + "..."
                : normalized;
    }

    /**
     * 외부 API 요청 페이로드 DTO.
     */
    private record EmotionResult(Map<String, Double> scores, String dominantEmotion, String comment) {
        private static EmotionResult empty() {
            return new EmotionResult(Map.of("neutral", 1.0), "neutral", "감정 분석을 수행할 수 없습니다");
        }

        public Map<String, Double> scores() {
            return scores != null && !scores.isEmpty()
                    ? scores
                    : Map.of("neutral", 1.0);
        }

        public String dominantEmotion() {
            return dominantEmotion != null ? dominantEmotion : "neutral";
        }

        public String comment() {
            return comment != null ? comment : "감정 분석 결과가 없습니다";
        }
    }

    /**
     * 외부 API 요청 페이로드 DTO.
     */
    private record InferenceRequest(String title, String text, Map<String, Object> metadata) {}

    /**
     * 외부 API 응답 DTO (AI 스펙에 맞춤).
     */
    private static class InferenceResponse {
        private String comment;
        private String dominantEmotion;
        private Map<String, Double> emotionScores;

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getDominantEmotion() {
            return dominantEmotion;
        }

        public void setDominantEmotion(String dominantEmotion) {
            this.dominantEmotion = dominantEmotion;
        }

        public Map<String, Double> getEmotionScores() {
            return emotionScores;
        }

        public void setEmotionScores(Map<String, Double> emotionScores) {
            this.emotionScores = emotionScores;
        }

        boolean isValid() {
            return emotionScores != null && !emotionScores.isEmpty();
        }
    }
}
