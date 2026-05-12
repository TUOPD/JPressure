<template>
  <div class="container">

    <!-- 顶部栏 -->
    <div class="header">
      <h2>JPressure 控制台</h2>
      <div>
        <el-button type="primary" @click="startTest">启动</el-button>
        <el-button type="danger" @click="stopTest">停止</el-button>
      </div>
    </div>

    <!-- 主体区域 -->
   <div class="main">

  <!-- 左侧请求编辑区 -->
  <el-card class="left-panel">

    <h3>请求配置</h3>

    <!-- ✅ 协议选择 -->
    <el-form :model="form" label-width="80px">

      <el-form-item label="协议">
        <el-select v-model="form.protocol" style="width:100%">
          <el-option label="HTTP" value="HTTP"/>
          <el-option label="TCP" value="TCP"/>
          <el-option label="WebSocket" value="WEBSOCKET"/>
          <el-option label="MySQL" value="MYSQL"/>
          <el-option label="Redis" value="REDIS"/>
        </el-select>
      </el-form-item>

      <!-- ✅ HTTP 模式 -->
      <template v-if="form.protocol === 'HTTP'">

        <el-form-item label="方法">
          <el-select v-model="form.method" style="width:100%">
            <el-option label="GET" value="GET"/>
            <el-option label="POST" value="POST"/>
            <el-option label="PUT" value="PUT"/>
            <el-option label="DELETE" value="DELETE"/>
          </el-select>
        </el-form-item>

        <el-form-item label="URL">
          <el-input v-model="form.url" placeholder="请求URL"/>
        </el-form-item>

        <h4>Headers</h4>

        <el-table :data="form.headers" size="small" border>
          <el-table-column label="Key">
            <template #default="scope">
              <el-input v-model="scope.row.key"/>
            </template>
          </el-table-column>
          <el-table-column label="Value">
            <template #default="scope">
              <el-input v-model="scope.row.value"/>
            </template>
          </el-table-column>
        </el-table>

        <el-button size="small" @click="addHeader" style="margin:10px 0">
          添加 Header
        </el-button>

        <h4>Body</h4>

        <el-input
          type="textarea"
          v-model="form.body"
          rows="4"
          placeholder="JSON Body"
        />

      </template>

      <!-- ✅ TCP 模式 -->
      <template v-if="form.protocol === 'TCP'">
        <el-form-item label="Host">
          <el-input v-model="form.host" placeholder="127.0.0.1"/>
        </el-form-item>

        <el-form-item label="Port">
          <el-input-number v-model="form.port"/>
        </el-form-item>

        <el-form-item label="消息">
          <el-input v-model="form.message" placeholder="发送内容"/>
        </el-form-item>
      </template>

      <!-- ✅ WebSocket 模式 -->
      <template v-if="form.protocol === 'WEBSOCKET'">
        <el-form-item label="URL">
          <el-input v-model="form.url" placeholder="ws://localhost:8080/ws"/>
        </el-form-item>
      </template>

      <!-- ✅ MySQL 模式 -->
      <template v-if="form.protocol === 'MYSQL'">
        <el-form-item label="JDBC">
          <el-input v-model="form.jdbcUrl" placeholder="jdbc:mysql://localhost:3306/test"/>
        </el-form-item>

        <el-form-item label="用户名">
          <el-input v-model="form.username"/>
        </el-form-item>

        <el-form-item label="密码">
          <el-input type="password" v-model="form.password"/>
        </el-form-item>

        <el-form-item label="SQL">
          <el-input
            type="textarea"
            rows="3"
            v-model="form.sql"
            placeholder="SELECT * FROM user"
          />
        </el-form-item>
      </template>

      <!-- ✅ Redis 模式 -->
      <template v-if="form.protocol === 'REDIS'">
        <el-form-item label="Host">
          <el-input v-model="form.host"/>
        </el-form-item>

        <el-form-item label="Port">
          <el-input-number v-model="form.port"/>
        </el-form-item>
      </template>

    </el-form>

    <!-- ✅ 压测参数 -->
    <h4 style="margin-top:20px">压测参数</h4>

    <el-form :model="form" label-width="80px">
      <el-form-item label="线程数">
        <el-input-number v-model="form.threads"/>
      </el-form-item>

      <el-form-item label="QPS">
        <el-input-number v-model="form.qps"/>
      </el-form-item>

      <el-form-item label="持续时间">
        <el-input-number v-model="form.duration"/>
      </el-form-item>
    </el-form>

  </el-card>

  <!-- 右侧图表区 -->
  <el-card class="right-panel">
    <div ref="chartRef" style="height:400px;"></div>
  </el-card>

