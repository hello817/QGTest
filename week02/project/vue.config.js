module.exports = {
  devServer: {
    port: 8082, // 前端运行端口
    proxy: {
      '/users': {
        target: 'http://localhost:8081', // 你的后端地址
        changeOrigin: true
      },
      '/orders': {
        target: 'http://localhost:8081',
        changeOrigin: true
      }
    }
  }
};