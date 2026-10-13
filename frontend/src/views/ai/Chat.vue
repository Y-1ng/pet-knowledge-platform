<template>
  <div class="chat-page">
    <!-- 聊天记录区域 -->
    <div class="chat-messages" ref="messagesRef">
      <div v-if="messages.length === 0" class="chat-empty">
        <div class="ai-avatar">🤖</div>
        <h3>AI宠物顾问 · 小宠</h3>
        <p>问我任何关于宠物的问题吧！</p>
        <div class="suggestions">
          <div class="suggestion" v-for="s in suggestions" :key="s" @click="sendMessage(s)">
            {{ s }}
          </div>
        </div>
      </div>

      <div v-for="msg in messages" :key="msg.id" :class="'msg ' + msg.role">
        <div class="msg-content">
          <div class="bubble">{{ msg.content }}</div>
          <div class="time">{{ formatTime(msg.createTime) }}</div>
        </div>
      </div>

      <div v-if="loading" class="msg assistant">
        <div class="msg-content">
          <div class="bubble thinking">小宠正在思考...</div>
        </div>
      </div>
    </div>

    <!-- 底部输入区 -->
    <div class="chat-input">
      <van-field
        v-model="inputMessage"
        placeholder="输入你的宠物问题..."
        :border="false"
        @keypress.enter="sendCurrentMessage"
      />
      <van-button
        icon="chat-o"
        type="primary"
        color="#6c8c5e"
        size="small"
        :disabled="!inputMessage.trim()"
        @click="sendCurrentMessage"
      />
      <van-button
        icon="delete-o"
        size="small"
        plain
        @click="clearHistory"
        style="margin-left: 4px"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted } from 'vue';
import { showToast, showConfirmDialog } from 'vant';
import request from '@/utils/request';

const sessionId = ref('local-' + Date.now() + '-' + Math.random().toString(36).slice(2));
const messages = ref<any[]>([]);
const inputMessage = ref('');
const loading = ref(false);
const messagesRef = ref<HTMLElement | null>(null);

const suggestions = [
  '猫咪不能吃什么食物？',
  '新手养狗要注意什么？',
  '狗狗疫苗多久打一次？',
  '兔子可以洗澡吗？',
];

onMounted(async () => {
  await loadHistory();
});

async function loadHistory() {
  try {
    const res = await request.get('/ai/history', { params: { sessionId: sessionId.value } });
    messages.value = res.data || [];
    scrollToBottom();
  } catch (e) {}
}

function sendMessage(msg: string) {
  inputMessage.value = msg;
  sendCurrentMessage();
}

async function sendCurrentMessage() {
  const msg = inputMessage.value.trim();
  if (!msg || loading.value) return;

  // 添加到本地
  messages.value.push({ id: Date.now(), role: 'user', content: msg, createTime: new Date().toISOString() });
  inputMessage.value = '';
  loading.value = true;
  scrollToBottom();

  try {
    const res = await request.post('/ai/chat', {
      sessionId: sessionId.value,
      message: msg
    });
    if (res.code === 200) {
      messages.value.push({ id: Date.now() + 1, role: 'assistant', content: res.data, createTime: new Date().toISOString() });
    }
  } catch (e) {
    messages.value.push({ id: Date.now() + 1, role: 'assistant', content: '抱歉，小宠暂时无法回复，请稍后再试。🐾', createTime: new Date().toISOString() });
  }
  loading.value = false;
  scrollToBottom();
}

async function clearHistory() {
  showConfirmDialog({ title: '提示', message: '确定清空所有聊天记录吗？' }).then(async () => {
    try {
      await request.delete('/ai/history', { params: { sessionId: sessionId.value } });
    } catch (e) {}
    messages.value = [];
    // 重新生成sessionId
    sessionId.value = 'local-' + Date.now() + '-' + Math.random().toString(36).slice(2);
  }).catch(() => {});
}

function scrollToBottom() {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight;
    }
  });
}

function formatTime(t: string) {
  if (!t) return '';
  return t.substring(11, 16);
}
</script>

<style scoped>
.chat-page { display: flex; flex-direction: column; height: 100%; }
.chat-messages { flex: 1; overflow-y: auto; padding: 16px; }
.chat-empty { text-align: center; padding: 40px 20px; }
.ai-avatar { font-size: 64px; margin-bottom: 12px; }
.chat-empty h3 { margin: 0 0 8px; font-size: 18px; color: #333; }
.chat-empty p { margin: 0 0 20px; font-size: 14px; color: #999; }
.suggestions { display: flex; flex-direction: column; gap: 8px; }
.suggestion {
  padding: 10px 16px;
  background: #f0f7ee;
  border-radius: 20px;
  font-size: 14px;
  color: #6c8c5e;
  cursor: pointer;
}

.msg { margin-bottom: 16px; display: flex; }
.msg.user { justify-content: flex-end; }
.msg.assistant { justify-content: flex-start; }
.msg-content { max-width: 80%; }
.bubble {
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
  color: #333;
}
.msg.user .bubble { background: #6c8c5e; color: #fff; border-bottom-right-radius: 4px; }
.msg.assistant .bubble { background: #f0f0f0; border-bottom-left-radius: 4px; }
.thinking { color: #999 !important; }
.time { font-size: 11px; color: #ccc; margin-top: 4px; text-align: right; }
.msg.user .time { text-align: right; }
.msg.assistant .time { text-align: left; }

.chat-input {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border-top: 1px solid #eee;
  background: #fff;
}
.chat-input .van-field { flex: 1; }
</style>
