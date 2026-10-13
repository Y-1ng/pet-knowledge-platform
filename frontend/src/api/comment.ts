import request from '@/utils/request'

export interface CommentParams {
  articleId: number
  page?: number
  size?: number
  parentId?: number
}

export interface AddCommentBody {
  articleId: number
  userId: number
  content: string
  parentId?: number | null
  replyToUserId?: number | null
  replyToUsername?: string | null
}

/**
 * 分页查询一级评论
 */
export const getCommentList = (params: CommentParams) => {
  return request.get(`/article-comment/${params.articleId}`, {
    params: { page: params.page || 1, size: params.size || 5 }
  })
}

/**
 * 查询指定一级评论下的所有二级回复
 */
export const getReplyList = (articleId: number, parentId: number) => {
  return request.get(`/article-comment/${articleId}/replies/${parentId}`)
}

/**
 * 新增评论（一级或二级）
 */
export const addComment = (data: AddCommentBody) => {
  return request.post('/article-comment', data)
}

/**
 * 删除评论
 */
export const deleteComment = (id: number, userId: number) => {
  return request.delete(`/article-comment/${id}`, { params: { userId } })
}

/**
 * 分页查询指定用户的评论列表
 */
export const getCommentsByUserId = (userId: number, page: number = 1, size: number = 10) => {
  return request.get(`/article-comment/user/${userId}`, {
    params: { page, size }
  })
}
