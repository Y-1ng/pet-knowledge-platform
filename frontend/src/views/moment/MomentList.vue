<template>
  <div class="moment-page">
    <div class="moment-list">
      <div v-for="m in moments" :key="m.id" class="moment-card">
        <!-- 用户信息 -->
        <div class="moment-user">
          <div class="user-avatar">{{ getUsername(m).charAt(0) }}</div>
          <div class="user-info">
            <span class="user-name">{{ getUsername(m) }}</span>
            <span class="moment-time">{{ formatTime(m.createTime) }}</span>
          </div>
          <van-icon v-if="isMyMoment(m)" name="delete-o" color="#ccc" class="delete-btn" @click="deleteMoment(m.id)" />
        </div>

        <!-- 内容 -->
        <div class="moment-content">{{ m.content }}</div>

        <!-- 图片 -->
        <div v-if="m.images" class="moment-images">
          <van-image v-for="(img, idx) in parseImages(m.images)" :key="idx" :src="img" fit="cover" @click="previewImg(idx, m.images)" />
        </div>

        <!-- 操作栏 -->
        <div class="moment-actions">
          <span :class="['action-btn', { liked: m.isLiked }]" @click="toggleLike(m)">
            {{ m.isLiked ? '❤️' : '🤍' }} {{ m.likeCount || 0 }}
          </span>
          <span class="action-btn" @click="showComments(m)">
            💬 {{ m.commentCount || 0 }}
          </span>
        </div>

        <!-- 评论预览 -->
        <div class="comment-preview" v-if="m._showComments">
          <div class="comment-list">
            <!-- 一级评论 -->
            <template v-if="momentPages[m.id]">
              <div v-for="primary in momentPages[m.id].records" :key="primary.id" class="comment-primary">
                <div :class="['comment-item']">
                  <span class="comment-user">{{ getUsername(primary) }}</span>
                  <span class="comment-text">{{ primary.content }}</span>
                  <span class="comment-time">{{ formatTime(primary.createTime) }}</span>
                  <span v-if="canDelete(primary)" class="comment-del" @click.stop="deleteComment(m, primary.id)">删除</span>
                </div>
                <!-- 二级回复 -->
                <div v-if="primary.replies && primary.replies.length" class="replies-wrap">
                  <div v-for="reply in primary.replies" :key="reply.id" class="comment-item is-reply">
                    <span class="comment-user">{{ getUsername(reply) }}</span>
                    <span v-if="reply.replyToUsername" class="reply-tag">回复 @{{ reply.replyToUsername }}：</span>
                    <span class="comment-text">{{ reply.content }}</span>
                    <span class="comment-time">{{ formatTime(reply.createTime) }}</span>
                    <span v-if="canDelete(reply)" class="comment-del" @click.stop="deleteComment(m, reply.id)">删除</span>
                  </div>
                </div>
              </div>
              <!-- 分页 -->
              <div v-if="momentPages[m.id].pages > 1" class="comment-pages">
                <span :class="{ disabled: momentPages[m.id].current <= 1 }" @click.stop="loadMomentComments(m, momentPages[m.id].current - 1)">上一页</span>
                <span class="page-num">{{ momentPages[m.id].current }}/{{ momentPages[m.id].pages }}</span>
                <span :class="{ disabled: momentPages[m.id].current >= momentPages[m.id].pages }" @click.stop="loadMomentComments(m, momentPages[m.id].current + 1)">下一页</span>
              </div>
            </template>
            <van-empty v-if="!momentPages[m.id]?.records?.length" description="暂无评论" />
          </div>
          <div class="comment-input" @click.stop>
            <van-field :model-value="commentTexts[m.id] || ''" @update:model-value="commentTexts[m.id] = $event" :placeholder="replyTargets[m.id] ? '输入回复...' : '写评论...'" size="small" @keypress.enter="submitComment(m)" />
            <van-button size="mini" color="#6c8c5e" @click="submitComment(m)">发送</van-button>
          </div>
        </div>
      </div>

      <van-empty v-if="moments.length === 0" description="还没有动态" />
    </div>

    <!-- 发布按钮 -->
    <van-floating-bubble icon="chat-o" color="#6c8c5e" @click="showCreate = true" />

    <!-- 发布弹窗 -->
    <van-dialog v-model:show="showCreate" title="发动态" show-cancel-button @confirm="onCreate" confirm-button-text="发布">
      <van-form>
        <van-field v-model="newContent" type="textarea" placeholder="分享你的宠物故事..." :autosize="{ minHeight: 80 }" rows="4" />
      </van-form>
    </van-dialog>

    <!-- 图片预览 -->
    <van-image-preview v-model:show="showPreview" :images="previewImages" :start-position="previewIndex" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { showToast, showConfirmDialog } from 'vant';
import request from '@/utils/request';
import { useUserStore } from '@/stores/user';

const userStore = useUserStore();
const moments = ref<any[]>([]);
const showCreate = ref(false);
const newContent = ref('');
const momentPages = reactive<Record<number, any>>({});
const commentTexts = reactive<Record<number, string>>({});
const replyTargets = reactive<Record<number, any>>({});
const showPreview = ref(false);
const previewImages = ref<string[]>([]);
const previewIndex = ref(0);

onMounted(async () => {
  await loadMoments();
});

async function loadMoments() {
  try {
    const res = await request.get('/moment/list');
    moments.value = (res.data || []).map((m: any) => {
      m._showComments = false;
      if (userStore.isLoggedIn) {
        request.get('/moment/like/check', { params: { momentId: m.id, userId: userStore.userInfo?.id } })
          .then(r => { m.isLiked = r.data; }).catch(() => {});
      }
      return m;
    });
  } catch (e) {}
}

