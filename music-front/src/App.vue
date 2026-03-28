<template>
  <el-container class="app-container">
    <AppHeader />
    <el-main class="app-main">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </el-main>
    <GlobalPlayer />
  </el-container>
</template>

<script setup>
import AppHeader from '@/components/AppHeader.vue'
import GlobalPlayer from './components/GlobalPlayer.vue';
</script>

<style>
.app-container {
  min-height: 100vh;
  background: var(--bg-color);
}
.app-main {
  max-width: 1200px;
  margin: 0 auto;
  padding: var(--space-lg) var(--space-md);
  width: 100%;
  box-sizing: border-box;
}

/* 页面转场动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 响应式移动端 */
@media (max-width: 768px) {
  .app-main {
    padding: var(--space-md);
  }
}

/* 全局覆盖 Element Plus 默认样式 */
.el-card {
  border-radius: var(--radius-lg) !important;
  border: none !important;
  box-shadow: var(--shadow-sm) !important;
  transition: all 0.3s ease;
}
.el-card:hover {
  box-shadow: var(--shadow-md) !important;
}
.el-card__header {
  border-bottom: 1px solid var(--color-gray-200);
  padding: var(--space-md) var(--space-lg);
  font-weight: 600;
}
.el-card__body {
  padding: var(--space-lg);
}

.el-button {
  border-radius: var(--radius-md);
  transition: all 0.2s ease;
}
.el-button--primary {
  background-color: var(--color-primary);
  border-color: var(--color-primary);
}
.el-button--primary:hover {
  background-color: var(--color-primary-light);
  border-color: var(--color-primary-light);
  transform: translateY(-1px);
  box-shadow: var(--shadow-sm);
}
.el-button--primary:active {
  transform: translateY(0);
}

.el-table__row:hover > td {
  background-color: var(--color-gray-100) !important;
  transition: background-color 0.2s;
}

.el-input__wrapper {
  border-radius: var(--radius-md);
}

/* 滚动条（可选） */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}
::-webkit-scrollbar-thumb {
  background: var(--color-gray-400);
  border-radius: 3px;
}
::-webkit-scrollbar-thumb:hover {
  background: var(--color-gray-500);
}
</style>