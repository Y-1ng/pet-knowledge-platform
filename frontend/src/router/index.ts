import { createRouter, createWebHistory } from 'vue-router'
import BasicLayout from '../layout/BasicLayout.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: BasicLayout,
      children: [
        {
          path: '',
          name: 'Home',
          component: () => import('../views/Home.vue'),
          meta: { title: '首页' }
        },
        // 用户模块
        {
          path: 'login',
          name: 'Login',
          component: () => import('../views/user/Login.vue'),
          meta: { title: '登录' }
        },
        {
          path: 'register',
          name: 'Register',
          component: () => import('../views/user/Register.vue'),
          meta: { title: '注册' }
        },
        {
          path: 'profile',
          name: 'Profile',
          component: () => import('../views/user/Profile.vue'),
          meta: { title: '个人中心' }
        },
        {
          path: 'password',
          name: 'Password',
          component: () => import('../views/user/Password.vue'),
          meta: { title: '修改密码' }
        },
        // AI 问答
        {
          path: 'ai-chat',
          name: 'AiChat',
          component: () => import('../views/ai/Chat.vue'),
          meta: { title: 'AI宠物顾问' }
        },
        // 知识百科
        {
          path: 'knowledge',
          name: 'KnowledgeList',
          component: () => import('../views/knowledge/List.vue'),
          meta: { title: '宠物百科' }
        },
        {
          path: 'knowledge/:id',
          name: 'KnowledgeDetail',
          component: () => import('../views/knowledge/Detail.vue'),
          meta: { title: '文章详情' }
        },
        // 动态
        {
          path: 'moments',
          name: 'MomentList',
          component: () => import('../views/moment/MomentList.vue'),
          meta: { title: '动态' }
        },
        {
          path: 'my-moments',
          name: 'MyMoments',
          component: () => import('../views/moment/MyMoments.vue'),
          meta: { title: '我的动态' }
        },
        // 养宠指南
        {
          path: 'guide',
          name: 'GuideList',
          component: () => import('../views/guide/List.vue'),
          meta: { title: '养宠指南' }
        },
        {
          path: 'guide/:id',
          name: 'GuideDetail',
          component: () => import('../views/guide/Detail.vue'),
          meta: { title: '指南详情' }
        },
        // 品种百科
        {
          path: 'breed/:categoryId',
          name: 'BreedList',
          component: () => import('../views/breed/BreedList.vue'),
          meta: { title: '品种大全' }
        },
        {
          path: 'breed/detail/:id',
          name: 'BreedDetail',
          component: () => import('../views/breed/BreedDetail.vue'),
          meta: { title: '品种详情' }
        },
        // 社区
        {
          path: 'community',
          name: 'Community',
          component: () => import('../views/community/PostList.vue'),
          meta: { title: '宠物社区' }
        },
        {
          path: 'community/post/:id',
          name: 'PostDetail',
          component: () => import('../views/community/PostDetail.vue'),
          meta: { title: '帖子详情' }
        },
        {
          path: 'community/create',
          name: 'CreatePost',
          component: () => import('../views/community/CreatePost.vue'),
          meta: { title: '发布帖子' }
        },
        // 宠物档案
        {
          path: 'pet/list',
          name: 'PetProfile',
          component: () => import('../views/pet/ProfileList.vue'),
          meta: { title: '我的宠物' }
        },
        {
          path: 'pet/edit',
          name: 'PetEdit',
          component: () => import('../views/pet/ProfileEdit.vue'),
          meta: { title: '宠物档案' }
        },
        {
          path: 'pet/health/:petId',
          name: 'PetHealth',
          component: () => import('../views/pet/HealthRecord.vue'),
          meta: { title: '健康记录' }
        },
        // 问答库
        {
          path: 'qa',
          name: 'QaList',
          component: () => import('../views/qa/QuestionList.vue'),
          meta: { title: '知识问答' }
        },
        {
          path: 'qa/question/:id',
          name: 'QaDetail',
          component: () => import('../views/qa/QuestionDetail.vue'),
          meta: { title: '问题详情' }
        },
        {
          path: 'qa/ask',
          name: 'AskQuestion',
          component: () => import('../views/qa/AskQuestion.vue'),
          meta: { title: '我要提问' }
        },
        {
          path: 'user-comments/:userId',
          name: 'UserComments',
          component: () => import('../views/user/UserComments.vue'),
          meta: { title: '用户评论' }
        }
      ]
    }
  ]
})

export default router
