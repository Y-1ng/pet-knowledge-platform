<template>
  <div class="guide-list">
    <van-tabs v-model:active="activeType" @change="loadGuides" color="#6c8c5e">
      <van-tab title="全部" name=""></van-tab>
      <van-tab title="饲养指南" name="feeding"></van-tab>
      <van-tab title="健康护理" name="health"></van-tab>
      <van-tab title="行为训练" name="training"></van-tab>
    </van-tabs>

    <div class="guide-wrap">
      <div v-for="item in guides" :key="item.id" class="guide-item" @click="goDetail(item.id)">
        <div class="guide-content">
          <h4>{{ item.title }}</h4>
          <div class="tag">{{ typeLabel(item.type) }}</div>
        </div>
        <van-icon name="arrow" color="#ccc" />
      </div>
      <van-empty v-if="guides.length === 0" description="暂无指南" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import request from '@/utils/request';

const router = useRouter();
const activeType = ref('');
const guides = ref<any[]>([]);

onMounted(() => loadGuides());

async function loadGuides() {
  try {
    const params: any = {};
    if (activeType.value) params.type = activeType.value;
    const res = await request.get('/guide/list', { params });
    guides.value = res.data || [];
  } catch (e) {}
}

function typeLabel(t: string) {
  const map: any = { feeding: '饲养', health: '健康', training: '训练' };
  return map[t] || t;
}

function goDetail(id: number) {
  router.push(`/guide/${id}`);
}
</script>

<style scoped>
.guide-list { background: #f7f8fa; min-height: 100%; }
.guide-wrap { padding: 12px; }
.guide-item {
  background: #fff;
  border-radius: 10px;
  padding: 16px;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
}
.guide-content h4 { margin: 0 0 6px; font-size: 16px; color: #333; }
.tag { display: inline-block; padding: 2px 10px; background: #e8f5e9; color: #6c8c5e; border-radius: 10px; font-size: 12px; }
</style>
