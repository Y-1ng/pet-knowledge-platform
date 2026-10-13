<template>
  <div class="create-post">
    <van-form @submit="onSubmit">
      <van-cell-group inset style="margin-top: 12px;">
        <van-field
          v-model="title"
          label="标题"
          placeholder="输入帖子标题"
          :rules="[{ required: true, message: '请输入标题' }]"
        />
        <van-field
          v-model="content"
          label="内容"
          type="textarea"
          placeholder="分享你的宠物故事..."
          :rules="[{ required: true, message: '请输入内容' }]"
          :autosize="{ minHeight: 120 }"
          rows="6"
        />
      </van-cell-group>
      <div style="margin: 16px">
        <van-button round block type="primary" native-type="submit" color="#6c8c5e">发布帖子</van-button>
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
const content = ref('');

async function onSubmit() {
  if (!userStore.isLoggedIn) { showToast('请先登录'); router.push('/login'); return; }
  try {
    const res = await request.post('/community/post', {
      userId: userStore.userInfo?.id,
      title: title.value,
      content: content.value
    });
    if (res.code === 200) {
      showToast('发布成功');
      router.push('/community');
    }
  } catch (e) {
    showToast('发布失败');
  }
}
</script>

<style scoped>
.create-post { background: #f7f8fa; min-height: 100%; }
</style>
