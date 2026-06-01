import { defineConfig } from 'vite';
import vue2 from '@vitejs/plugin-vue2';

export default defineConfig({
  plugins: [vue2()],
  server: {
    port: 5173,
    open: true,
    proxy: {
      '/ai': {
        target: 'http://localhost:8090',
        changeOrigin: true,
      },
    },
  },
});
