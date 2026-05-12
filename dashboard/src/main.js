import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
// Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// ECharts（按需不需要全局引入）
import * as echarts from 'echarts'

// 创建应用
const app = createApp(App)

// 全局注册
app.use(ElementPlus)

// 可选：挂载全局属性
app.config.globalProperties.$echarts = echarts

// 挂载
app.use(router)
app.mount('#app')
