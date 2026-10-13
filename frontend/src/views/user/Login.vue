<template>
  <div class="login-page">
    <div class="login-header">
      <span class="login-icon">🐾</span>
      <h2>欢迎回来</h2>
      <p>登录宠知，探索宠物世界</p>
    </div>
    <van-form @submit="onLogin">
      <van-cell-group inset>
        <van-field
          v-model="username"
          name="username"
          label="用户名"
          placeholder="请输入用户名"
          :rules="[{ required: true, message: '请输入用户名' }]"
          clearable
        />
        <van-field
          v-model="password"
          type="password"
          name="password"
          label="密码"
          placeholder="请输入密码"
          :rules="[{ required: true, message: '请输入密码' }]"
          clearable
        />
      </van-cell-group>
      <div style="margin: 16px">
        <van-button
          round block type="primary" native-type="submit"
          color="#6c8c5e" :loading="loading" loading-text="登录中..."
        >
          登录
        </van-button>
      </div>
    </van-form>
    <div class="login-links">
      <span @click="router.push('/register')">没有账号？去注册</span>
    </div>
    <div v-if="backendOnline === false" class="offline-notice">
      ⚠️ 后端服务未连接，请确认后端已启动
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { showToast } from 'vant';
import request from '@/utils/request';
import { useUserStore } from '@/stores/user';

const router = useRouter();
const userStore = useUserStore();
const username = ref('');
const password = ref('');
const loading = ref(false);
const backendOnline = ref<boolean | null>(null);

async function onLogin() {
  loading.value = true;
  try {
    const res = await request.post('/user/login', {
      username: username.value,
      password: password.value
    });
    backendOnline.value = true;
    if (res.code === 200 && res.data) {
      userStore.setUser(res.data);
      showToast('登录成功');
      setTimeout(() => router.push('/'), 500);
    } else {
      showToast(res.message || '登录失败');
    }
  } catch (e: any) {
    backendOnline.value = false;
    // request.ts 已经弹过 toast，这里不再重复弹
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.login-page { padding: 40px 20px; }
.login-header { text-align: center; margin-bottom: 40px; }
.login-icon { font-size: 64px; }
.login-header h2 { margin: 12px 0 6px; font-size: 24px; color: #333; }
.login-header p { margin: 0; font-size: 14px; color: #999; }
.login-links { text-align: center; margin-top: 16px; font-size: 14px; color: #6c8c5e; }
.offline-notice { text-align: center; margin-top: 20px; padding: 10px; background: #fff3e0; border-radius: 8px; font-size: 13px; color: #e65100; }
</style>

<style scoped>
.login-page { padding: 40px 20px; }
.login-header { text-align: center; margin-bottom: 40px; }
.login-icon { font-size: 64px; }
.login-header h2 { margin: 12px 0 6px; font-size: 24px; color: #333; }
.login-header p { margin: 0; font-size: 14px; color: #999; }
.login-links { text-align: center; margin-top: 16px; font-size: 14px; color: #6c8c5e; }
</style>
