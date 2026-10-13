<template>
  <div class="my-moments-page">
    <div class="page-header">
      <van-nav-bar title="我的动态" left-arrow @click-left="router.back()" />
    </div>

    <div class="moment-list">
      <div v-for="m in moments" :key="m.id" class="moment-card">
        <!-- 内容 -->
        <div class="moment-content">{{ m.content }}</div>

        <!-- 图片 -->
        <div v-if="m.images" class="moment-images">
          <van-image v-for="(img, idx) in parseImages(m.images)" :key="idx" :src="img" fit="cover" @click="previewImg(idx, m.images)" />
        </div>

        <!-- 时间和操作 -->
        <div class="moment-footer">
          <span class="moment-time">{{ formatTime(m.createTime) }}</span>
          <div class="moment-actions">
            <span :class="['action-btn', { liked: m.isLiked }]" @click="toggleLike(m)">
              {{ m.isLiked ? '❤️' : '🤍' }} {{ m.likeCount || 0 }}
            </span>
            <span class="action-btn" @click="showComments(m)">
              💬 {{ m.commentCount || 0 }}
            </span>
            <van-icon name="delete-o" color="#ee0a24" class="delete-btn" @click="deleteMoment(m.id)" />
          </div>
        </div>

        <!-- 评论区 -->
        <div class="comment-preview" v-if="m._showComments">
          <div class="comment-list">
            <div v-for="c in momentComments[m.id] || []" :key="c.id" :class="['comment-item', { 'is-reply': c.parentId }]">
              <span class="comment-user">{{ getUsername(c) }}</span>
              <span v-if="c.replyToUsername" class="reply-tag">回复 @{{ c.replyToUsername }}：</span>
              <span class="comment-text">{{ c.content }}</span>
            </div>
            <van-empty v-if="!momentComments[m.id]?.length" description="暂无评论" image-size="40" />
          </div>
          <div class="comment-input" @click.stop>
            <van-field :model-value="commentTexts[m.id] || ''" @update:model-value="commentTexts[m.id] = $event" :placeholder="replyTargets[m.id] ? '输入回复...' : '写评论...'" size="small" @keypress.enter="submitComment(m)" />
            <van-button size="mini" color="#6c8c5e" @click="submitComment(m)">发送</van-button>
          </div>
        </div>
      </div>

      <van-empty v-if="moments.length === 0" description="还没有发过动态">
        <van-button round type="primary" color="#6c8c5e" size="small" @click="router.push('/moments')">去发动态</van-button>
      </van-empty>
    </div>

    <!-- 图片预览 -->
    <van-image-preview v-model:show="showPreview" :images="previewImages" :start-position="previewIndex" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { showToast, showConfirmDialog } from 'vant';
import request from '@/utils/request';
import { useUserStore } from '@/stores/user';

const router = useRouter();
const userStore = useUserStore();
const moments = ref<any[]>([]);
const momentComments = reactive<Record<number, any[]>>({});
const commentTexts = reactive<Record<number, string>>({});
const replyTargets = reactive<Record<number, any>>({});
const showPreview = ref(false);
const previewImages = ref<string[]>([]);
const previewIndex = ref(0);

onMounted(async () => {
  await loadMyMoments();
});

async function loadMyMoments() {
  if (!userStore.isLoggedIn || !userStore.userInfo?.id) return;
  try {
    const res = await request.get(`/moment/list/${userStore.userInfo.id}`);
    moments.value = (res.data || []).map((m: any) => {
      m._showComments = false;
      m.isLiked = false;
      return m;
    });
  } catch (e) {
    showToast('加载失败');
  }
}

function getUsername(u: any) { return u.username || `用户${u.userId}`; }
function formatTime(t: string) { return t ? t.substring(0, 16).replace('T', ' ') : ''; }

function parseImages(images: string) {
  try { return JSON.parse(images); } catch { return [images]; }
}

