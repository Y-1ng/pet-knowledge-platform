<template>
  <div class="health-record">
    <div v-if="records.length > 0">
      <div v-for="r in records" :key="r.id" class="record-item">
        <div class="record-icon">{{ typeIcon(r.recordType) }}</div>
        <div class="record-info">
          <h4>{{ r.recordName }}</h4>
          <p>{{ typeLabel(r.recordType) }} · {{ r.recordDate }}</p>
          <p v-if="r.notes" class="record-notes">{{ r.notes }}</p>
        </div>
      </div>
    </div>
    <van-empty v-else description="暂无健康记录" />

    <!-- 添加记录按钮 -->
    <van-floating-bubble icon="plus" color="#6c8c5e" @click="showAdd = true" />

    <!-- 添加记录弹窗 -->
    <van-dialog v-model:show="showAdd" title="添加健康记录" show-cancel-button @confirm="onAddRecord">
      <van-form>
        <van-field v-model="recordName" label="记录名称" placeholder="如：第二针疫苗" />
        <van-field name="recordType" label="类型">
          <template #input>
            <van-radio-group v-model="recordType" direction="horizontal">
              <van-radio name="vaccine">疫苗</van-radio>
              <van-radio name="deworm">驱虫</van-radio>
              <van-radio name="checkup">体检</van-radio>
            </van-radio-group>
          </template>
        </van-field>
        <van-field v-model="recordDate" label="日期" is-link readonly @click="showDatePicker = true" placeholder="选择日期" />
        <van-field v-model="notes" label="备注" type="textarea" placeholder="备注信息" />
      </van-form>
    </van-dialog>

    <van-popup v-model:show="showDatePicker" position="bottom">
      <van-date-picker @confirm="({selectedValues}) => { recordDate = selectedValues.join('-'); showDatePicker = false; }" @cancel="showDatePicker = false" />
    </van-popup>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { showToast } from 'vant';
import request from '@/utils/request';

const route = useRoute();
const records = ref<any[]>([]);
const showAdd = ref(false);
const recordName = ref('');
const recordType = ref('vaccine');
const recordDate = ref('');
const notes = ref('');
const showDatePicker = ref(false);

function typeIcon(t: string) {
  const icons: any = { vaccine: '💉', deworm: '💊', checkup: '🏥', illness: '🤒' };
  return icons[t] || '📝';
}
function typeLabel(t: string) {
  const labels: any = { vaccine: '疫苗', deworm: '驱虫', checkup: '体检', illness: '生病' };
  return labels[t] || t;
}

onMounted(async () => {
  try {
    const res = await request.get(`/pet/health/${route.params.petId}`);
    records.value = res.data || [];
  } catch (e) {}
});

async function onAddRecord() {
  if (!recordName.value || !recordDate.value) {
    showToast('请填写完整信息');
    return;
  }
  try {
    await request.post('/pet/health', {
      petId: route.params.petId,
      recordType: recordType.value,
      recordName: recordName.value,
      recordDate: recordDate.value,
      notes: notes.value
    });
    showToast('添加成功');
    showAdd.value = false;
    recordName.value = '';
    recordDate.value = '';
    notes.value = '';
    // 刷新
    const res = await request.get(`/pet/health/${route.params.petId}`);
    records.value = res.data || [];
  } catch (e) {
    showToast('添加失败');
  }
}
</script>

<style scoped>
.health-record { background: #f7f8fa; min-height: 100%; padding: 12px; }
.record-item {
  background: #fff;
  border-radius: 10px;
  padding: 16px;
  margin-bottom: 10px;
  display: flex;
  align-items: flex-start;
  gap: 12px;
}
.record-icon { font-size: 32px; }
.record-info h4 { margin: 0 0 4px; font-size: 16px; }
.record-info p { margin: 0; font-size: 13px; color: #888; }
.record-notes { color: #aaa !important; margin-top: 4px !important; }
</style>
