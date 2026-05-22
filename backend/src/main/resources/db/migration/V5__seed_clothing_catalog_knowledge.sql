INSERT INTO knowledge_documents (id, title, category, source, content, updated_at)
VALUES
  ('DOC-CATALOG-CLOTHING', '服装商品目录', 'CATALOG', 'internal://catalog/clothing', '服装商品目录包含商品编号、商品名称、价格：CLOTH-TEE-001 基础纯棉白色T恤 ¥99.00；CLOTH-SHIRT-002 蓝色牛津纺衬衫 ¥199.00；CLOTH-JEANS-003 直筒水洗牛仔裤 ¥299.00；CLOTH-HOODIE-004 黑色连帽卫衣 ¥259.00；CLOTH-DRESS-005 碎花雪纺连衣裙 ¥329.00；CLOTH-JACKET-006 轻薄防风夹克 ¥399.00；CLOTH-SKIRT-007 高腰A字半身裙 ¥189.00；CLOTH-POLO-008 商务休闲Polo衫 ¥169.00；CLOTH-COAT-009 羊毛混纺大衣 ¥699.00；CLOTH-SWEATER-010 米色针织毛衣 ¥229.00。', CURRENT_TIMESTAMP(6));

INSERT INTO knowledge_chunks (id, document_id, title, category, source, content)
VALUES
  ('CHK-CATALOG-CLOTHING-TEE-001', 'DOC-CATALOG-CLOTHING', '服装商品目录：基础纯棉白色T恤', 'CATALOG', 'internal://catalog/clothing#CLOTH-TEE-001', '商品编号 CLOTH-TEE-001，商品名称 基础纯棉白色T恤，服装商品，价格 ¥99.00，价格刚好99元。'),
  ('CHK-CATALOG-CLOTHING-SHIRT-002', 'DOC-CATALOG-CLOTHING', '服装商品目录：蓝色牛津纺衬衫', 'CATALOG', 'internal://catalog/clothing#CLOTH-SHIRT-002', '商品编号 CLOTH-SHIRT-002，商品名称 蓝色牛津纺衬衫，服装商品，价格 ¥199.00，价格刚好199元。'),
  ('CHK-CATALOG-CLOTHING-JEANS-003', 'DOC-CATALOG-CLOTHING', '服装商品目录：直筒水洗牛仔裤', 'CATALOG', 'internal://catalog/clothing#CLOTH-JEANS-003', '商品编号 CLOTH-JEANS-003，商品名称 直筒水洗牛仔裤，服装商品，价格 ¥299.00，价格刚好299元。'),
  ('CHK-CATALOG-CLOTHING-HOODIE-004', 'DOC-CATALOG-CLOTHING', '服装商品目录：黑色连帽卫衣', 'CATALOG', 'internal://catalog/clothing#CLOTH-HOODIE-004', '商品编号 CLOTH-HOODIE-004，商品名称 黑色连帽卫衣，服装商品，价格 ¥259.00，价格刚好259元。'),
  ('CHK-CATALOG-CLOTHING-DRESS-005', 'DOC-CATALOG-CLOTHING', '服装商品目录：碎花雪纺连衣裙', 'CATALOG', 'internal://catalog/clothing#CLOTH-DRESS-005', '商品编号 CLOTH-DRESS-005，商品名称 碎花雪纺连衣裙，服装商品，价格 ¥329.00，价格刚好329元。'),
  ('CHK-CATALOG-CLOTHING-JACKET-006', 'DOC-CATALOG-CLOTHING', '服装商品目录：轻薄防风夹克', 'CATALOG', 'internal://catalog/clothing#CLOTH-JACKET-006', '商品编号 CLOTH-JACKET-006，商品名称 轻薄防风夹克，服装商品，价格 ¥399.00，价格刚好399元。'),
  ('CHK-CATALOG-CLOTHING-SKIRT-007', 'DOC-CATALOG-CLOTHING', '服装商品目录：高腰A字半身裙', 'CATALOG', 'internal://catalog/clothing#CLOTH-SKIRT-007', '商品编号 CLOTH-SKIRT-007，商品名称 高腰A字半身裙，服装商品，价格 ¥189.00，价格刚好189元。'),
  ('CHK-CATALOG-CLOTHING-POLO-008', 'DOC-CATALOG-CLOTHING', '服装商品目录：商务休闲Polo衫', 'CATALOG', 'internal://catalog/clothing#CLOTH-POLO-008', '商品编号 CLOTH-POLO-008，商品名称 商务休闲Polo衫，服装商品，价格 ¥169.00，价格刚好169元。'),
  ('CHK-CATALOG-CLOTHING-COAT-009', 'DOC-CATALOG-CLOTHING', '服装商品目录：羊毛混纺大衣', 'CATALOG', 'internal://catalog/clothing#CLOTH-COAT-009', '商品编号 CLOTH-COAT-009，商品名称 羊毛混纺大衣，服装商品，价格 ¥699.00，价格刚好699元。'),
  ('CHK-CATALOG-CLOTHING-SWEATER-010', 'DOC-CATALOG-CLOTHING', '服装商品目录：米色针织毛衣', 'CATALOG', 'internal://catalog/clothing#CLOTH-SWEATER-010', '商品编号 CLOTH-SWEATER-010，商品名称 米色针织毛衣，服装商品，价格 ¥229.00，价格刚好229元。'),
  ('CHK-CATALOG-CLOTHING-ALL', 'DOC-CATALOG-CLOTHING', '服装商品目录：完整价格表', 'CATALOG', 'internal://catalog/clothing#all', '服装商品完整价格表：基础纯棉白色T恤 CLOTH-TEE-001 ¥99.00；蓝色牛津纺衬衫 CLOTH-SHIRT-002 ¥199.00；直筒水洗牛仔裤 CLOTH-JEANS-003 ¥299.00；黑色连帽卫衣 CLOTH-HOODIE-004 ¥259.00；碎花雪纺连衣裙 CLOTH-DRESS-005 ¥329.00；轻薄防风夹克 CLOTH-JACKET-006 ¥399.00；高腰A字半身裙 CLOTH-SKIRT-007 ¥189.00；商务休闲Polo衫 CLOTH-POLO-008 ¥169.00；羊毛混纺大衣 CLOTH-COAT-009 ¥699.00；米色针织毛衣 CLOTH-SWEATER-010 ¥229.00。');