async function deleteMoment(id: number) {
  try {
    await showConfirmDialog({ title: '提示', message: '确定删除这条动态吗？' });
    console.log('Deleting moment:', id, 'userId:', userStore.userInfo?.id);
    const res = await request.delete(`/moment/${id}`, { params: { userId: userStore.userInfo?.id } });
    console.log('Delete response:', res);
    showToast('已删除');
    await loadMyMoments();
  } catch (e) {
    console.error('Delete failed:', e);
  }
}

async function toggleLike(m: any) {
  if (!userStore.isLoggedIn) { showToast('请先登录'); return; }
  try {
    const res = await request.post('/moment/like', { momentId: m.id, userId: userStore.userInfo?.id });
    m.isLiked = res.data;
    m.likeCount = (m.likeCount || 0) + (res.data ? 1 : -1);
  } catch (e) {}
}

async function showComments(m: any) {
  m._showComments = !m._showComments;
  if (m._showComments && !momentComments[m.id]) {
    try {
      const res = await request.get(`/moment/comments/${m.id}`);
      momentComments[m.id] = res.data || [];
    } catch (e) {}
  }
}

async function submitComment(m: any) {
  if (!userStore.isLoggedIn) { showToast('请先登录'); return; }
  const text = commentTexts[m.id];
  if (!text?.trim()) return;
  const reply = replyTargets[m.id];
  try {
    await request.post('/moment/comment', {
      momentId: m.id,
      userId: userStore.userInfo?.id,
      content: text,
      parentId: reply?.id || null,
      replyToUserId: reply?.userId || null,
      replyToUsername: reply ? getUsername(reply) : null
    });
    commentTexts[m.id] = '';
    replyTargets[m.id] = null;
    const res = await request.get(`/moment/comments/${m.id}`);
    momentComments[m.id] = res.data || [];
    m.commentCount = (m.commentCount || 0) + 1;
  } catch (e) {}
}

function previewImg(idx: number, images: string) {
  const list = parseImages(images);
  previewImages.value = list;
  previewIndex.value = idx;
  showPreview.value = true;
}
</script>

<style scoped>
.my-moments-page {
  min-height: 100%;
  background: #f7f8fa;
}

.page-header :deep(.van-nav-bar) {
  background: linear-gradient(135deg, #6c8c5e, #8fbc8f);
}
.page-header :deep(.van-nav-bar__title) {
  color: #fff;
}
.page-header :deep(.van-nav-bar__arrow) {
  color: #fff;
}

.moment-list { padding: 12px; }

.moment-card {
  background: #fff; border-radius: 12px; padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
}

.moment-content { font-size: 15px; line-height: 1.6; color: #333; margin-bottom: 10px; }

.moment-images {
  display: grid; grid-template-columns: repeat(3, 1fr); gap: 4px;
  margin-bottom: 10px; border-radius: 8px; overflow: hidden;
}
.moment-images .van-image { height: 100px; }

.moment-footer {
  display: flex; align-items: center; justify-content: space-between;
  padding-top: 10px; border-top: 1px solid #f5f5f5;
}
.moment-time { font-size: 12px; color: #bbb; }
.moment-actions { display: flex; align-items: center; gap: 16px; }
.action-btn { font-size: 13px; color: #888; cursor: pointer; user-select: none; }
.action-btn.liked { color: #ee0a24; }
.delete-btn { font-size: 16px; cursor: pointer; }

.comment-preview { margin-top: 10px; padding-top: 10px; border-top: 1px solid #f0f0f0; }
.comment-item { font-size: 13px; padding: 4px 0; color: #555; }
.comment-item.is-reply { margin-left: 20px; }
.comment-user { font-weight: 600; color: var(--app-primary); }
.reply-tag { color: var(--app-primary); font-size: 12px; }
.comment-text { color: #555; }
.comment-input { display: flex; align-items: center; gap: 6px; margin-top: 6px; }
.comment-input .van-field { flex: 1; }
</style>
