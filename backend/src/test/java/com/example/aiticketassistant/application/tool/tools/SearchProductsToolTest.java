package com.example.aiticketassistant.application.tool.tools;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.aiticketassistant.application.workflow.WorkflowContext;
import com.example.aiticketassistant.domain.catalog.Product;
import com.example.aiticketassistant.domain.catalog.ProductInventory;
import com.example.aiticketassistant.domain.catalog.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class SearchProductsToolTest {
    @Test
    void calculatesMaximumAffordableQuantityFromCheapestProduct() {
        SearchProductsTool tool = new SearchProductsTool(new InMemoryProductRepository(List.of(
                new Product("CLOTH-COAT-009", "羊毛混纺大衣", new BigDecimal("699.00"), true),
                new Product("CLOTH-TEE-001", "基础纯棉白色T恤", new BigDecimal("99.00"), true),
                new Product("CLOTH-POLO-008", "商务休闲Polo衫", new BigDecimal("169.00"), true))));

        var result = tool.execute(
                Map.of("combinationMode", true, "budget", 1000, "maxPrice", 1000, "category", "clothing"),
                new WorkflowContext("s", "CUST-1001", null, "我只有1000元，最多可以买几件衣服"));

        assertThat(result.success()).isTrue();
        assertThat((Map<String, Object>) result.data())
                .containsEntry("combinationMode", true)
                .containsEntry("maxQuantity", 10)
                .containsEntry("bestProductId", "CLOTH-TEE-001")
                .containsEntry("bestProductName", "基础纯棉白色T恤")
                .containsEntry("totalPrice", new BigDecimal("990.00"))
                .containsEntry("remainingBudget", new BigDecimal("10.00"));
    }

    private record InMemoryProductRepository(List<Product> products) implements ProductRepository {
        @Override
        public Optional<Product> findById(String id) {
            return products.stream().filter(product -> product.id().equals(id)).findFirst();
        }

        @Override
        public List<Product> findActiveProducts() {
            return products.stream().filter(Product::active).toList();
        }

        @Override
        public Optional<ProductInventory> findInventory(String productId) {
            return Optional.empty();
        }

        @Override
        public ProductInventory saveInventory(ProductInventory inventory) {
            return inventory;
        }
    }
}
