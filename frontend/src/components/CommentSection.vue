<template>
  <div class="comment-section">
    <!-- 评论输入 -->
    <div class="comment-input">
      <div class="input-header">
        <span class="input-title">发表评论</span>
      </div>
      <van-field
        v-model="commentContent"
        rows="3"
        autosize
        type="textarea"
        maxlength="500"
        :placeholder="userStore.isLoggedIn ? '写下你的评论...' : '请先登录后发表评论'"
        :disabled="!userStore.isLoggedIn"
        show-word-limit
      />
      <div class="input-actions">
        <van-button
          type="primary"
          size="small"
          color="#6c8c5e"
          :disabled="!commentContent.trim() || !userStore.isLoggedIn"
          :loading="submitting"
          @click="submitComment"
        >
          发表
        </van-button>
      </div>
      <div v-if="!userStore.isLoggedIn" class="login-tip" @click="$router.push('/login')">
        登录后即可评论
      </div>
    </div>

    <!-- 评论列表 -->
    <div v-if="comments.length > 0" class="comment-list">
      <div
        v-for="comment in comments"
        :key="comment.id"
        class="comment-item"
      >
        <!-- 头像 -->
        <div class="avatar-wrap">
          <img
            v-if="comment.userAvatar"
            :src="comment.userAvatar"
            class="avatar"
          />
          <div v-else class="avatar-placeholder">
            {{ getInitial(comment.username) }}
          </div>
        </div>

        <!-- 评论主体 -->
        <div class="comment-body">
          <!-- 头部 -->
          <div class="comment-header">
            <span class="username" @click.stop="goUserComments(comment.userId)">{{ comment.username || `用户${comment.userId}` }}</span>
            <span class="time">{{ comment.createTime }}</span>
          </div>

          <!-- 内容（可展开收起） -->
          <div class="comment-text" :class="{ collapsed: !expandedComments[comment.id] }">
            {{ comment.content }}
          </div>
          <div
            v-if="comment.content && comment.content.length > 100"
            class="expand-btn"
            @click="toggleComment(comment.id)"
          >
            {{ expandedComments[comment.id] ? '收起' : '展开' }}
          </div>

          <!-- 操作 -->
          <div class="comment-actions">
            <span
              class="action-btn"
              @click="showReplyInput(comment)"
            >
              回复
            </span>
            <span
              v-if="isMyComment(comment.userId)"
              class="action-btn delete-btn"
              @click="deleteCommentHandler(comment.id)"
            >
              删除
            </span>
          </div>

          <!-- 回复输入框（内联） -->
          <div v-if="replyingTo?.id === comment.id" class="reply-input-wrap">
            <van-field
              v-model="replyContent"
              rows="2"
              autosize
              type="textarea"
              maxlength="500"
              :placeholder="`回复 ${comment.username}...`"
            />
            <div class="reply-actions">
              <van-button
                type="primary"
                size="small"
                color="#6c8c5e"
                :disabled="!replyContent.trim()"
                :loading="replying"
                @click="submitReply(comment)"
              >
                回复
              </van-button>
              <van-button size="small" plain @click="cancelReply">取消</van-button>
            </div>
          </div>

          <!-- 二级回复区域 -->
          <div v-if="comment.id" class="replies-section">
            <!-- 未展开：显示加载入口 -->
            <div
              v-if="!replyLoaded[comment.id]"
              class="load-replies"
              @click="loadReplies(comment)"
            >
              查看回复
            </div>

            <!-- 已展开：显示回复列表 + 回复分页 -->
            <div v-else>
              <div class="replies-list">
                <div
                  v-for="reply in currentReplies(comment.id)"
                  :key="reply.id"
                  class="reply-item"
                >
                  <div class="reply-avatar-wrap">
                    <img
                      v-if="reply.userAvatar"
                      :src="reply.userAvatar"
                      class="reply-avatar"
                    />
                    <div v-else class="reply-avatar-placeholder">
                      {{ getInitial(reply.username) }}
                    </div>
                  </div>
                  <div class="reply-content">
                    <div class="reply-header">
                      <span class="reply-username" @click.stop="goUserComments(reply.userId)">{{ reply.username || `用户${reply.userId}` }}</span>
                      <span
                        v-if="reply.replyToUsername"
                        class="reply-to"
                      >回复 @{{ reply.replyToUsername }}</span>
                      <span class="reply-time">{{ reply.createTime }}</span>
                    </div>
                    <div class="reply-text">{{ reply.content }}</div>
                    <div class="reply-item-actions">
                      <span
                        class="action-btn"
                        @click="showReplyToReply(comment, reply)"
                      >
                        回复
                      </span>
                      <span
                        v-if="isMyComment(reply.userId)"
                        class="action-btn delete-btn"
                        @click="deleteCommentHandler(reply.id)"
                      >
                        删除
                      </span>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 回复分页 -->
              <div
                v-if="replyTotal[comment.id] > defaultPageSize"
                class="reply-pagination"
              >
                <van-pagination
                  v-model="replyPages[comment.id]"
                  :total-items="replyTotal[comment.id]"
                  :items-per-page="defaultPageSize"
                  :show-page-size="3"
                  @change="loadReplies(comment)"
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <van-empty v-else-if="!loading" description="暂无评论，来说两句吧" />
    <van-loading v-if="loading" class="loading" size="24px">加载中...</van-loading>

    <!-- 评论分页 -->
    <div v-if="total > defaultPageSize" class="pagination-bar">
      <van-pagination
        v-model="currentPage"
        :total-items="total"
        :items-per-page="defaultPageSize"
        :show-page-size="5"
        @change="loadComments"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { showToast, showConfirmDialog } from 'vant'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  getCommentList as defaultGetList,
  getReplyList as defaultGetReplies,
  addComment as defaultAdd,
  deleteComment as defaultDelete
} from '@/api/comment'

