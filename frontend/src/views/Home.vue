<template>
  <div class="home-page">
    <!-- 轮播 + 品牌区合并 -->
    <div class="carousel-hero-wrap">
      <van-swipe class="pet-carousel" :autoplay="4000" indicator-color="white" lazy-render>
        <van-swipe-item v-for="(item, idx) in carouselImages" :key="idx">
          <div class="carousel-slide" @click="goBreedList(item.categoryId)">
            <img :src="item.url" :alt="item.name" />
            <div class="carousel-overlay"></div>
          </div>
        </van-swipe-item>
      </van-swipe>
      <!-- 品牌文字叠加在轮播上方 -->
      <div class="hero-overlay">
        <h1>🐾 宠知</h1>
        <p>你的宠物知识百科 · AI 智能问答平台</p>
      </div>
    </div>

    <!-- 品种分类大卡片 -->
    <div class="section">
      <div class="section-header">
        <span class="section-title">📋 宠物品种大全</span>
      </div>
      <div class="category-grid">
        <div
          v-for="cat in categories"
          :key="cat.id"
          class="category-card"
          @click="goBreedList(cat.id)"
        >
          <div class="cat-icon-wrap" :style="{ background: cat.bg }">
            <span class="cat-icon">{{ cat.icon }}</span>
          </div>
          <div class="cat-info">
            <h3>{{ cat.name }}</h3>
            <p>{{ cat.desc }}</p>
          </div>
          <van-icon name="arrow" color="#ccc" />
        </div>
      </div>
    </div>

    <!-- 推荐品种 -->
    <div class="section" v-if="featuredBreeds.length > 0">
      <div class="section-header">
        <span class="section-title">🌟 推荐品种</span>
      </div>
      <div class="featured-scroll">
        <div
          v-for="breed in featuredBreeds"
          :key="breed.id"
          class="featured-item"
          @click="goBreedDetail(breed.id)"
        >
          <div class="featured-img">
            <img :src="getBreedImg(breed)" :alt="breed.name" @error="onBreedImgError($event)" />
          </div>
          <p class="featured-name">{{ breed.name }}</p>
        </div>
      </div>
    </div>

    <!-- 热门知识 -->
    <div class="section">
      <div class="section-header">
        <span class="section-title">📖 热门科普</span>
        <span class="section-more" @click="goPage('/knowledge')">更多 &gt;</span>
      </div>
      <div v-for="item in hotKnowledge" :key="item.id" class="article-card" @click="goPage(`/knowledge/${item.id}`)">
        <h4>{{ item.title }}</h4>
        <p class="article-summary">{{ item.summary }}</p>
        <div class="article-meta">
          <span>👁 {{ item.viewCount }}</span>
          <span>💬 {{ item.commentCount ?? 0 }}</span>
          <span>{{ formatTime(item.createTime) }}</span>
        </div>
      </div>
      <van-empty v-if="hotKnowledge.length === 0" description="暂无内容" />
    </div>

    <!-- 底部留白 -->
    <div style="height: 20px"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import request from '@/utils/request';

const router = useRouter();

const categories = [
  { id: 1, icon: '🐱', name: '猫咪品种', desc: '波斯、布偶、英短...', bg: '#e8f5e9' },
  { id: 2, icon: '🐶', name: '狗狗品种', desc: '金毛、柯基、哈士奇...', bg: '#fff3e0' },
  { id: 3, icon: '🐰', name: '兔子品种', desc: '垂耳兔、侏儒兔、安哥拉...', bg: '#e3f2fd' },
  { id: 4, icon: '🐦', name: '鸟类品种', desc: '虎皮鹦鹉、玄凤、金丝雀...', bg: '#fce4ec' },
  { id: 5, icon: '🐟', name: '鱼类品种', desc: '金鱼、锦鲤、孔雀鱼...', bg: '#e0f7fa' },
  { id: 6, icon: '🦎', name: '爬宠品种', desc: '巴西龟、鬃狮蜥、玉米蛇...', bg: '#f3e5f5' },
];

const featuredBreeds = ref<any[]>([]);
const hotKnowledge = ref<any[]>([]);
const carouselImages = ref<any[]>([]);

onMounted(async () => {
  generateCarousel();
  try {
    const res = await request.get('/breed/featured');
    featuredBreeds.value = res.data || [];
  } catch (e) {}
  try {
    const res = await request.get('/knowledge/hot');
    hotKnowledge.value = res.data || [];
  } catch (e) {}
});

