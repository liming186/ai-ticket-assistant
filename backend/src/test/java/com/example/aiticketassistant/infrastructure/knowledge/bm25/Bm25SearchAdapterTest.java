package com.example.aiticketassistant.infrastructure.knowledge.bm25;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.aiticketassistant.infrastructure.persistence.jpa.KnowledgeChunkJpaEntity;
import org.junit.jupiter.api.Test;

class Bm25SearchAdapterTest {
    @Test
    void exactPriceQuestionMatchesCatalogChunk() {
        KnowledgeChunkJpaEntity chunk = new KnowledgeChunkJpaEntity();
        chunk.setTitle("服装商品目录：羊毛混纺大衣");
        chunk.setContent("商品编号 CLOTH-COAT-009，商品名称 羊毛混纺大衣，服装商品，价格 ¥699.00，价格刚好699元。");

        double score = new Bm25SearchAdapter().score("价格刚好699的衣服是什么", chunk);

        assertThat(score).isGreaterThan(0.0);
    }
}
