<template>
  <div class="post-detail">
    <!-- 帖子内容 -->
    <div class="post-content">
      <h2>{{ post.title }}</h2>
      <div class="meta">
        <span>{{ post.username || `用户${post.userId}` }}</span>
        <span>❤️ {{ post.likeCount }}</span>
        <span>💬 {{ post.commentCount }}</span>
        <span>{{ post.createTime?.substring(0, 10) }}</span>
      </div>
      <div class="content-body">{{ post.content }}</div>
      <div class="actions">
        <van-button :icon="isLiked ? 'like' : 'like-o'" :color="isLiked ? '#ee0a24' : '#666'" size="small" plain @click="toggleLike">
          {{ isLiked ? '已点赞' : '点赞' }}
        </van-button>
      </div>
    </div>

    <!-- 评论区 — 使用通用评论组件 -->
    <div class="comments-wrap">
      <h3 class="comments-title">评论</h3>
      <CommentSection :target-id="Number(route.params.id)" :api="communityApi" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { showToast } from 'vant';
import request from '@/utils/request';
import { useUserStore } from '@/stores/user';
import CommentSection, { type CommentApi } from '@/components/CommentSection.vue';

const route = useRoute();
const userStore = useUserStore();
const post = ref<any>({});
const isLiked = ref(false);

/** 社区评论 API 适配器 */
const communityApi: CommentApi = {
  getList: async (params) => {
    return request.get(`/community/comments/${params.targetId}`, {
      params: { page: params.page, size: params.size }
    })
  },
  getReplies: async (targetId, parentId) => {
    return request.get(`/community/comments/${targetId}/replies/${parentId}`)
  },
  add: async (data) => {
    return request.post('/community/comment', {
      postId: data.targetId,
      userId: data.userId,
      content: data.content,
      parentId: data.parentId || null,
      replyToUserId: data.replyToUserId || null,
      replyToUsername: data.replyToUsername || null
    })
  },
  delete: async (id, userId) => {
    return request.delete(`/community/comment/${id}`, { params: { userId } })
  }
}

onMounted(async () => {
  const id = route.params.id;
  try {
    const res = await request.get(`/community/post/${id}`);
    post.value = res.data || {};
  } catch (e) {}
  if (userStore.isLoggedIn) {
    try {
      const res = await request.get('/community/like/check', { params: { postId: id, userId: userStore.userInfo?.id } });
      isLiked.value = res.data || false;
    } catch (e) {}
  }
});

async function toggleLike() {
  if (!userStore.isLoggedIn) { showToast('请先登录'); return; }
  try {
    const res = await request.post('/community/like', { postId: route.params.id, userId: userStore.userInfo?.id });
    isLiked.value = res.data;
    if (res.data) post.value.likeCount = (post.value.likeCount || 0) + 1;
    else post.value.likeCount = Math.max(0, (post.value.likeCount || 0) - 1);
  } catch (e) {}
}
</script>

<style scoped>
.post-detail { background: #fff; min-height: 100%; padding-bottom: 20px; }
.post-content { padding: 20px 16px; border-bottom: 8px solid #f7f8fa; }
.post-content h2 { margin: 0 0 8px; font-size: 20px; }
.meta { font-size: 13px; color: #999; display: flex; gap: 16px; margin-bottom: 16px; }
.content-body { font-size: 15px; line-height: 1.8; color: #444; margin-bottom: 16px; }
.actions { display: flex; gap: 12px; }

.comments-wrap {
  padding: 0 16px;
}
.comments-title {
  margin: 16px 0 0;
  font-size: 16px;
  color: #333;
}
</style>