function generateCarousel() {
  // 从6个分类中各随机选一张图片，打乱后展示
  const pool = [
    { dir: 'cat',     name: '🐱 猫咪',   categoryId: 1 },
    { dir: 'dog',     name: '🐶 狗狗',   categoryId: 2 },
    { dir: 'rabbit',  name: '🐰 兔子',   categoryId: 3 },
    { dir: 'bird',    name: '🐦 鸟类',   categoryId: 4 },
    { dir: 'fish',    name: '🐟 鱼类',   categoryId: 5 },
    { dir: 'reptile', name: '🦎 爬宠',   categoryId: 6 },
  ];
  const result: any[] = [];
  for (const p of pool) {
    const idx = Math.floor(Math.random() * 15) + 1;
    result.push({
      url: `/images/breeds/${p.dir}/${idx}.jpg`,
      name: p.name,
      categoryId: p.categoryId,
    });
  }
  // Fisher-Yates 洗牌
  for (let i = result.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [result[i], result[j]] = [result[j], result[i]];
  }
  carouselImages.value = result;
}

function goBreedList(categoryId: number) {
  router.push(`/breed/${categoryId}`);
}

function goBreedDetail(id: number) {
  router.push(`/breed/detail/${id}`);
}

function goPage(path: string) {
  router.push(path);
}

function getBreedImg(breed: any) {
  const dirMap: Record<number, string> = { 1: 'cat', 2: 'dog', 3: 'rabbit', 4: 'bird', 5: 'fish', 6: 'reptile' };
  const dir = dirMap[breed.categoryId] || 'cat';
  const idx = ((breed.id - 1) % 15) + 1;
  return `/images/breeds/${dir}/${idx}.jpg`;
}

function onBreedImgError(e: Event) {
  const target = e.target as HTMLImageElement;
  target.style.display = 'none';
}

function formatTime(time: string) {
  if (!time) return '';
  return time.substring(0, 10);
}
</script>

<style scoped>
.home-page { padding: 0 0 12px 0; }

/* 轮播 + 品牌区合并 */
.carousel-hero-wrap {
  position: relative;
  width: 100%;
}
.pet-carousel,
.carousel-slide {
  width: 100%;
  height: 260px;
}
.carousel-slide {
  position: relative;
  overflow: hidden;
  background: #e0e0e0;
  cursor: pointer;
}
.carousel-slide img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.carousel-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(0,0,0,0.5) 0%, rgba(0,0,0,0.2) 50%, rgba(0,0,0,0.4) 100%);
}
.hero-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: #fff;
  pointer-events: none;
  z-index: 2;
}
.hero-overlay h1 {
  margin: 0;
  font-size: 36px;
  text-shadow: 0 2px 12px rgba(0,0,0,0.5);
}
.hero-overlay p {
  margin: 10px 0 0;
  font-size: 15px;
  opacity: 0.95;
  text-shadow: 0 1px 8px rgba(0,0,0,0.4);
  max-width: 80%;
}

@media (min-width: 769px) {
  .pet-carousel,
  .carousel-slide {
    height: 380px;
  }
  .hero-overlay h1 { font-size: 48px; }
  .hero-overlay p { font-size: 20px; }
}

/* Sections */
.section {
  margin: 12px;
  background: #fff;
  border-radius: 12px;
  padding: 16px;
}

@media (min-width: 769px) {
  .section {
    margin: 16px 24px;
    padding: 20px 24px;
  }
  .section-title { font-size: 18px; }
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}
.section-title { font-size: 16px; font-weight: 600; color: #333; }
.section-more { font-size: 13px; color: #999; }

/* Category Cards */
.category-grid { display: flex; flex-direction: column; gap: 10px; }
.category-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.2s;
}
.category-card:active { background: #f5f5f5; }
.cat-icon-wrap {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.cat-icon { font-size: 32px; }
.cat-info { flex: 1; }
.cat-info h3 { margin: 0; font-size: 16px; color: #333; }
.cat-info p { margin: 2px 0 0; font-size: 13px; color: #999; }

/* Featured Breeds Scroll */
.featured-scroll {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 4px;
  -webkit-overflow-scrolling: touch;
}
.featured-scroll::-webkit-scrollbar { display: none; }
.featured-item {
  flex-shrink: 0;
  width: 100px;
  cursor: pointer;
}
.featured-img {
  width: 100px;
  height: 130px;
  border-radius: 12px;
  overflow: hidden;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
}
.featured-img img { width: 100%; height: 100%; object-fit: contain; }
.featured-name { margin: 6px 0 0; font-size: 12px; text-align: center; color: #555; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

/* Article Cards (same as before) */
.article-card { padding: 12px 0; border-bottom: 1px solid #f0f0f0; cursor: pointer; }
.article-card:last-child { border-bottom: none; }
.article-card h4 { margin: 0 0 6px; font-size: 15px; color: #333; }
.article-summary { font-size: 13px; color: #888; margin: 0 0 8px; line-height: 1.5; }
.article-meta { display: flex; gap: 16px; font-size: 12px; color: #aaa; }
</style>
