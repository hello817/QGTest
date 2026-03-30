<template>
  <div id="app">
    <transition name="fade-transform" mode="out-in">
      <router-view />
    </transition>
  </div>
</template>

<style>
body { margin: 0; font-family: "Helvetica Neue",Helvetica,Arial,sans-serif; background: #fdfdfd; }
:root { --orange: #FF8C00; }

/* �������ȶ��� */
.fade-transform-enter-active, .fade-transform-leave-active {
  transition: all .3s;
}
.fade-transform-enter { opacity: 0; transform: translateX(-20px); }
.fade-transform-leave-to { opacity: 0; transform: translateX(20px); }

/* ȫ�ָ��� Element ��ɫ */
.el-button--primary { background-color: var(--orange) !important; border-color: var(--orange) !important; }
.el-tabs__item.is-active { color: var(--orange) !important; }
.el-tabs__active-bar { background-color: var(--orange) !important; }
</style>