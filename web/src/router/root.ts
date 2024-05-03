// 路由表
const constantRouterMap = [
  // ************* 前台路由 **************
  {
    path: '/',
    redirect: '/index'
  },
  {
    path: '/index',
    name: 'index',
    redirect: '/index/portal',
    component: () => import('/@/views/index/index.vue'),
    children: [
      {
        path: 'login',
        name: 'login',
        component: () => import('/@/views/index/login.vue')
      },
      {
        path: 'register',
        name: 'register',
        component: () => import('/@/views/index/register.vue')
      },
      {
        path: 'portal',
        name: 'portal',
        component: () => import('/@/views/index/portal.vue')
      },
      {
        path: 'detail',
        name: 'detail',
        component: () => import('/@/views/index/detail.vue')
      },
      {
        path: 'confirm',
        name: 'confirm',
        component: () => import('/@/views/index/confirm.vue')
      },
      {
        path: 'pay',
        name: 'pay',
        component: () => import('/@/views/index/pay.vue')
      },
      {
        path: 'search',
        name: 'search',
        component: () => import('/@/views/index/search.vue')
      },
      {
        path: 'usercenter',
        name: 'usercenter',
        redirect: '/index/usercenter/addressView',
        component: () => import('/@/views/index/usercenter.vue'),
        children: [
          {
            path: 'orderView',
            name: 'orderView',
            component: () => import('/@/views/index/user/order-view.vue')
          },
          {
            path: 'orderView',
            name: 'orderView',
            component: () => import('/@/views/index/user/order-view.vue')
          },
          {
            path: 'userInfoEditView',
            name: 'userInfoEditView',
            component: () => import('/@/views/index/user/userinfo-edit-view.vue')
          },
          {
            path: 'securityView',
            name: 'securityView',
            component: () => import('/@/views/index/user/security-view.vue')
          },
          {
            path: 'collectView',
            name: 'collectView',
            component: () => import('/@/views/index/user/collect-thing-view.vue')
          },
        ]
      }
    ]
  },
  {
    path: '/adminLogin',
    name: 'adminLogin',
    component: () => import('/@/views/admin/admin-login.vue'),
  },
  {
    path: '/admin',
    name: 'admin',
    redirect: '/admin/user',
    component: () => import('/@/views/admin/main.vue'),
    children: [
      { path: 'overview', name: 'overview', component: () => import('/@/views/admin/overview.vue') },
      { path: 'order', name: 'order', component: () => import('/@/views/admin/order.vue') },
      { path: 'thing', name: 'thing', component: () => import('/@/views/admin/thing.vue') },
      { path: 'comment', name: 'comment', component: () => import('/@/views/admin/comment.vue') },
      { path: 'user', name: 'user', component: () => import('/@/views/admin/user.vue') },
      { path: 'classification', name: 'classification', component: () => import('/@/views/admin/classification.vue') },
      { path: 'tag', name: 'tag', component: () => import('/@/views/admin/tag.vue') },
      { path: 'ad', name: 'ad', component: () => import('/@/views/admin/ad.vue') },
      { path: 'notice', name: 'notice', component: () => import('/@/views/admin/notice.vue') },
      { path: 'loginLog', name: 'loginLog', component: () => import('/@/views/admin/login-log.vue') },
      { path: 'opLog', name: 'opLog', component: () => import('/@/views/admin/op-log.vue') },
      { path: 'errorLog', name: 'errorLog', component: () => import('/@/views/admin/error-log.vue') },
    ]
  },
];

export default constantRouterMap;
