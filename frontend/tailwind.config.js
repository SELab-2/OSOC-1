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
       'osoc-btn-primary': '#1DE1AE'
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
