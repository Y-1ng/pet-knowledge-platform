<template>
  <div class="register-page">
    <div class="register-header">
      <span class="reg-icon">🐾</span>
      <h2>注册宠知账号</h2>
      <p>推荐使用测试账号: test / 123456</p>
    </div>
    <van-form @submit="onRegister">
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
          v-model="email"
          name="email"
          label="邮箱"
          placeholder="请输入邮箱"
          :rules="[{ required: true, message: '请输入邮箱' }]"
          clearable
        />
        <van-field
          v-model="code"
          center
          label="验证码"
          placeholder="请输入验证码"
          :rules="[{ required: true, message: '请输入验证码' }]"
          clearable
        >
          <template #button>
            <van-button
              size="small"
              type="primary"
              color="#6c8c5e"
              :disabled="countdown > 0"
              @click="onSendCode"
            >
              {{ countdown > 0 ? countdown + 's' : '发送验证码' }}
            </van-button>
          </template>
        </van-field>
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
        <van-button round block type="primary" native-type="submit" color="#6c8c5e" :loading="loading" loading-text="注册中...">
          注册
        </van-button>
      </div>
    </van-form>
    <div class="reg-links" @click="router.push('/login')">已有账号？去登录</div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { showToast } from 'vant';
import request from '@/utils/request';

const router = useRouter();
const username = ref('');
const email = ref('');
const password = ref('');
const code = ref('');
const loading = ref(false);
const countdown = ref(0);
let timer: ReturnType<typeof setInterval> | null = null;

async function onSendCode() {
  if (!email.value) {
    showToast('请先输入邮箱');
    return;
  }
  try {
    const res = await request.post('/user/send-code', { email: email.value });
    if (res.code === 200) {
      showToast('验证码已发送');
      countdown.value = 60;
      timer = setInterval(() => {
        countdown.value--;
        if (countdown.value <= 0) {
          countdown.value = 0;
          if (timer) clearInterval(timer);
        }
      }, 1000);
    } else {
      showToast(res.message);
    }
  } catch {
    // request.ts 已弹 toast
  }
}

async function onRegister() {
  loading.value = true;
  try {
    const res = await request.post('/user/register', {
      username: username.value,
      email: email.value,
      password: password.value,
      code: code.value
    });
    if (res.code === 200) {
      showToast('注册成功');
      setTimeout(() => router.push('/login'), 500);
    } else {
      showToast(res.message);
    }
  } catch {
    // request.ts 已弹 toast
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.register-page { padding: 40px 20px; }
.register-header { text-align: center; margin-bottom: 40px; }
.reg-icon { font-size: 64px; }
.register-header h2 { margin: 12px 0 6px; font-size: 24px; color: #333; }
.reg-links { text-align: center; margin-top: 16px; font-size: 14px; color: #6c8c5e; }
</style>
