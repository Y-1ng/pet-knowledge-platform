<template>
  <div class="pet-list">
    <div v-if="pets.length > 0" class="pet-wrap">
      <div v-for="pet in pets" :key="pet.id" class="pet-card" @click="router.push(`/pet/health/${pet.id}`)">
        <div class="pet-avatar">{{ speciesIcon(pet.species) }}</div>
        <div class="pet-info">
          <h4>{{ pet.name }}</h4>
          <p>{{ speciesLabel(pet.species) }} · {{ pet.breed || '未知品种' }}</p>
          <p class="pet-birthday">🎂 {{ pet.birthday || '未知' }}</p>
        </div>
        <van-icon name="arrow" color="#ccc" />
      </div>
    </div>
    <van-empty v-else description="还没有添加宠物">
      <van-button round type="primary" color="#6c8c5e" @click="goAdd">添加宠物</van-button>
    </van-empty>

    <van-floating-bubble icon="plus" color="#6c8c5e" @click="goAdd" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import request from '@/utils/request';
import { useUserStore } from '@/stores/user';

const router = useRouter();
const userStore = useUserStore();
const pets = ref<any[]>([]);

onMounted(async () => {
  if (!userStore.isLoggedIn) return;
  try {
    const res = await request.get('/pet/list', { params: { userId: userStore.userInfo?.id } });
    pets.value = res.data || [];
  } catch (e) {}
});

function speciesIcon(s: string) {
  const icons: any = { cat: '🐱', dog: '🐶', rabbit: '🐰', bird: '🐦', fish: '🐟' };
  return icons[s] || '🐾';
}

function speciesLabel(s: string) {
  const labels: any = { cat: '猫咪', dog: '狗狗', rabbit: '兔子', bird: '鸟类', fish: '鱼类' };
  return labels[s] || s;
}

function goAdd() {
  router.push('/pet/edit');
}
</script>

<style scoped>
.pet-list { background: #f7f8fa; min-height: 100%; }
.pet-wrap { padding: 12px; }
.pet-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
}
.pet-avatar { font-size: 48px; }
.pet-info { flex: 1; }
.pet-info h4 { margin: 0 0 4px; font-size: 18px; }
.pet-info p { margin: 0; font-size: 13px; color: #888; }
.pet-birthday { color: #aaa !important; font-size: 12px !important; margin-top: 4px !important; }
</style>
