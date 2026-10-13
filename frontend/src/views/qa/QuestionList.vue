<template>
  <div class="qa-list">
    <van-search v-model="keyword" placeholder="搜索问题..." @search="onSearch" />

    <div class="qa-wrap">
      <div v-for="q in questions" :key="q.id" class="qa-item" @click="goDetail(q.id)">
        <div class="qa-stats">
          <div class="stat">
            <span class="num">{{ q.answerCount }}</span>
            <span class="label">回答</span>
          </div>
          <div class="stat">
            <span class="num">{{ q.viewCount }}</span>
            <span class="label">浏览</span>
          </div>
        </div>
        <div class="qa-info">
          <h4>{{ q.title }}</h4>
          <p>{{ q.description?.substring(0, 80) }}</p>
          <span class="qa-time">{{ q.createTime?.substring(0, 10) }}</span>
        </div>
      </div>
      <van-empty v-if="questions.length === 0" description="还没有问题" />
    </div>

    <van-floating-bubble icon="chat-o" color="#6c8c5e" @click="goAsk" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import request from '@/utils/request';

const router = useRouter();
const keyword = ref('');
const questions = ref<any[]>([]);

onMounted(() => loadQuestions());

async function loadQuestions() {
  try {
    const res = await request.get('/qa/questions');
    questions.value = res.data || [];
  } catch (e) {}
}

function onSearch() {
  if (!keyword.value.trim()) { loadQuestions(); return; }
  request.get('/qa/search', { params: { keyword: keyword.value } }).then(res => {
    questions.value = res.data || [];
  }).catch(() => {});
}

function goDetail(id: number) {
  router.push(`/qa/question/${id}`);
}

function goAsk() {
  router.push('/qa/ask');
}
</script>

<style scoped>
.qa-list { background: #f7f8fa; min-height: 100%; }
.qa-wrap { padding: 12px; }
.qa-item {
  background: #fff;
  border-radius: 10px;
  padding: 16px;
  margin-bottom: 12px;
  display: flex;
  gap: 16px;
  cursor: pointer;
}
.qa-stats { text-align: center; min-width: 50px; }
.stat { margin-bottom: 8px; }
.num { display: block; font-size: 18px; font-weight: bold; color: #6c8c5e; }
.label { font-size: 11px; color: #aaa; }
.qa-info { flex: 1; }
.qa-info h4 { margin: 0 0 6px; font-size: 15px; color: #333; }
.qa-info p { margin: 0 0 6px; font-size: 13px; color: #888; }
.qa-time { font-size: 12px; color: #bbb; }
</style>