export interface CommentApi {
  getList: (params: { targetId: number; page: number; size: number }) => Promise<any>
  getReplies: (targetId: number, parentId: number) => Promise<any>
  add: (data: {
    targetId?: number
    userId: number
    content: string
    parentId?: number | null
    replyToUserId?: number | null
    replyToUsername?: string | null
  }) => Promise<any>
  delete: (id: number, userId: number) => Promise<any>
}

/** 默认的文章评论 API 适配器 */
const articleApi: CommentApi = {
  getList: (params) => defaultGetList({ articleId: params.targetId, page: params.page, size: params.size }),
  getReplies: (targetId, parentId) => defaultGetReplies(targetId, parentId),
  add: (data) => defaultAdd({ articleId: data.targetId!, userId: data.userId, content: data.content, parentId: data.parentId, replyToUserId: data.replyToUserId, replyToUsername: data.replyToUsername }),
  delete: (id, userId) => defaultDelete(id, userId)
}

const props = defineProps<{
  targetId: number
  pageSize?: number
  api?: CommentApi
}>()

const emit = defineEmits<{
  'update:count': [total: number]
}>()

// 使用自定义 API 或默认的文章评论 API
const commentApi = computed<CommentApi>(() => props.api || articleApi)

const router = useRouter()
const userStore = useUserStore()

// 状态
const comments = ref<any[]>([])
const loading = ref(false)
const submitting = ref(false)
const replying = ref(false)
const currentPage = ref(1)
const total = ref(0)
const commentContent = ref('')
const replyContent = ref('')
const replyingTo = ref<any>(null)

// 回复相关
const replyLoaded = ref<Record<number, boolean>>({})       // 是否已加载回复
const replyPageData = ref<Record<number, any[]>>({})       // 回复数据(全部)
const replyPages = ref<Record<number, number>>({})         // 回复当前分页
const replyTotal = ref<Record<number, number>>({})          // 回复总数

// 展开/收起
const expandedComments = ref<Record<number, boolean>>({})

const defaultPageSize = computed(() => props.pageSize || 5)

onMounted(() => {
  loadComments()
})

/** 获取当前页的回复（内存分页） */
function currentReplies(commentId: number) {
  const all = replyPageData.value[commentId] || []
  const page = replyPages.value[commentId] || 1
  const size = defaultPageSize.value
  return all.slice(0, page * size)
}

