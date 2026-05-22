import type { Config } from 'tailwindcss'

export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      fontFamily: {
        sans: ['Inter', 'ui-sans-serif', 'system-ui']
      },
      colors: {
        obsidian: '#070912',
        panel: '#0D111F',
        panel2: '#11182A',
        cyanGlow: '#7DD3FC',
        violetGlow: '#A78BFA',
        emeraldGlow: '#5EEAD4'
      },
      boxShadow: {
        glow: '0 0 40px rgba(125, 211, 252, 0.16)',
        card: '0 24px 80px rgba(0, 0, 0, 0.35)'
      }
    }
  },
  plugins: []
} satisfies Config
