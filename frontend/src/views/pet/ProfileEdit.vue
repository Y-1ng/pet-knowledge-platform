<template>
  <div class="pet-edit">
    <van-form @submit="onSave">
      <van-cell-group inset style="margin-top: 12px;">
        <van-field v-model="name" label="宠物名字" placeholder="请输入名字" :rules="[{ required: true }]" />
        <van-field name="species" label="物种">
          <template #input>
            <van-radio-group v-model="species" direction="horizontal">
              <van-radio name="cat">🐱 猫</van-radio>
              <van-radio name="dog">🐶 狗</van-radio>
              <van-radio name="rabbit">🐰 兔</van-radio>
              <van-radio name="other">🐾 其他</van-radio>
            </van-radio-group>
          </template>
        </van-field>
        <van-field v-model="breed" label="品种" placeholder="如：橘猫、金毛" />
        <van-field name="gender" label="性别">
          <template #input>
            <van-radio-group v-model="gender" direction="horizontal">
              <van-radio name="M">♂ 男生</van-radio>
              <van-radio name="F">♀ 女生</van-radio>
            </van-radio-group>
          </template>
        </van-field>
        <van-field v-model="birthday" label="生日" is-link readonly @click="showPicker = true" placeholder="选择生日" />
        <van-field v-model="weight" label="体重(kg)" type="digit" placeholder="如：3.5" />
      </van-cell-group>
      <div style="margin: 16px">
        <van-button round block type="primary" native-type="submit" color="#6c8c5e">保存</van-button>
      </div>
    </van-form>

    <van-popup v-model:show="showPicker" position="bottom">
      <van-date-picker @confirm="onConfirmDate" @cancel="showPicker = false" :min-date="minDate" :max-date="maxDate" />
    </van-popup>
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
const name = ref('');
const species = ref('cat');
const breed = ref('');
const gender = ref('M');
const birthday = ref('');
const weight = ref('');
const showPicker = ref(false);
const minDate = new Date(2010, 0, 1);
const maxDate = new Date();

function onConfirmDate({ selectedValues }: any) {
  birthday.value = selectedValues.join('-');
  showPicker.value = false;
}

async function onSave() {
  try {
    const res = await request.post('/pet/save', {
      userId: userStore.userInfo?.id,
      name: name.value,
      species: species.value,
      breed: breed.value,
      gender: gender.value,
      birthday: birthday.value,
      weight: weight.value || null
    });
    if (res.code === 200) {
      showToast('保存成功');
      router.push('/pet/list');
    }
  } catch (e) {
    showToast('保存失败');
  }
}
</script>

<style scoped>
.pet-edit { background: #f7f8fa; min-height: 100%; }
</style>
