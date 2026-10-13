<template>
  <div class="article-detail">
    <van-skeleton title :row="10" :loading="!article.title">
      <div class="article-header">
        <h2>{{ article.title }}</h2>
        <div class="meta">
          <span>👁 {{ article.viewCount }}</span>
          <span>💬 {{ article.commentCount ?? 0 }}</span>
          <span>{{ article.createTime?.substring(0, 10) }}</span>
        </div>
      </div>
      <div class="article-body" v-html="renderedContent"></div>

      <!-- 评论区 — 使用通用评论组件 -->
      <div class="divider">— 评论区 —</div>
      <CommentSection :target-id="Number(route.params.id)" @update:count="onCommentCountUpdate" />
    </van-skeleton>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import request from '@/utils/request';
import { marked } from 'marked';
import CommentSection from '@/components/CommentSection.vue';

const route = useRoute();
const article = ref<any>({});

const renderedContent = computed(() => {
  if (!article.value.content) return '';
  return marked(article.value.content, { breaks: true });
});

onMounted(async () => {
  try {
    const res = await request.get(`/knowledge/${route.params.id}`);
    article.value = res.data || {};
  } catch (e) {}
});

function onCommentCountUpdate(count: number) {
  if (article.value) {
    article.value.commentCount = count
  }
}
</script>

<style scoped>
.article-detail { background: #fff; min-height: 100%; padding-bottom: 20px; }
.article-header { padding: 20px 16px; margin-bottom: 0; border-bottom: 1px solid #f0f0f0; }
.article-header h2 { margin: 0 0 8px; font-size: 22px; color: #222; }
.meta { font-size: 13px; color: #999; display: flex; gap: 16px; }
.article-body { padding: 20px 16px; font-size: 15px; line-height: 1.8; color: #444; }
.article-body :deep(h1), .article-body :deep(h2), .article-body :deep(h3) { margin: 20px 0 10px; color: #333; }
.article-body :deep(li) { margin: 4px 0 4px 20px; }
.article-body :deep(blockquote) { margin: 12px 0; padding: 12px 16px; background: #f8f8f8; border-left: 4px solid #6c8c5e; color: #666; font-style: italic; }
.article-body :deep(img) { max-width: 100%; border-radius: 8px; margin: 12px 0; }

.divider { text-align: center; color: #ccc; font-size: 13px; padding: 20px 0 0; }
</style>
