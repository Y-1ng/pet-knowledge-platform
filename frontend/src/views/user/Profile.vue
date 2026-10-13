<template>
  <div class="profile-page">
    <!-- 隐藏文件选择器 -->
    <input ref="fileInputRef" type="file" accept="image/*" style="display:none" @change="onFileChange" />

    <!-- 用户信息头 -->
    <div class="profile-header">
      <div class="header-bg"></div>
      <div class="avatar-wrapper" @click="onAvatarClick">
        <div class="avatar-ring">
          <div class="avatar" v-if="userStore.userInfo?.avatar">
            <van-image :src="userStore.userInfo.avatar" round fit="cover" />
          </div>
          <div class="avatar avatar-placeholder" v-else>
            {{ userStore.userInfo?.nickname?.charAt(0) || '?' }}
          </div>
        </div>
        <div class="avatar-overlay">
          <van-icon name="photograph" size="16" color="#fff" />
          <span>更换头像</span>
        </div>
      </div>
      <div class="user-name">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</div>
      <div class="user-email" v-if="userStore.userInfo?.email">
        <van-icon name="envelop-o" size="14" /> {{ userStore.userInfo.email }}
      </div>
    </div>

    <!-- 未登录 -->
    <div v-if="!userStore.isLoggedIn" class="not-logged-in">
      <van-empty description="您还没有登录">
        <van-button round type="primary" color="#6c8c5e" @click="router.push('/login')">去登录</van-button>
      </van-empty>
    </div>

    <!-- 功能菜单 -->
    <div class="menu-section" v-if="userStore.isLoggedIn">
      <van-cell-group inset>
        <van-cell title="个人信息" icon="info-o" is-link @click="showEdit = true" />
        <van-cell title="修改密码" icon="lock-o" is-link @click="router.push('/password')" />
        <van-cell title="我的宠物" icon="smile-o" is-link @click="router.push('/pet/list')" />
        <van-cell title="我的动态" icon="chat-o" is-link @click="router.push('/my-moments')" />
        <van-cell title="我的评论" icon="comment-o" is-link @click="router.push(`/user-comments/${userStore.userInfo?.id}`)" />
        <van-cell title="我的收藏" icon="star-o" is-link @click="router.push('/qa')" />
      </van-cell-group>
    </div>

    <!-- 退出 -->
    <div style="margin: 30px 16px" v-if="userStore.isLoggedIn">
      <van-button round block color="#ee0a24" @click="onLogout">退出登录</van-button>
    </div>

    <!-- 编辑个人信息 -->
    <van-dialog v-model:show="showEdit" title="编辑个人信息" show-cancel-button @confirm="onSaveProfile">
      <van-form>
        <van-field v-model="editNickname" label="昵称" placeholder="请输入昵称" />
        <van-field v-model="editBio" label="简介" placeholder="个人简介" type="textarea" :autosize="{ minHeight: 60 }" />
      </van-form>
    </van-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { showToast, showLoadingToast, closeToast } from 'vant';
import { useUserStore } from '@/stores/user';
import request from '@/utils/request';

const router = useRouter();
const userStore = useUserStore();
const showEdit = ref(false);
const editNickname = ref(userStore.userInfo?.nickname || '');
const editBio = ref(userStore.userInfo?.bio || '');
const fileInputRef = ref<HTMLInputElement | null>(null);

function onAvatarClick() {
  if (!userStore.isLoggedIn) { showToast('请先登录'); return; }
  fileInputRef.value?.click();
}

async function onFileChange(e: Event) {
  const target = e.target as HTMLInputElement;
  const file = target.files?.[0];
  if (!file) return;

  if (file.size > 20 * 1024 * 1024) {
    showToast('文件大小不能超过20MB');
    return;
  }

  showLoadingToast({ message: '上传头像中...', forbidClick: true, duration: 0 });

  try {
    const formData = new FormData();
    formData.append('file', file);

    const uploadRes = await request({
      url: '/upload',
      method: 'post',
      data: formData,
      headers: { 'Content-Type': 'multipart/form-data' }
    });
    closeToast();

    if (uploadRes.code !== 200) {
      showToast(uploadRes.message || '上传失败');
      return;
    }

    const avatarUrl = uploadRes.data.url;

    const res = await request.post('/user/updateAvatar', {
      userId: userStore.userInfo?.id,
      url: avatarUrl
    });
    if (res.code === 200 && res.data) {
      userStore.setUser(res.data);
      showToast('头像更新成功');
    } else {
      showToast(res.message || '头像更新失败');
    }
  } catch (e) {
    closeToast();
    showToast('上传失败');
  } finally {
    target.value = '';
  }
}

async function onSaveProfile() {
  try {
    const res = await request.put('/user/profile', {
      id: userStore.userInfo?.id,
      nickname: editNickname.value,
      bio: editBio.value
    });
    if (res.code === 200 && res.data) {
      userStore.setUser(res.data);
      showToast('保存成功');
    }
  } catch (e) {
    showToast('保存失败');
  }
}

function onLogout() {
  userStore.logout();
  showToast('已退出');
  router.push('/');
}
</script>

<style scoped>
.profile-page { min-height: 100%; background: #f7f8fa; padding-bottom: 20px; }

.profile-header {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 50px 20px 30px;
  overflow: hidden;
}
.header-bg {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: linear-gradient(135deg, #6c8c5e, #8fbc8f);
  z-index: 0;
}

.avatar-wrapper {
  position: relative; z-index: 1; cursor: pointer; margin-bottom: 14px;
  transition: transform 0.3s;
}
.avatar-wrapper:active { transform: scale(0.95); }
.avatar-wrapper:hover .avatar-ring { box-shadow: 0 0 0 8px rgba(255,255,255,0.3); }

.avatar-ring {
  width: 90px; height: 90px; border-radius: 50%;
  padding: 3px;
  background: rgba(255,255,255,0.6);
  transition: box-shadow 0.3s;
}
.avatar {
  width: 100%; height: 100%; border-radius: 50%;
  overflow: hidden;
  display: flex; align-items: center; justify-content: center;
}
.avatar :deep(.van-image) { width: 100%; height: 100%; }
.avatar-placeholder {
  background: rgba(255,255,255,0.3);
  font-size: 36px; color: #fff; font-weight: bold;
}

.avatar-overlay {
  position: absolute; bottom: -4px; left: 50%; transform: translateX(-50%);
  padding: 3px 10px; background: rgba(0,0,0,0.5);
  border-radius: 10px; display: flex; align-items: center; gap: 4px;
  opacity: 0; transition: all 0.3s; white-space: nowrap;
  backdrop-filter: blur(4px);
}
.avatar-wrapper:hover .avatar-overlay { opacity: 1; bottom: -8px; }
.avatar-overlay span { color: #fff; font-size: 11px; }

.user-name { position: relative; z-index: 1; font-size: 22px; font-weight: 700; color: #fff; margin-bottom: 4px; }
.user-email { position: relative; z-index: 1; font-size: 13px; color: rgba(255,255,255,0.8); display: flex; align-items: center; gap: 4px; }

.not-logged-in { padding: 40px 0; }
.menu-section { margin-top: -12px; }
</style>
