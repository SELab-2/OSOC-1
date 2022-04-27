module.exports = {
  content: [
    './pages/**/*.{js,ts,jsx,tsx}',
    './components/**/*.{js,ts,jsx,tsx}',
  ],
  theme: {
    extend: {
      screens: {
        'xl1920': '1920px',
      },
      colors: {
        'osoc-blue': '#0A0839',
        'osoc-yellow': '#FCB70F',
        'osoc-green': '#44DBA4',
        'osoc-red': '#F14A3B',
        'osoc-bg': '#86efac',
        'osoc-neutral-bg': '#fafafa',
        'osoc-btn-primary': '#1DE1AE',
        'check-green': '#4ade80',
        'check-red': '#f87171',
        'check-orange': '#fbbf24',
        'check-gray': '#9ca3af',
      },
      animation: {
        'spin-reverse': 'spin-reverse 1s linear infinite'
      },
      keyframes: {
        'spin-reverse': {
          from: {
            transform: 'rotate(360deg)'
          }
        }
      }
    },
    fontFamily: {
      sans: ['Montserrat', 'sans-serif']
    },
  },
  plugins: [],
}
