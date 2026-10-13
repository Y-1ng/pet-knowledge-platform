<template>
  <div class="password-page">
    <van-form @submit="onUpdate">
      <van-cell-group inset style="margin-top: 20px;">
        <van-field
          v-model="oldPassword"
          type="password"
          label="原密码"
          placeholder="请输入原密码"
          :rules="[{ required: true, message: '请输入原密码' }]"
        />
        <van-field
          v-model="newPassword"
          type="password"
          label="新密码"
          placeholder="请输入新密码"
          :rules="[{ required: true, message: '请输入新密码' }]"
        />
        <van-field
          v-model="confirmPassword"
          type="password"
          label="确认密码"
          placeholder="请再次输入新密码"
          :rules="[{ validator: (v) => v === newPassword, message: '两次密码不一致' }]"
        />
      </van-cell-group>
      <div style="margin: 16px">
        <van-button round block type="primary" native-type="submit" color="#6c8c5e">修改密码</van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { showToast } from 'vant';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import request from '@/utils/request';

const router = useRouter();
const userStore = useUserStore();
const oldPassword = ref('');
const newPassword = ref('');
const confirmPassword = ref('');

async function onUpdate() {
  try {
    const res = await request.put('/user/password', {
      userId: userStore.userInfo?.id,
      oldPassword: oldPassword.value,
      newPassword: newPassword.value
    });
    if (res.code === 200) {
      showToast('密码修改成功');
      router.push('/profile');
    } else {
      showToast(res.message);
    }
  } catch (e) {
    showToast('网络错误');
  }
}
</script>
