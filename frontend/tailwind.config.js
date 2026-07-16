/** @type {import('tailwindcss').Config} */
module.exports = {
  darkMode: ['class'], // enable class‑based dark mode
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        // Primary brand
        primary: '#0F62FE', // blue‑700
        'primary-foreground': '#ffffff',
        // Secondary accent
        secondary: '#6F42C1', // purple‑700
        'secondary-foreground': '#ffffff',
        // Semantic colors
        success: '#00C853',
        'success-foreground': '#ffffff',
        warning: '#FFAB00',
        'warning-foreground': '#000000',
        danger: '#D32F2F',
        'danger-foreground': '#ffffff',
        // Background / surface
        background: '#F5F7FA',
        surface: 'rgba(255,255,255,0.55)', // glass surface (light)
        "surface-dark": 'rgba(30,30,42,0.65)', // glass surface (dark)
        // Text colors
        foreground: '#1F2937', // primary text (light)
        'foreground-dark': '#F1F5F9', // primary text (dark)
        muted: '#9CA3AF', // secondary text / muted
        // Border / divider
        border: 'rgba(0,0,0,0.08)',
        "border-dark": 'rgba(255,255,255,0.12)',
      },
      spacing: {
        0: '0px',
        1: '4px',
        2: '8px',
        3: '12px',
        4: '16px',
        5: '24px',
        6: '32px',
        7: '48px',
        8: '64px',
      },
      borderRadius: {
        xl: '1rem',
        lg: '0.75rem',
        md: '0.5rem',
        sm: '0.25rem',
      },
      fontFamily: {
        display: ['Inter', 'system-ui', 'sans-serif'],
        body: ['Inter', 'system-ui', 'sans-serif'],
        mono: ['JetBrains Mono', 'ui-monospace', 'monospace'],
      },
      fontSize: {
        display: ['2.5rem', { lineHeight: '3rem', letterSpacing: '-0.02em' }],
        h1: ['2rem', { lineHeight: '2.5rem', letterSpacing: '-0.01em' }],
        h2: ['1.5rem', { lineHeight: '2rem' }],
        h3: ['1.25rem', { lineHeight: '1.75rem' }],
        base: ['1rem', { lineHeight: '1.5rem' }],
        sm: ['0.875rem', { lineHeight: '1.375rem' }],
        xs: ['0.75rem', { lineHeight: '1.125rem' }],
      },
      backdropBlur: {
        md: '12px',
      },
      transitionDuration: {
        150: '150ms',
        200: '200ms',
        250: '250ms',
      },
      transitionTimingFunction: {
        'ease-out': 'cubic-bezier(0.25,0.8,0.5,1)',
      },
    },
  },
  plugins: [require('tailwindcss-animate')],
};
