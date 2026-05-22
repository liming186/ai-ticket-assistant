package com.example.aiticketassistant.infrastructure.knowledge.bm25;

import com.example.aiticketassistant.infrastructure.persistence.jpa.KnowledgeChunkJpaEntity;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class Bm25SearchAdapter {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+(?:\\.\\d+)?");

    public double score(String query, KnowledgeChunkJpaEntity chunk) {
        String searchable = normalize(chunk.getTitle() + " " + chunk.getContent());
        Set<String> terms = tokenize(query);
        Set<String> contentTerms = tokenize(searchable);
        if (terms.isEmpty()) {
            return 0.0;
        }
        long matches = terms.stream().filter(term -> contentTerms.contains(term) || searchable.contains(term)).count();
        String normalizedQuery = normalize(query);
        boolean phraseBoost = searchable.contains(normalizedQuery);
        boolean catalogSummaryBoost = searchable.contains("完整价格表") && isCatalogSummaryQuery(normalizedQuery);
        return matches / (double) terms.size() + (phraseBoost ? 0.5 : 0.0) + (catalogSummaryBoost ? 0.4 : 0.0);
    }

    private Set<String> tokenize(String text) {
        if (text == null || text.isBlank()) {
            return Set.of();
        }
        String normalized = normalize(text);
        Set<String> tokens = new HashSet<>(Arrays.stream(normalized.split("\\s+"))
                .filter(token -> token.length() > 1)
                .toList());
        NUMBER_PATTERN.matcher(normalized).results()
                .map(match -> match.group().replaceAll("\\.0+$", ""))
                .forEach(tokens::add);
        chineseNgrams(normalized).forEach(tokens::add);
        return tokens;
    }

    private boolean isCatalogSummaryQuery(String normalizedQuery) {
        return normalizedQuery.contains("最便宜")
                || normalizedQuery.contains("最贵")
                || normalizedQuery.contains("哪几种")
                || normalizedQuery.contains("哪些")
                || normalizedQuery.contains("有什么")
                || normalizedQuery.contains("低于")
                || normalizedQuery.contains("小于")
                || normalizedQuery.contains("便宜");
    }

    private String normalize(String text) {
        return text == null ? "" : text.toLowerCase(Locale.ROOT).replaceAll("[^\\p{IsHan}a-z0-9.]+", " ").trim();
    }

    private Set<String> chineseNgrams(String text) {
        Set<String> grams = new LinkedHashSet<>();
        String han = text.replaceAll("[^\\p{IsHan}]", "");
        for (int size : new int[]{2, 3, 4, 5, 6, 7, 8}) {
            for (int i = 0; i + size <= han.length(); i++) {
                grams.add(han.substring(i, i + size));
            }
        }
        return grams;
    }
}