function getUsername(u: any) { return u.username || `用户${u.userId}`; }
function formatTime(t: string) { return t ? t.substring(0, 16) : ''; }
function isMyMoment(m: any) { return userStore.isLoggedIn && m.userId === userStore.userInfo?.id; }
function canDelete(c: any) { return userStore.isLoggedIn && userStore.userInfo?.id === c.userId; }

function parseImages(images: string) {
  try { return JSON.parse(images); } catch { return [images]; }
}

async function onCreate() {
  if (!userStore.isLoggedIn) { showToast('请先登录'); return; }
  if (!newContent.value.trim()) { showToast('请输入内容'); return; }
  try {
    await request.post('/moment/create', {
      userId: userStore.userInfo?.id,
      content: newContent.value
    });
    showToast('发布成功');
    newContent.value = '';
    showCreate.value = false;
    await loadMoments();
  } catch (e) { showToast('发布失败'); }
}

async function deleteMoment(id: number) {
  showConfirmDialog({ title: '提示', message: '确定删除这条动态吗？' }).then(async () => {
    try {
      await request.delete(`/moment/${id}`, { params: { userId: userStore.userInfo?.id } });
      showToast('已删除');
      await loadMoments();
    } catch (e) {}
  }).catch(() => {});
}

async function toggleLike(m: any) {
  if (!userStore.isLoggedIn) { showToast('请先登录'); return; }
  try {
    const res = await request.post('/moment/like', { momentId: m.id, userId: userStore.userInfo?.id });
    m.isLiked = res.data;
    m.likeCount = (m.likeCount || 0) + (res.data ? 1 : -1);
  } catch (e) {}
}

async function loadMomentComments(m: any, page: number = 1) {
  try {
    const res = await request.get(`/moment/comments/${m.id}`, { params: { page, size: 5 } });
    momentPages[m.id] = res.data || { records: [], total: 0, current: 1, size: 5, pages: 0 };
  } catch (e) {}
}

async function showComments(m: any) {
  m._showComments = !m._showComments;
  if (m._showComments && !momentPages[m.id]) {
    await loadMomentComments(m, 1);
  }
}

async function deleteComment(m: any, commentId: number) {
  showConfirmDialog({ title: '提示', message: '确定删除此评论吗？' }).then(async () => {
    try {
      console.log('Deleting comment:', commentId, 'userId:', userStore.userInfo?.id);
      await request.delete(`/moment/comment/${commentId}`, {
        params: { userId: userStore.userInfo?.id }
      });
      showToast('已删除');
      m.commentCount = Math.max(0, (m.commentCount || 0) - 1);
      await loadMomentComments(m, momentPages[m.id]?.current || 1);
    } catch (e) {
      console.error('Delete failed:', e);
      showToast('删除失败: ' + (e as any)?.message);
    }
  }).catch(() => {});
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
    m.commentCount = (m.commentCount || 0) + 1;
    await loadMomentComments(m, reply ? (momentPages[m.id]?.current || 1) : 1);
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
.moment-page { background: #f7f8fa; min-height: 100%; }
.moment-list { padding: 12px; }

.moment-card {
  background: #fff; border-radius: 12px; padding: 16px;
  margin-bottom: 12px;
}
.moment-user { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; }
.user-avatar {
  width: 36px; height: 36px; border-radius: 50%;
  background: var(--app-primary); color: #fff;
  display: flex; align-items: center; justify-content: center;
  font-size: 15px; flex-shrink: 0;
}
.user-info { flex: 1; }
.user-name { font-size: 14px; font-weight: 600; color: #333; display: block; }
.moment-time { font-size: 11px; color: #bbb; }
.delete-btn { font-size: 16px; }

.moment-content { font-size: 15px; line-height: 1.6; color: #333; margin-bottom: 10px; }

.moment-images { display: grid; grid-template-columns: repeat(3, 1fr); gap: 4px; margin-bottom: 10px; border-radius: 8px; overflow: hidden; }
.moment-images .van-image { height: 100px; }

.moment-actions { display: flex; gap: 24px; padding-top: 10px; border-top: 1px solid #f5f5f5; }
.action-btn { font-size: 13px; color: #888; cursor: pointer; user-select: none; }
.action-btn.liked { color: #ee0a24; }

.comment-preview { margin-top: 10px; padding-top: 10px; border-top: 1px solid #f0f0f0; }
.comment-item { font-size: 13px; padding: 6px 0; color: #555; display: flex; flex-wrap: wrap; gap: 4px; align-items: baseline; }
.comment-item.is-reply { margin-left: 24px; border-left: 2px solid #e8f5e9; padding-left: 10px; }
.comment-user { font-weight: 600; color: var(--app-primary); flex-shrink: 0; }
.reply-tag { color: var(--app-primary); font-size: 12px; }
.comment-text { color: #555; }
.comment-time { font-size: 11px; color: #bbb; margin-left: auto; }
.comment-del { font-size: 11px; color: #ccc; cursor: pointer; margin-left: 4px; }
.comment-del:hover { color: #ee0a24; }

/* 二级回复 */
.replies-wrap { margin-left: 8px; }

/* 分页 */
.comment-pages { display: flex; justify-content: center; align-items: center; gap: 12px; font-size: 12px; padding: 8px 0; }
.comment-pages span { color: var(--app-primary); cursor: pointer; }
.comment-pages span.disabled { color: #ccc; cursor: default; }
.page-num { color: #999 !important; cursor: default !important; }

.comment-input { display: flex; align-items: center; gap: 6px; margin-top: 6px; }
.comment-input .van-field { flex: 1; }
</style>
