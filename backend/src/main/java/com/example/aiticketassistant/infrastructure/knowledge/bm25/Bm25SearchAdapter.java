package com.example.aiticketassistant.infrastructure.knowledge.bm25;

import com.example.aiticketassistant.infrastructure.persistence.jpa.KnowledgeChunkJpaEntity;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class Bm25SearchAdapter {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+(?:\\.\\d+)?");

    public double score(String query, KnowledgeChunkJpaEntity chunk) {
        String searchable = chunk.getTitle() + " " + chunk.getContent();
        Set<String> terms = tokenize(query);
        Set<String> contentTerms = tokenize(searchable);
        if (terms.isEmpty()) {
            return 0.0;
        }
        long matches = terms.stream().filter(term -> contentTerms.contains(term) || searchable.contains(term)).count();
        boolean phraseBoost = searchable.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT));
        return matches / (double) terms.size() + (phraseBoost ? 0.5 : 0.0);
    }

    private Set<String> tokenize(String text) {
        if (text == null || text.isBlank()) {
            return Set.of();
        }
        String normalized = text.toLowerCase(Locale.ROOT).replaceAll("[^\\p{IsHan}a-z0-9.]+", " ");
        Set<String> tokens = new HashSet<>(Arrays.stream(normalized.split("\\s+"))
                .filter(token -> token.length() > 1)
                .toList());
        NUMBER_PATTERN.matcher(normalized).results()
                .map(match -> match.group().replaceAll("\\.0+$", ""))
                .forEach(tokens::add);
        return tokens;
    }
}
