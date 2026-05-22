package com.example.aiticketassistant.infrastructure.knowledge.bm25;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.aiticketassistant.infrastructure.persistence.jpa.KnowledgeChunkJpaEntity;
import org.junit.jupiter.api.Test;

class Bm25SearchAdapterTest {
    @Test
    void productNamePriceQuestionMatchesCatalogChunk() {
        KnowledgeChunkJpaEntity chunk = new KnowledgeChunkJpaEntity();
        chunk.setTitle("服装商品目录：高腰A字半身裙");
        chunk.setContent("商品编号 CLOTH-SKIRT-007，商品名称 高腰A字半身裙，服装商品，价格 ¥189.00，价格刚好189元。");

        double score = new Bm25SearchAdapter().score("一件高腰A字半身裙多少钱", chunk);

        assertThat(score).isGreaterThan(0.0);
    }

    @Test
    void cheapestQuestionBoostsFullCatalogChunk() {
        KnowledgeChunkJpaEntity fullCatalog = new KnowledgeChunkJpaEntity();
        fullCatalog.setTitle("服装商品目录：完整价格表");
        fullCatalog.setContent("服装商品完整价格表：基础纯棉白色T恤 CLOTH-TEE-001 ¥99.00；蓝色牛津纺衬衫 CLOTH-SHIRT-002 ¥199.00；羊毛混纺大衣 CLOTH-COAT-009 ¥699.00。");
        KnowledgeChunkJpaEntity singleProduct = new KnowledgeChunkJpaEntity();
        singleProduct.setTitle("服装商品目录：羊毛混纺大衣");
        singleProduct.setContent("商品编号 CLOTH-COAT-009，商品名称 羊毛混纺大衣，服装商品，价格 ¥699.00。");

        Bm25SearchAdapter adapter = new Bm25SearchAdapter();
        double fullScore = adapter.score("价格最便宜的衣服是哪一件", fullCatalog);
        double singleScore = adapter.score("价格最便宜的衣服是哪一件", singleProduct);

        assertThat(fullScore).isGreaterThan(singleScore);
    }

    @Test
    void exactPriceQuestionMatchesCatalogChunk() {
        KnowledgeChunkJpaEntity chunk = new KnowledgeChunkJpaEntity();
        chunk.setTitle("服装商品目录：羊毛混纺大衣");
        chunk.setContent("商品编号 CLOTH-COAT-009，商品名称 羊毛混纺大衣，服装商品，价格 ¥699.00，价格刚好699元。");

        double score = new Bm25SearchAdapter().score("价格刚好699的衣服是什么", chunk);

        assertThat(score).isGreaterThan(0.0);
    }
}