/** 回复总数是否超过分页数 */
function replyHasMorePages(commentId: number) {
  const total = replyTotal.value[commentId] || 0
  return total > defaultPageSize.value
}

/** 加载一级评论 */
async function loadComments() {
  loading.value = true
  try {
    const res = await commentApi.value.getList({
      targetId: props.targetId,
      page: currentPage.value,
      size: defaultPageSize.value
    })
    if (res.code === 200) {
      comments.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载评论失败', error)
  } finally {
    loading.value = false
    emit('update:count', total.value)
  }
}

/** 展开/收起长评论 */
function toggleComment(commentId: number) {
  expandedComments.value[commentId] = !expandedComments.value[commentId]
}

/** 加载二级回复（累加模式） */
async function loadReplies(comment: any) {
  const page = replyPages.value[comment.id] || 1
  try {
    const res = await commentApi.value.getReplies(props.targetId, comment.id)
    if (res.code === 200) {
      const data = res.data || []
      replyPageData.value[comment.id] = data
      replyTotal.value[comment.id] = data.length
      replyLoaded.value[comment.id] = true
      // 如果当前页已有数据但未设分页号，保持 1
      if (!replyPages.value[comment.id]) {
        replyPages.value[comment.id] = 1
      }
    }
  } catch (error) {
    console.error('加载回复失败', error)
  }
}

/** 发表一级评论 */
async function submitComment() {
  if (!userStore.isLoggedIn) {
    showToast('请先登录')
    return
  }
  if (!commentContent.value.trim()) {
    showToast('请输入评论内容')
    return
  }
  submitting.value = true
  try {
    const res = await commentApi.value.add({
      targetId: props.targetId,
      userId: userStore.userInfo!.id,
      content: commentContent.value.trim()
    })
    if (res.code === 200) {
      showToast('评论成功')
      commentContent.value = ''
      currentPage.value = 1
      await loadComments()
    } else {
      showToast(res.message || '评论失败')
    }
  } catch (error) {
    showToast('评论失败')
  } finally {
    submitting.value = false
  }
}

/** 显示回复输入框（一级评论） */
function showReplyInput(comment: any) {
  if (!userStore.isLoggedIn) {
    showToast('请先登录')
    return
  }
  replyingTo.value = comment
  replyContent.value = ''
}

/** 显示回复输入框（对二级回复进行回复） */
function showReplyToReply(comment: any, reply: any) {
  if (!userStore.isLoggedIn) {
    showToast('请先登录')
    return
  }
  replyingTo.value = { ...comment, replyToId: reply.userId, replyToUsername: reply.username }
  replyContent.value = ''
}

/** 提交回复 */
async function submitReply(comment: any) {
  if (!replyContent.value.trim()) {
    showToast('请输入回复内容')
    return
  }
  replying.value = true
  try {
    const res = await commentApi.value.add({
      targetId: props.targetId,
      userId: userStore.userInfo!.id,
      content: replyContent.value.trim(),
      parentId: comment.id,
      replyToUserId: replyingTo.value.replyToId || comment.userId,
      replyToUsername: replyingTo.value.replyToUsername || comment.username
    })
    if (res.code === 200) {
      showToast('回复成功')
      replyContent.value = ''
      replyingTo.value = null
      // 重新加载回复
      await loadReplies(comment)
    } else {
      showToast(res.message || '回复失败')
    }
  } catch (error) {
    showToast('回复失败')
  } finally {
    replying.value = false
  }
}

/** 取消回复 */
function cancelReply() {
  replyingTo.value = null
  replyContent.value = ''
}

/** 删除评论 */
function deleteCommentHandler(id: number) {
  showConfirmDialog({ message: '确定删除该评论吗？' }).then(async () => {
    try {
      const res = await commentApi.value.delete(id, userStore.userInfo!.id)
      if (res.code === 200) {
        showToast('删除成功')
        currentPage.value = 1
        await loadComments()
      } else {
        showToast(res.message || '删除失败')
      }
    } catch (error) {
      showToast('删除失败')
    }
  }).catch(() => {})
}

/** 判断是否为当前用户的评论 */
function isMyComment(userId: number) {
  return userStore.isLoggedIn && userStore.userInfo?.id === userId
}

/** 获取首字符 */
function getInitial(name: string) {
  return (name || '?').charAt(0).toUpperCase()
}

/** 跳转到该用户的评论历史 */
function goUserComments(userId: number) {
  if (userId) {
    router.push(`/user-comments/${userId}`)
  }
}
</script>

<style scoped>
.comment-section {
  padding: 16px 0;
}

/* 输入区域 */
.comment-input {
  background: #fff;
  padding: 16px;
  border-radius: 10px;
  margin-bottom: 16px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
}
.input-header {
  margin-bottom: 10px;
}
.input-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}
.input-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 8px;
}
.login-tip {
  text-align: center;
  padding: 8px 0 0;
  font-size: 13px;
  color: #6c8c5e;
  cursor: pointer;
}

