import { Shirt } from 'lucide-react'
import { useEffect, useState } from 'react'
import { API_BASE_URL } from '../api/client'

type Product = {
  id: string
  name: string
  price: number
}

export function ProductCatalogPanel() {
  const [products, setProducts] = useState<Product[]>([])

  useEffect(() => {
    fetch(`${API_BASE_URL}/products`)
      .then((response) => response.json())
      .then((data) => setProducts(Array.isArray(data.products) ? data.products : []))
      .catch(() => setProducts([]))
  }, [])

  return (
    <section className="glass rounded-[2rem] p-5">
      <div className="mb-4 flex items-center gap-2">
        <Shirt size={18} className="text-cyan-200" />
        <h3 className="font-semibold text-white">服装商品目录</h3>
      </div>
      <div className="space-y-2">
        {products.length === 0 && <p className="text-sm text-slate-500">正在加载商品。</p>}
        {products.map((product) => (
          <div key={product.id} className="rounded-2xl bg-white/5 p-3 text-sm text-slate-300">
            <div className="font-medium text-slate-100">{product.name}</div>
            <div className="mt-1 flex items-center justify-between text-xs text-slate-400">
              <span>{product.id}</span>
              <span>¥{Number(product.price).toFixed(2)}</span>
            </div>
          </div>
        ))}
      </div>
      <p className="mt-3 text-xs text-slate-500">示例：购买 CLOTH-JEANS-003 下单</p>
    </section>
  )
}
