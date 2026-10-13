<template>
  <div class="qa-detail">
    <!-- 问题 -->
    <div class="question-section">
      <h2>{{ question.title }}</h2>
      <p class="question-desc">{{ question.description }}</p>
      <div class="meta">👁 {{ question.viewCount }} · 💬 {{ question.answerCount }} 个回答</div>
    </div>

    <!-- 回答列表 -->
    <div class="answers-section">
      <h3>回答 ({{ answers.length }})</h3>
      <div v-for="a in answers" :key="a.id" class="answer-item" :class="{ accepted: a.isAccepted }">
        <div class="answer-content">{{ a.content }}</div>
        <div class="answer-meta">
          <span v-if="a.isAccepted" class="accept-badge">✅ 已采纳</span>
          <span class="answer-time">{{ a.createTime?.substring(0, 10) }}</span>
        </div>
      </div>
      <van-empty v-if="answers.length === 0" description="还没有回答" />
    </div>

    <!-- 回答输入 -->
    <div class="answer-input">
      <van-field v-model="answerText" placeholder="写下你的回答..." :border="false" type="textarea" rows="2" />
      <van-button size="small" color="#6c8c5e" @click="addAnswer">回答</van-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { showToast } from 'vant';
import request from '@/utils/request';
import { useUserStore } from '@/stores/user';

const route = useRoute();
const userStore = useUserStore();
const question = ref<any>({});
const answers = ref<any[]>([]);
const answerText = ref('');

onMounted(async () => {
  const id = route.params.id;
  try {
    const res = await request.get(`/qa/question/${id}`);
    question.value = res.data || {};
  } catch (e) {}
  try {
    const res = await request.get(`/qa/answers/${id}`);
    answers.value = res.data || [];
  } catch (e) {}
});

async function addAnswer() {
  if (!userStore.isLoggedIn) { showToast('请先登录'); return; }
  if (!answerText.value.trim()) { showToast('请输入回答内容'); return; }
  try {
    await request.post('/qa/answer', {
      questionId: route.params.id,
      userId: userStore.userInfo?.id,
      content: answerText.value
    });
    showToast('回答成功');
    answerText.value = '';
    const res = await request.get(`/qa/answers/${route.params.id}`);
    answers.value = res.data || [];
    question.value.answerCount = (question.value.answerCount || 0) + 1;
  } catch (e) {}
}
</script>

<style scoped>
.qa-detail { background: #f7f8fa; min-height: 100%; padding-bottom: 70px; }
.question-section { background: #fff; padding: 20px 16px; }
.question-section h2 { margin: 0 0 10px; font-size: 20px; }
.question-desc { font-size: 15px; color: #555; line-height: 1.6; margin: 0 0 10px; }
.meta { font-size: 13px; color: #999; }

.answers-section { padding: 16px; }
.answers-section h3 { margin: 0 0 12px; font-size: 16px; }
.answer-item { background: #fff; border-radius: 10px; padding: 16px; margin-bottom: 10px; }
.answer-item.accepted { border: 2px solid #6c8c5e; }
.answer-content { font-size: 14px; line-height: 1.6; color: #444; margin-bottom: 8px; }
.answer-meta { display: flex; justify-content: space-between; align-items: center; }
.accept-badge { font-size: 12px; color: #6c8c5e; }
.answer-time { font-size: 12px; color: #bbb; }

.answer-input {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border-top: 1px solid #eee;
  background: #fff;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  width: 100%;
  z-index: 100;
}
.answer-input .van-field { flex: 1; }
</style>
