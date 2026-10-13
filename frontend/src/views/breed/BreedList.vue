<template>
  <div class="breed-list">
    <!-- 分类标题 -->
    <div class="category-header">
      <span class="cat-icon">{{ categoryIcon }}</span>
      <div>
        <h2>{{ categoryName }}</h2>
        <p>共 {{ breeds.length }} 个品种</p>
      </div>
    </div>

    <!-- 搜索 -->
    <van-search v-model="keyword" shape="round" placeholder="搜索品种名称..." @search="filterBreeds" />

    <!-- 品种网格 -->
    <div class="breed-grid">
      <div
        v-for="breed in filteredBreeds"
        :key="breed.id"
        class="breed-card"
        @click="goDetail(breed.id)"
      >
        <div class="breed-img">
          <img
            :src="getImageUrl(breed)"
            :alt="breed.name"
            @error="onImgError($event)"
          />
        </div>
        <div class="breed-info">
          <h4>{{ breed.name }}</h4>
          <p class="en-name">{{ breed.englishName || '' }}</p>
          <div class="tags">
            <span class="tag">🧬 {{ breed.origin || '未知' }}</span>
            <span class="tag">⏳ {{ breed.lifespan || '未知' }}</span>
          </div>
        </div>
      </div>
    </div>

    <van-empty v-if="filteredBreeds.length === 0" description="没有找到匹配的品种" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import request from '@/utils/request';

const route = useRoute();
const router = useRouter();
const keyword = ref('');
const breeds = ref<any[]>([]);
const categoryName = ref('');
const categoryIcon = ref('');

const categoryMap: Record<string, any> = {
  '1': { name: '猫咪品种', icon: '🐱', dir: 'cat' },
  '2': { name: '狗狗品种', icon: '🐶', dir: 'dog' },
  '3': { name: '兔子品种', icon: '🐰', dir: 'rabbit' },
  '4': { name: '鸟类品种', icon: '🐦', dir: 'bird' },
  '5': { name: '鱼类品种', icon: '🐟', dir: 'fish' },
  '6': { name: '爬宠品种', icon: '🦎', dir: 'reptile' },
};

const filteredBreeds = computed(() => {
  if (!keyword.value.trim()) return breeds.value;
  const kw = keyword.value.toLowerCase();
  return breeds.value.filter((b: any) =>
    b.name.toLowerCase().includes(kw) ||
    (b.englishName && b.englishName.toLowerCase().includes(kw))
  );
});

onMounted(async () => {
  const cat = categoryMap[route.params.categoryId as string] || { name: '品种', icon: '🐾', dir: 'other' };
  categoryName.value = cat.name;
  categoryIcon.value = cat.icon;

  try {
    const res = await request.get(`/breed/list/${route.params.categoryId}`);
    breeds.value = res.data || [];
  } catch (e) {}
});

function getImageUrl(breed: any) {
  const cat = categoryMap[route.params.categoryId as string];
  if (!cat) return '';
  // 使用品种ID映射到图片编号（1-15循环）
  const imgIndex = ((breed.id - 1) % 15) + 1;
  return `/images/breeds/${cat.dir}/${imgIndex}.jpg`;
}

function onImgError(e: Event) {
  const target = e.target as HTMLImageElement;
  const map: Record<string, string> = {
    '1': '🐱', '2': '🐶', '3': '🐰', '4': '🐦', '5': '🐟', '6': '🦎'
  };
  const catId = route.params.categoryId as string;
  target.style.display = 'none';
  if (target.parentElement) {
    target.parentElement.innerHTML = `<span style="font-size:64px">${map[catId] || '🐾'}</span>`;
  }
}

function goDetail(id: number) {
  router.push(`/breed/detail/${id}`);
}
</script>

<style scoped>
.breed-list { background: #f7f8fa; min-height: 100%; }
.category-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 16px;
  background: linear-gradient(135deg, #6c8c5e, #8fbc8f);
  color: #fff;
}
.category-header .cat-icon { font-size: 48px; }
.category-header h2 { margin: 0; font-size: 22px; }
.category-header p { margin: 4px 0 0; font-size: 13px; opacity: 0.85; }

.breed-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  padding: 12px;
}

/* 桌面端：4列 */
@media (min-width: 769px) {
  .breed-grid {
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
    padding: 16px 24px;
  }
  .category-header {
    padding: 28px 24px;
  }
  .category-header h2 { font-size: 26px; }
}
.breed-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s;
}
.breed-card:active { transform: scale(0.97); }
.breed-img {
  width: 100%;
  height: 180px;
  overflow: hidden;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
}
.breed-img img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}
.breed-info { padding: 10px 12px 14px; }
.breed-info h4 { margin: 0; font-size: 14px; color: #333; }
.en-name { margin: 2px 0 6px; font-size: 11px; color: #aaa; }
.tags { display: flex; flex-wrap: wrap; gap: 4px; }
.tag { font-size: 11px; color: #888; background: #f5f5f5; padding: 2px 8px; border-radius: 8px; }
</style>
