import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    // During development, forward /api calls to the Spring Boot backend
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
  build: {
    // Build straight into Spring's static folder so the backend
    // serves the frontend — one deployable application.
    outDir: '../src/main/resources/static',
    emptyOutDir: true,
  },
});
