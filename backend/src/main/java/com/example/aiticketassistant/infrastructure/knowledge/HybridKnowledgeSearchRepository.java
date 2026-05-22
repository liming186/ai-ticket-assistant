package com.example.aiticketassistant.infrastructure.knowledge;

import com.example.aiticketassistant.domain.knowledge.KnowledgeChunk;
import com.example.aiticketassistant.domain.knowledge.KnowledgeSearchRepository;
import com.example.aiticketassistant.domain.knowledge.KnowledgeSearchResult;
import com.example.aiticketassistant.infrastructure.knowledge.bm25.Bm25SearchAdapter;
import com.example.aiticketassistant.infrastructure.knowledge.chroma.ChromaClient;
import com.example.aiticketassistant.infrastructure.persistence.jpa.KnowledgeChunkJpaEntity;
import com.example.aiticketassistant.infrastructure.persistence.jpa.SpringDataKnowledgeChunkJpaRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class HybridKnowledgeSearchRepository implements KnowledgeSearchRepository {
    private final SpringDataKnowledgeChunkJpaRepository chunkRepository;
    private final Bm25SearchAdapter bm25;
    private final ChromaClient chromaClient;

    public HybridKnowledgeSearchRepository(SpringDataKnowledgeChunkJpaRepository chunkRepository,
                                           Bm25SearchAdapter bm25,
                                           ChromaClient chromaClient) {
        this.chunkRepository = chunkRepository;
        this.bm25 = bm25;
        this.chromaClient = chromaClient;
    }

    @Override
    public List<KnowledgeSearchResult> search(String query, int limit) {
        List<String> vectorIds = chromaClient.searchIds(query, limit);
        return chunkRepository.findTop100ByOrderByIdAsc().stream()
                .map(chunk -> toResult(query, chunk, vectorIds))
                .filter(result -> result.finalScore() > 0.0)
                .sorted(Comparator.comparingDouble(KnowledgeSearchResult::finalScore).reversed())
                .limit(Math.max(1, limit))
                .toList();
    }

    private KnowledgeSearchResult toResult(String query, KnowledgeChunkJpaEntity entity, List<String> vectorIds) {
        double vectorScore = vectorIds.contains(entity.getId()) ? 1.0 : 0.0;
        double bm25Score = bm25.score(query, entity);
        double finalScore = 0.65 * bm25Score + 0.35 * vectorScore;
        KnowledgeChunk chunk = new KnowledgeChunk(entity.getId(), entity.getDocumentId(), entity.getTitle(), entity.getCategory(), entity.getSource(), entity.getContent());
        String reason = vectorScore > 0 ? "hybrid-vector-bm25" : "bm25-local";
        return new KnowledgeSearchResult(chunk, vectorScore, bm25Score, finalScore, reason);
    }
}
