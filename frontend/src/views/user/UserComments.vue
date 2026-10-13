<template>
  <div class="user-comments">
    <van-nav-bar
      :title="isMyPage ? '我的评论' : `${userInfo?.nickname || userInfo?.username || '用户'}的评论`"
      left-arrow
      @click-left="router.back()"
    />

    <!-- 用户摘要 -->
    <div v-if="userInfo" class="user-summary">
      <div class="summary-avatar">
        <img v-if="userInfo.avatar" :src="userInfo.avatar" class="avatar-img" />
        <div v-else class="avatar-placeholder">{{ (userInfo.nickname || userInfo.username || '?').charAt(0) }}</div>
      </div>
      <div class="summary-info">
        <span class="summary-name">{{ userInfo.nickname || userInfo.username }}</span>
        <span class="summary-count">共 {{ total }} 条评论</span>
      </div>
    </div>

    <!-- 评论列表 -->
    <div class="comments-list">
      <!-- 骨架屏加载 -->
      <template v-if="loading">
        <div v-for="i in 3" :key="i" class="skeleton-item">
          <van-skeleton title :row="2" />
        </div>
      </template>

      <!-- 空状态 -->
      <van-empty v-else-if="!loading && comments.length === 0" description="暂无评论" />

      <!-- 评论列表 -->
      <div v-else class="comments-wrap">
        <div
          v-for="comment in comments"
          :key="comment.id"
          class="comment-card"
          @click="goArticle(comment.articleId)"
        >
          <div class="card-header">
            <span class="article-title">📄 {{ comment.articleTitle || `文章 #${comment.articleId}` }}</span>
            <span class="card-time">{{ comment.createTime }}</span>
          </div>
          <div class="card-content">{{ comment.content }}</div>
          <div class="card-footer">
            <span v-if="comment.parentId" class="tag tag-reply">回复</span>
            <span v-else class="tag tag-primary">评论</span>
            <span
              v-if="isMyPage"
              class="delete-btn"
              @click.stop="deleteHandler(comment.id)"
            >删除</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="total > size" class="pagination-bar">
      <van-pagination
        v-model="currentPage"
        :total-items="total"
        :items-per-page="size"
        :show-page-size="5"
        @change="loadData"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import { useUserStore } from '@/stores/user'
import { getCommentsByUserId, deleteComment } from '@/api/comment'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const userId = ref(0)
const userInfo = ref<any>(null)
const comments = ref<any[]>([])
const loading = ref(true)
const currentPage = ref(1)
const total = ref(0)
const size = 10

const isMyPage = computed(() => userStore.isLoggedIn && userStore.userInfo?.id === userId.value)

onMounted(async () => {
  userId.value = Number(route.params.userId)
  if (!userId.value) {
    showToast('参数错误')
    router.back()
    return
  }
  // 查询用户信息
  try {
    const res = await request.get(`/user/${userId.value}`)
    if (res.code === 200) {
      userInfo.value = res.data
    }
  } catch (e) {
    // 非关键错误
  }
  await loadData()
})

async function loadData() {
  loading.value = true
  try {
    const res = await getCommentsByUserId(userId.value, currentPage.value, size)
    if (res.code === 200) {
      comments.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (e) {
    console.error('加载评论失败', e)
  } finally {
    loading.value = false
  }
}

function goArticle(articleId: number) {
  router.push(`/knowledge/${articleId}`)
}

function deleteHandler(commentId: number) {
  showConfirmDialog({ message: '确定删除此评论吗？' }).then(async () => {
    try {
      const res = await deleteComment(commentId, userStore.userInfo!.id)
      if (res.code === 200) {
        showToast('删除成功')
        await loadData()
      } else {
        showToast(res.message || '删除失败')
      }
    } catch (e) {
      showToast('删除失败')
    }
  }).catch(() => {})
}
</script>

<style scoped>
.user-comments {
  background: #f7f8fa;
  min-height: 100vh;
}

.user-summary {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 20px 16px;
  background: linear-gradient(135deg, #6c8c5e, #8fbc8f);
  color: #fff;
}
.summary-avatar {
  flex-shrink: 0;
}
.avatar-img {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(255,255,255,0.5);
}
.avatar-placeholder {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: rgba(255,255,255,0.25);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 700;
  border: 2px solid rgba(255,255,255,0.5);
}
.summary-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.summary-name {
  font-size: 18px;
  font-weight: 600;
}
.summary-count {
  font-size: 13px;
  opacity: 0.85;
}

.comments-list {
  padding: 12px 16px;
}

.skeleton-item {
  background: #fff;
  border-radius: 10px;
  padding: 16px;
  margin-bottom: 12px;
}

.comment-card {
  background: #fff;
  border-radius: 10px;
  padding: 14px 16px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: transform 0.15s;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
}
.comment-card:active {
  transform: scale(0.98);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.article-title {
  font-size: 13px;
  color: #6c8c5e;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
  margin-right: 8px;
}
.card-time {
  font-size: 11px;
  color: #bbb;
  flex-shrink: 0;
}

.card-content {
  font-size: 14px;
  color: #444;
  line-height: 1.6;
  margin-bottom: 10px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.tag {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 500;
}
.tag-primary {
  background: #e8f5e9;
  color: #6c8c5e;
}
.tag-reply {
  background: #fff3e0;
  color: #e65100;
}
.delete-btn {
  font-size: 12px;
  color: #ccc;
  cursor: pointer;
}
.delete-btn:hover {
  color: #ee0a24;
}

.pagination-bar {
  padding: 8px 16px 24px;
}
</style>