</div>

    <!-- 底部统计表 -->
    <el-card style="margin-top:20px">
      <el-table :data="metricsList" border>

        <el-table-column prop="qps" label="当前QPS"/>
        <el-table-column prop="totalSuccess" label="成功数"/>
        <el-table-column prop="totalFail" label="失败数"/>
        <el-table-column prop="avgRt" label="平均RT(ms)"/>
        <el-table-column prop="tp90" label="TP90(ms)"/>
        <el-table-column prop="tp99" label="TP99(ms)"/>

      </el-table>
    </el-card>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'

const form = ref({

  // ✅ 协议类型
  protocol: 'HTTP',

  // ========================
  // ✅ HTTP 专用
  // ========================
  method: 'GET',
  url: '',
  headers: [],
  body: '',

  // ========================
  // ✅ TCP / Redis
  // ========================
  host: '',
  port: 80,
  message: '',

  // ========================
  // ✅ MySQL
  // ========================
  jdbcUrl: '',
  username: '',
  password: '',
  sql: '',

  // ========================
  // ✅ 公共压测参数
  // ========================
  threads: 10,
  qps: 50,
  duration: 60

})

const metricsList = ref([])
let socket = null
// eslint-disable-next-line no-unused-vars
function addHeader () {
  form.value.headers.push({ key: '', value: '' })
}

// eslint-disable-next-line no-unused-vars
async function startTest () {
  metricsList.value = []
  timeData.length = 0
  qpsData.length = 0
  console.log('Starting test with config:', form.value)
  await fetch('http://localhost:8080/api/test/start', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(form.value)
  })
  connectWebSocket()
}

// eslint-disable-next-line no-unused-vars
async function stopTest () {
  await fetch('http://localhost:8080/api/test/stop', { method: 'POST' })
  if (socket) {
    socket.close()
    socket = null
  }
}

const chartRef = ref(null)
let chart

const timeData = []
const qpsData = []
const rtData = []
const tp99Data = []

function initChart () {
  chart = echarts.init(chartRef.value)

  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['QPS', 'AvgRT', 'TP99'] },
    xAxis: { type: 'category', data: timeData },
    yAxis: { type: 'value' },
    series: [
      { name: 'QPS', type: 'line', data: qpsData },
      { name: 'AvgRT', type: 'line', data: rtData },
      { name: 'TP99', type: 'line', data: tp99Data }
    ]
  })
}

function updateChart (data) {
  if (!data.qps) return

  const now = new Date().toLocaleTimeString()

  timeData.push(now)
  qpsData.push(data.qps)
  rtData.push(data.avgRt)
  tp99Data.push(data.tp99)

  if (timeData.length > 30) {
    timeData.shift()
    qpsData.shift()
    rtData.shift()
    tp99Data.shift()
  }

  chart.setOption({
    xAxis: { data: timeData },
    series: [
      { data: qpsData },
      { data: rtData },
      { data: tp99Data }
    ]
  })
}
function connectWebSocket () {
  socket = new WebSocket('ws://localhost:8080/ws/metrics')

  socket.onmessage = (event) => {
    const data = JSON.parse(event.data)
    metricsList.value.push({
      ...data,
      time: new Date().toLocaleTimeString()
    })

    updateChart(data)

    // 限制最多保存 100 条
    if (metricsList.value.length > 100) {
      metricsList.value.shift()
    }
  }

  socket.onclose = () => {
    console.log('WebSocket 已关闭')
  }
}
onMounted(() => {
  initChart()
})
</script>

<style>
.container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.main {
  display: flex;
  gap: 20px;
  margin-top: 20px;
}

.left-panel {
  width: 400px;
}

.right-panel {
  flex: 1;
}
</style>
