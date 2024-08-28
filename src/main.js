import Vue from 'vue';
import ElementUI from 'element-ui';
import App from './App.vue';
import router from './router';
import 'element-ui/lib/theme-chalk/index.css';
import locale from 'element-ui/lib/locale/lang/en';
import VueI18n from 'vue-i18n';

Vue.config.productionTip = false

Vue.use(ElementUI, { locale });

Vue.use(VueI18n);

const i18n = new VueI18n({
  locale: 'en', // 设置语言
  messages: {
    en: {
      // 你可以在这里添加其他的英文翻译
    }
  }
});

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
