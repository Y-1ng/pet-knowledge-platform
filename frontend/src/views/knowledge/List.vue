<template>
  <div class="knowledge-list">
    <!-- 品种分类大卡片 -->
    <div class="breed-section">
      <div class="section-header">
        <span class="section-title">🐾 品种大全</span>
      </div>
      <div class="breed-grid">
        <div
          v-for="cat in breedCategories"
          :key="cat.id"
          class="breed-card"
          @click="goBreedList(cat.id)"
        >
          <div class="breed-icon-wrap" :style="{ background: cat.bg }">
            <img :src="cat.img" :alt="cat.name" class="breed-icon-img" />
          </div>
          <div class="breed-info">
            <h4>{{ cat.name }}</h4>
            <p>{{ cat.subtitle }}</p>
          </div>
          <van-icon name="arrow" color="#ccc" />
        </div>
      </div>
    </div>

    <!-- 搜索栏 -->
    <van-search v-model="keyword" placeholder="搜索科普文章..." @search="onSearch" />

    <!-- 分类标签 -->
    <van-tabs v-model:active="activeCategory" @change="onCategoryChange" color="#6c8c5e" class="category-tabs" :ellipsis="false">
      <van-tab title="全部" :name="0"></van-tab>
      <van-tab v-for="cat in categories" :key="cat.id" :title="cat.icon + ' ' + cat.name" :name="cat.id"></van-tab>
    </van-tabs>

    <!-- 文章列表 -->
    <div class="article-wrap">
      <div v-for="item in articles" :key="item.id" class="article-item" @click="goDetail(item.id)">
        <div class="article-info">
          <h4>{{ item.title }}</h4>
          <p>{{ item.summary }}</p>
          <div class="meta">
            <span>👁 {{ item.viewCount }}</span>
            <span>💬 {{ item.commentCount ?? 0 }}</span>
            <span>{{ item.createTime?.substring(0, 10) }}</span>
          </div>
        </div>
      </div>
      <van-empty v-if="articles.length === 0" description="暂无文章" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import request from '@/utils/request';

const router = useRouter();
const keyword = ref('');
const activeCategory = ref(0);
const categories = ref<any[]>([]);
const articles = ref<any[]>([]);

const breedCategories = [
  { id: 1, name: '猫咪品种', subtitle: '波斯、布偶、英短...', bg: '#e8f5e9', img: '/images/breeds/cat/7.jpg' },
  { id: 2, name: '狗狗品种', subtitle: '金毛、柯基、哈士奇...', bg: '#fff3e0', img: '/images/breeds/dog/15.jpg' },
  { id: 3, name: '兔子品种', subtitle: '垂耳兔、侏儒兔、安哥拉...', bg: '#e3f2fd', img: '/images/breeds/rabbit/3.jpg' },
  { id: 4, name: '鸟类品种', subtitle: '虎皮鹦鹉、玄凤、金丝雀...', bg: '#fce4ec', img: '/images/breeds/bird/4.jpg' },
  { id: 5, name: '鱼类品种', subtitle: '金鱼、锦鲤、孔雀鱼...', bg: '#e0f7fa', img: '/images/breeds/fish/13.jpg' },
  { id: 6, name: '爬宠品种', subtitle: '巴西龟、鬃狮蜥、玉米蛇...', bg: '#f3e5f5', img: '/images/breeds/reptile/3.jpg' },
];

onMounted(async () => {
  try {
    const res = await request.get('/knowledge/categories');
    categories.value = res.data || [];
  } catch (e) {}
  await loadArticles();
});

async function loadArticles() {
  try {
    const params: any = {};
    if (activeCategory.value > 0) params.categoryId = activeCategory.value;
    const res = await request.get('/knowledge/list', { params });
    articles.value = res.data || [];
  } catch (e) {}
}

function onCategoryChange() {
  loadArticles();
}

function onSearch() {
  if (!keyword.value.trim()) {
    loadArticles();
    return;
  }
  request.get('/knowledge/search', { params: { keyword: keyword.value } }).then(res => {
    articles.value = res.data || [];
  }).catch(() => {});
}

function goDetail(id: number) {
  router.push(`/knowledge/${id}`);
}

function goBreedList(categoryId: number) {
  router.push(`/breed/${categoryId}`);
}
</script>

<style scoped>
.knowledge-list { background: #f7f8fa; min-height: 100%; }

/* 品种大全卡片区 */
.breed-section {
  background: #fff;
  padding: 14px 16px 6px;
}
.section-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}
.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}
.breed-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}
.breed-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 8px;
  border-radius: 12px;
  cursor: pointer;
  transition: background 0.2s;
  position: relative;
}
.breed-card:active { background: #f5f5f5; }
.breed-card .van-icon {
  display: none;
}
.breed-icon-wrap {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-bottom: 6px;
}
.breed-icon-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.breed-info { text-align: center; }
.breed-info h4 {
  margin: 0;
  font-size: 13px;
  color: #333;
  font-weight: 600;
}
.breed-info p {
  margin: 2px 0 0;
  font-size: 11px;
  color: #999;
}

@media (min-width: 769px) {
  .breed-section {
    padding: 20px 24px 10px;
  }
  .breed-grid {
    grid-template-columns: repeat(6, 1fr);
    gap: 16px;
  }
  .breed-icon-wrap {
    width: 80px;
    height: 80px;
  }
  .breed-info h4 { font-size: 15px; }
  .breed-info p { font-size: 12px; }
}

/* 分类标签 */
.category-tabs { background: #fff; }
.category-tabs :deep(.van-tab) { flex: 1; min-width: 0; }

/* 文章列表 */
.article-wrap { padding: 12px; }
.article-item {
  background: #fff;
  border-radius: 10px;
  padding: 16px;
  margin-bottom: 12px;
  cursor: pointer;
}
.article-item h4 { margin: 0 0 6px; font-size: 16px; color: #333; }
.article-item p { margin: 0 0 10px; font-size: 13px; color: #888; line-height: 1.5; }
.meta { display: flex; gap: 16px; font-size: 12px; color: #aaa; }
</style>
