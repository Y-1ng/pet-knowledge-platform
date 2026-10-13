<template>
  <div class="guide-detail">
    <van-skeleton title :row="8" :loading="!guide.title">
      <h2>{{ guide.title }}</h2>
      <div class="meta">
        <span>{{ typeLabel(guide.type) }}</span>
        <span>👁 {{ guide.viewCount }}</span>
      </div>
      <div class="guide-body" v-html="renderMarkdown(guide.content)"></div>
    </van-skeleton>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import request from '@/utils/request';

const route = useRoute();
const guide = ref<any>({});

onMounted(async () => {
  try {
    const res = await request.get(`/guide/${route.params.id}`);
    guide.value = res.data || {};
  } catch (e) {}
});

function typeLabel(t: string) {
  const map: any = { feeding: '🍽️ 饲养指南', health: '🏥 健康护理', training: '🎯 行为训练' };
  return map[t] || t;
}

function renderMarkdown(md: string) {
  if (!md) return '';
  return md
    .replace(/### (.+)/g, '<h3>$1</h3>')
    .replace(/## (.+)/g, '<h2>$1</h2>')
    .replace(/# (.+)/g, '<h1>$1</h1>')
    .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
    .replace(/\|(.+)\|/g, (m: string) => m.includes('---') ? '' : m)
    .replace(/\n- (.+)/g, '<li>$1</li>')
    .replace(/\n/g, '<br>')
    .replace(/> (.+)/g, '<blockquote>$1</blockquote>');
}
</script>

<style scoped>
.guide-detail { padding: 20px 16px; background: #fff; min-height: 100%; }
.guide-detail h2 { margin: 0 0 8px; font-size: 22px; }
.meta { font-size: 13px; color: #999; margin-bottom: 20px; display: flex; gap: 16px; }
.guide-body { font-size: 15px; line-height: 1.8; color: #444; }
.guide-body :deep(h1), .guide-body :deep(h2), .guide-body :deep(h3) { margin: 20px 0 10px; color: #333; }
.guide-body :deep(li) { margin: 4px 0 4px 20px; }
.guide-body :deep(blockquote) { margin: 12px 0; padding: 12px 16px; background: #f8f8f8; border-left: 4px solid #6c8c5e; }
</style>
