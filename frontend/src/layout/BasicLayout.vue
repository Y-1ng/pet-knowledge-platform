<template>
  <div class="basic-layout">
    <van-nav-bar :title="title" :fixed="false" left-arrow @click-left="onBack" />
    <div class="layout-body">
      <!-- 桌面端侧边导航 -->
      <div class="side-nav" v-if="isDesktop">
        <div class="side-nav-item" :class="{ active: active === 0 }" @click="navigate(0, '/')">
          <van-icon name="home-o" /> <span>首页</span>
        </div>
        <div class="side-nav-item" :class="{ active: active === 1 }" @click="navigate(1, '/moments')">
          <van-icon name="smile-o" /> <span>动态</span>
        </div>
        <div class="side-nav-item" :class="{ active: active === 2 }" @click="navigate(2, '/knowledge')">
          <van-icon name="search" /> <span>百科</span>
        </div>
        <div class="side-nav-item" :class="{ active: active === 3 }" @click="navigate(3, '/ai-chat')">
          <van-icon name="chat-o" /> <span>AI顾问</span>
        </div>
        <div class="side-nav-item" :class="{ active: active === 4 }" @click="navigate(4, '/community')">
          <van-icon name="friends-o" /> <span>社区</span>
        </div>
        <div class="side-nav-item" :class="{ active: active === 5 }" @click="navigate(5, '/profile')">
          <van-icon name="contact" /> <span>我的</span>
        </div>
      </div>
      <div class="app-content">
        <router-view />
      </div>
    </div>
    <!-- 手机端底部导航 -->
    <van-tabbar v-if="!isDesktop" v-model="active" :fixed="false" active-color="#6c8c5e">
      <van-tabbar-item icon="home-o" to="/">首页</van-tabbar-item>
      <van-tabbar-item icon="smile-o" to="/moments">动态</van-tabbar-item>
      <van-tabbar-item icon="search" to="/knowledge">百科</van-tabbar-item>
      <van-tabbar-item icon="chat-o" to="/ai-chat">AI顾问</van-tabbar-item>
      <van-tabbar-item icon="friends-o" to="/community">社区</van-tabbar-item>
      <van-tabbar-item icon="contact" to="/profile">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();
const active = ref(0);
const isDesktop = ref(window.innerWidth >= 769);

function checkWidth() {
  isDesktop.value = window.innerWidth >= 769;
}

onMounted(() => window.addEventListener('resize', checkWidth));
onUnmounted(() => window.removeEventListener('resize', checkWidth));

const title = computed(() => (route.meta.title as string) || '宠知');

// 根据路由切换导航高亮
watch(() => route.path, (path) => {
  if (path === '/') active.value = 0;
  else if (path.startsWith('/moments')) active.value = 1;
  else if (path.startsWith('/knowledge')) active.value = 2;
  else if (path.startsWith('/ai-chat')) active.value = 3;
  else if (path.startsWith('/community')) active.value = 4;
  else if (path.startsWith('/profile') || path.startsWith('/login') || path.startsWith('/register')) active.value = 5;
});

function onBack() {
  router.back();
}

function navigate(idx: number, path: string) {
  active.value = idx;
  router.push(path);
}
</script>

<style scoped>
.basic-layout {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
  overflow: hidden;
}

/* 桌面端：内容区 + 侧边栏水平布局 */
.layout-body {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.app-content {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  background-color: #f7f8fa;
  position: relative;
}

/* 桌面端侧边导航 */
.side-nav {
  width: 180px;
  background: #fff;
  border-right: 1px solid #ebedf0;
  padding: 16px 0;
  flex-shrink: 0;
}
.side-nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 24px;
  font-size: 15px;
  color: #555;
  cursor: pointer;
  transition: all 0.2s;
  border-left: 3px solid transparent;
}
.side-nav-item:hover {
  background: #f5f8f3;
  color: var(--app-primary);
}
.side-nav-item.active {
  background: #f0f7ee;
  color: var(--app-primary);
  border-left-color: var(--app-primary);
  font-weight: 600;
}

/* 移动端保持原样 */
@media (max-width: 768px) {
  .layout-body {
    flex-direction: column;
  }
  .side-nav {
    display: none;
  }
}
</style>