/* 评论列表 */
.comment-list {
  background: #fff;
  border-radius: 10px;
  padding: 8px 16px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid #f0f0f0;
}
.comment-item:last-child {
  border-bottom: none;
}

/* 头像 */
.avatar-wrap {
  flex-shrink: 0;
}
.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}
.avatar-placeholder {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #6c8c5e, #8fb07e);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 600;
}

/* 评论主体 */
.comment-body {
  flex: 1;
  min-width: 0;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}
.username {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  cursor: pointer;
}
.username:hover {
  color: #6c8c5e;
}
.time {
  font-size: 12px;
  color: #bbb;
}

.comment-text {
  font-size: 14px;
  color: #555;
  line-height: 1.6;
  word-break: break-word;
}
.comment-text.collapsed {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.expand-btn {
  font-size: 12px;
  color: #6c8c5e;
  cursor: pointer;
  margin-top: 4px;
}

.comment-actions {
  display: flex;
  gap: 16px;
  margin-top: 6px;
}
.action-btn {
  font-size: 12px;
  color: #999;
  cursor: pointer;
  transition: color 0.2s;
}
.action-btn:hover {
  color: #6c8c5e;
}
.delete-btn {
  color: #ccc;
}
.delete-btn:hover {
  color: #ee0a24;
}

/* 回复输入框 */
.reply-input-wrap {
  margin-top: 10px;
  padding: 12px;
  background: #f7f8fa;
  border-radius: 8px;
}
.reply-actions {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}

/* 二级回复区域 */
.replies-section {
  margin-top: 10px;
  padding-left: 0;
}

.load-replies {
  font-size: 13px;
  color: #6c8c5e;
  cursor: pointer;
  padding: 6px 0;
  display: inline-block;
}
.load-replies:hover {
  text-decoration: underline;
}

.replies-list {
  padding: 8px 0 4px;
}

.reply-item {
  display: flex;
  gap: 10px;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
}
.reply-item:last-child {
  border-bottom: none;
}

.reply-avatar-wrap {
  flex-shrink: 0;
}
.reply-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}
.reply-avatar-placeholder {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #8fb07e, #a8c99a);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
}

.reply-content {
  flex: 1;
  min-width: 0;
}

.reply-header {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 4px 8px;
  margin-bottom: 4px;
}
.reply-username {
  font-size: 13px;
  font-weight: 600;
  color: #444;
  cursor: pointer;
}
.reply-username:hover {
  color: #6c8c5e;
}
.reply-to {
  font-size: 12px;
  color: #999;
}
.reply-time {
  font-size: 11px;
  color: #bbb;
}

.reply-text {
  font-size: 13px;
  color: #555;
  line-height: 1.5;
  word-break: break-word;
}

.reply-item-actions {
  display: flex;
  gap: 12px;
  margin-top: 4px;
}

/* 分页 */
.pagination-bar {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}
.reply-pagination {
  margin-top: 8px;
  display: flex;
  justify-content: center;
}

.loading {
  display: flex;
  justify-content: center;
  padding: 40px 0;
}
</style>
