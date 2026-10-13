<template>
  <div class="ask-question">
    <van-form @submit="onSubmit">
      <van-cell-group inset style="margin-top: 12px;">
        <van-field
          v-model="title"
          label="问题标题"
          placeholder="请简明扼要地描述你的问题"
          :rules="[{ required: true, message: '请输入问题标题' }]"
        />
        <van-field
          v-model="description"
          label="补充描述"
          type="textarea"
          placeholder="详细描述你的问题（可选）"
          :autosize="{ minHeight: 100 }"
          rows="5"
        />
      </van-cell-group>
      <div style="margin: 16px">
        <van-button round block type="primary" native-type="submit" color="#6c8c5e">提交问题</van-button>
      </div>
    </van-form>
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
const title = ref('');
const description = ref('');

async function onSubmit() {
  if (!userStore.isLoggedIn) { showToast('请先登录'); router.push('/login'); return; }
  try {
    const res = await request.post('/qa/question', {
      userId: userStore.userInfo?.id,
      title: title.value,
      description: description.value
    });
    if (res.code === 200) {
      showToast('提问成功');
      router.push('/qa');
    }
  } catch (e) {
    showToast('提交失败');
  }
}
</script>

<style scoped>
.ask-question { background: #f7f8fa; min-height: 100%; }
</style>
