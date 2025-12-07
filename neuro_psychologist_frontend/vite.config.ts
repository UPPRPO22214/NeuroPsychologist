import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  
  // ВАЖНО: base должен указывать на S3 бакет для абсолютных путей
  base: 'https://storage.yandexcloud.net/neuropsychologist-front/',
  
  build: {
    // Выходная директория
    outDir: 'dist',
    
    // Не генерировать source maps для продакшена
    sourcemap: false,
    
    // Минификация (esbuild быстрее и встроен в Vite)
    minify: 'esbuild',
    
    // Директория для ассетов
    assetsDir: 'assets',
    
    rollupOptions: {
      output: {
        // Настройка имен файлов с хешами для кеширования
        entryFileNames: 'assets/[name]-[hash].js',
        chunkFileNames: 'assets/[name]-[hash].js',
        assetFileNames: 'assets/[name]-[hash].[ext]',
        
        // Разделение на чанки для оптимизации
        manualChunks: {
          'react-vendor': ['react', 'react-dom', 'react-router-dom'],
          'store': ['zustand'],
        }
      }
    }
  },
  
  // Настройки dev сервера
  server: {
    port: 5173,
    host: true,
    
    // Прокси для API в режиме разработки
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
      '/actuator': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      }
    }
  },
  
  // Настройки preview
  preview: {
    port: 4173,
    host: true,
  }
})
