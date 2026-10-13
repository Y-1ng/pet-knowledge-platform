<template>
  <div class="breed-detail" v-if="breed.id">
    <van-skeleton title :row="6" :loading="!loaded">
      <!-- 顶部图片 -->
      <div class="detail-img">
        <img
          :src="getImageUrl()"
          :alt="breed.name"
          @error="onImgError"
        />
        <div class="img-overlay">
          <h2>{{ breed.name }}</h2>
          <p>{{ breed.englishName }}</p>
        </div>
      </div>

      <!-- 快速信息 -->
      <div class="quick-info">
        <div class="info-item" v-if="breed.origin">
          <span class="info-icon">🌍</span>
          <div>
            <p class="info-label">原产地</p>
            <p class="info-value">{{ breed.origin }}</p>
          </div>
        </div>
        <div class="info-item" v-if="breed.lifespan">
          <span class="info-icon">⏳</span>
          <div>
            <p class="info-label">寿命</p>
            <p class="info-value">{{ breed.lifespan }}</p>
          </div>
        </div>
        <div class="info-item" v-if="breed.weight">
          <span class="info-icon">⚖️</span>
          <div>
            <p class="info-label">体重</p>
            <p class="info-value">{{ breed.weight }}</p>
          </div>
        </div>
        <div class="info-item" v-if="breed.personality">
          <span class="info-icon">💝</span>
          <div>
            <p class="info-label">性格特点</p>
            <p class="info-value">{{ breed.personality }}</p>
          </div>
        </div>
      </div>

      <!-- 品种介绍 -->
      <div class="section">
        <h3>📖 品种介绍</h3>
        <p class="desc-text">{{ breed.description }}</p>
      </div>

      <!-- 特征信息 -->
      <div class="section" v-if="breed.coat || breed.colors || breed.height">
        <h3>🔍 外观特征</h3>
        <van-cell-group inset>
          <van-cell title="毛发" :value="breed.coat" v-if="breed.coat" />
          <van-cell title="常见毛色" :value="breed.colors" v-if="breed.colors" />
          <van-cell title="身高" :value="breed.height" v-if="breed.height" />
        </van-cell-group>
      </div>

      <!-- 饲养要点 -->
      <div class="section" v-if="breed.careTips">
        <h3>💡 饲养要点</h3>
        <div class="care-content">{{ breed.careTips }}</div>
      </div>

      <div style="height: 20px"></div>
    </van-skeleton>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import request from '@/utils/request';

const route = useRoute();
const breed = ref<any>({});
const loaded = ref(false);

const categoryMap: Record<string, string> = {
  '1': 'cat', '2': 'dog', '3': 'rabbit',
  '4': 'bird', '5': 'fish', '6': 'reptile',
};

onMounted(async () => {
  try {
    const res = await request.get(`/breed/${route.params.id}`);
    breed.value = res.data || {};
  } catch (e) {}
  loaded.value = true;
});

function getImageUrl() {
  const catId = breed.value.categoryId;
  const dir = categoryMap[String(catId)] || 'cat';
  const imgIndex = ((breed.value.id - 1) % 15) + 1;
  return `/images/breeds/${dir}/${imgIndex}.jpg`;
}

function onImgError(e: Event) {
  const target = e.target as HTMLImageElement;
  const icons: Record<string, string> = { '1': '🐱', '2': '🐶', '3': '🐰', '4': '🐦', '5': '🐟', '6': '🦎' };
  const catId = breed.value.categoryId;
  target.style.display = 'none';
  if (target.parentElement) {
    target.parentElement.innerHTML = `<span style="font-size:80px">${icons[String(catId)] || '🐾'}</span>`;
  }
}
</script>

<style scoped>
.breed-detail { background: #f7f8fa; min-height: 100%; padding-bottom: 20px; }
.detail-img {
  width: 100%;
  height: 380px;
  position: relative;
  overflow: hidden;
  background: #e8e8e8;
  display: flex;
  align-items: center;
  justify-content: center;
}
.detail-img img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}
.img-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 40px 20px 20px;
  background: linear-gradient(transparent, rgba(0,0,0,0.7));
  color: #fff;
}
.img-overlay h2 { margin: 0; font-size: 26px; }
.img-overlay p { margin: 4px 0 0; font-size: 14px; opacity: 0.85; }

.quick-info {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  padding: 16px;
  background: #fff;
  margin: 0 0 12px 0;
}
.info-item {
  display: flex;
  align-items: center;
  gap: 10px;
}
.info-icon { font-size: 28px; }
.info-label { margin: 0; font-size: 11px; color: #999; }
.info-value { margin: 2px 0 0; font-size: 13px; color: #444; }

.section {
  background: #fff;
  margin: 0 0 12px 0;
  padding: 16px;
}
.section h3 { margin: 0 0 12px; font-size: 16px; color: #333; }
.desc-text { font-size: 14px; line-height: 1.8; color: #555; margin: 0; }
.care-content { font-size: 14px; line-height: 1.8; color: #555; white-space: pre-wrap; }
</style>
