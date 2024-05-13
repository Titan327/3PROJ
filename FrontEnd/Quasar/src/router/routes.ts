import { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [{ path: '', component: () => import('pages/IndexPage.vue') }],
  },
  {
    path: '/login',
    component: () => import('layouts/EmptyLayout.vue'),
    children: [{ path: '', component: () => import('pages/LoginPage.vue') }],
  },
  {
    path: '/register',
    component: () => import('layouts/EmptyLayout.vue'),
    children: [{ path: '', component: () => import('pages/RegisterPage.vue') }],
  },
  {
    path: '/reset-pwd',
    component: () => import('layouts/EmptyLayout.vue'),
    children: [{ path: '', component: () => import('pages/ResetPwdPage.vue') }],
  },
  {
    path: '/chat',
    component: () => import('layouts/EmptyLayout.vue'),
    children: [{ path: '', component: () => import('pages/ChatPage.vue') }],
  },
  {
    path: '/groups',
    component: () => import('layouts/MainLayout.vue'),
    children: [{ path: '', component: () => import('pages/GroupListPage.vue') }],
  },
  {
    path: '/notifications',
    component: () => import('layouts/MainLayout.vue'),
    children: [{ path: '', component: () => import('pages/NotificationsPage.vue') }],
  },
  {
    path: '/groups/:id',
    component: () => import('layouts/MainLayout.vue'),
    children: [{ path: '', component: () => import('pages/ConsultGroupPage.vue') }],
  },
  {
    path: '/groups/:id/transactions/:transactionId',
    component: () => import('layouts/MainLayout.vue'),
    children: [{ path: '', component: () => import('pages/ConsultGroupPage.vue') }],
  },
  {
    path: '/groups/:id/private-chat/:user2id',
    component: () => import('layouts/MainLayout.vue'),
    children: [{ path: '', component: () => import('pages/ConsultGroupPage.vue') }],
  },
  {
    path: '/user-data',
    component: () => import('layouts/MainLayout.vue'),
    children: [{ path: '', component: () => import('pages/UserDataPage.vue') }],
  },
  {
    path: '/statistics',
    component: () => import('layouts/MainLayout.vue'),
    children: [{ path: '', component: () => import('pages/UserStatisticsPage.vue') }],
  },
  {
    path: '/join-group/:token',
    component: () => import('layouts/MainLayout.vue'),
    children: [{ path: '', component: () => import('pages/JoinGroupPage.vue') }],
  },
  {
    path: '/oauth',
    component: () => import('layouts/EmptyLayout.vue'),
    children: [{ path: '', component: () => import('pages/OauthTest.vue') }],
  },

  // Always leave this as last one,
  {
    path: '/404',
    component: () => import('pages/ErrorNotFound.vue'),
  },
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue'),
  },
];

export default routes;
