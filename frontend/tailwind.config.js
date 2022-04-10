module.exports = {
  content: [
    './pages/**/*.{js,ts,jsx,tsx}',
    './components/**/*.{js,ts,jsx,tsx}',
  ],
  theme: {
    extend: {
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
    },
    fontFamily: {
      sans: ['Montserrat', 'sans-serif']
    },
  },
  plugins: [],
}
