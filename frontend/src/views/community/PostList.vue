<template>
  <div class="post-list">
    <div class="post-wrap">
      <div v-for="post in posts" :key="post.id" class="post-item" @click="goDetail(post.id)">
        <h4>{{ post.title }}</h4>
        <p>{{ post.content?.substring(0, 100) }}{{ post.content?.length > 100 ? '...' : '' }}</p>
        <div class="meta">
          <span>❤️ {{ post.likeCount }}</span>
          <span>💬 {{ post.commentCount }}</span>
          <span>{{ post.createTime?.substring(0, 10) }}</span>
        </div>
      </div>
      <van-empty v-if="posts.length === 0" description="还没有帖子，快来发布第一条吧！" />
    </div>

    <!-- 浮动发布按钮 -->
    <van-floating-bubble icon="chat-o" color="#6c8c5e" @click="goCreate" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import request from '@/utils/request';

const router = useRouter();
const posts = ref<any[]>([]);

onMounted(async () => {
  try {
    const res = await request.get('/community/posts');
    posts.value = res.data || [];
  } catch (e) {}
});

function goDetail(id: number) {
  router.push(`/community/post/${id}`);
}

function goCreate() {
  router.push('/community/create');
}
</script>

<style scoped>
.post-list { background: #f7f8fa; min-height: 100%; }
.post-wrap { padding: 12px; }
.post-item {
  background: #fff;
  border-radius: 10px;
  padding: 16px;
  margin-bottom: 12px;
  cursor: pointer;
}
.post-item h4 { margin: 0 0 8px; font-size: 16px; color: #333; }
.post-item p { margin: 0 0 10px; font-size: 14px; color: #666; line-height: 1.5; }
.meta { display: flex; gap: 16px; font-size: 12px; color: #aaa; }
</style>
