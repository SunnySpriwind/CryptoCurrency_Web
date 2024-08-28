import Vue from 'vue'
import VueRouter from 'vue-router'
import HomeView from '../views/HomeView.vue'

Vue.use(VueRouter)

const routes = [
    {
        path: '/',
        redirect: '/login',
    },

    {
        path: '/login',
        name: 'login',
        component: () => import( '../views/LoginView.vue')
    },

    {
        path: '/register',
        name: 'register',
        component: () => import( '../views/RegisterView.vue')
    },

    {
        path: '/main',
        name: 'main',
        component: () => import('../views/MainView.vue'),
        redirect: '/main/firstpage',
        children: [
            {
                path: 'firstpage',
                name: 'firstpage',
                component: () => import( '../views/FirstPageView.vue'),
            },

            {
                path: 'realtime',
                name: 'realtime',
                component: () => import('../views/RealTimeView.vue')
            },

            {
                path: 'historytime',
                name: 'historytime',
                component: () => import('../views/HistoryTimeView.vue'),
            },

            {
                path: 'comparing',
                name: 'comparing',
                component: () => import('../views/ComparingView.vue'),
            },

            {
                path:'/temp',
                name:'temp',
                component: () => import('../views/Temp.vue'),
            }

        ]
    },

    {
        path: '/person',
        name: 'person',
        component: () => import('../views/PersonView.vue'),
    },

    {
        path: '/asset',
        name: 'asset',
        component: () => import('../views/AssetView.vue'),
    },

    {
        path:'/chartmap',
        name: 'chartmap',
        component: () => import('../views/ChartMapView.vue'),
    },



]

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
})

// To check if users have token or not.
router.beforeEach((to, from, next) => {
    // If users visit login or register page, go next
    if (to.path === '/login' || to.path === '/register') {

        return next();
    }

    // If not, check if the user login or not
    const token = localStorage.getItem('token');

    // If the user do not have token, that is not login. Turn to login page
    if (!token) {
        return next('/login');
    }

    // If the user have, go everywhere
    next();
});

export default router
