import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

// 解决导航栏或者底部导航tabBar中的vue-router在3.0版本以上频繁点击菜单报错的问题。
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push (location) {
  return originalPush.call(this, location).catch(err => err)
}

const routes = [
  {
    path: '/',
    name: 'Manager',
    component: () => import('../views/Manager.vue'),
    redirect: '/home',  // 重定向到主页
    children: [
      { path: '403', name: 'NoAuth', meta: { name: '无权限' }, component: () => import('../views/manager/403') },
      { path: 'home', name: 'Home', meta: { name: '系统首页' }, component: () => import('../views/manager/Home') },
      { path: 'admin', name: 'Admin', meta: { name: '管理员信息' }, component: () => import('../views/manager/Admin') },
      { path: 'adminPerson', name: 'AdminPerson', meta: { name: '个人信息' }, component: () => import('../views/manager/AdminPerson') },
      { path: 'password', name: 'Password', meta: { name: '修改密码' }, component: () => import('../views/manager/Password') },
      { path: 'notice', name: 'Notice', meta: { name: '公告信息' }, component: () => import('../views/manager/Notice') },
      { path: 'user', name: 'User', meta: { name: '用户信息' }, component: () => import('../views/manager/User') },
      { path: 'userPerson', name: 'UserPerson', meta: { name: '个人信息' }, component: () => import('../views/manager/UserPerson') },
      { path: 'ac', name: 'Ac', meta: { name: '账户信息' }, component: () => import('../views/manager/Ac') },
      { path: 'category', name: 'Category', meta: { name: '账户信息' }, component: () => import('../views/manager/Category') },
      { path: 'bill', name: 'Bill', meta: { name: '我的账单' }, component: () => import('../views/manager/Bill') },
      { path: 'notebook', name: 'Notebook', meta: { name: '记账日记' }, component: () => import('../views/manager/Notebook') },
      { path: 'plan', name: 'Plan', meta: { name: '存钱计划' }, component: () => import('../views/manager/Plan') },
      { path: 'planDetail', name: 'PlanDetail', meta: { name: '存钱计划明细' }, component: () => import('../views/manager/PlanDetail') },
      { path: 'transferMoney', name: 'TransferMoney', meta: { name: '转出记录' }, component: () => import('../views/manager/TransferMoney') },
      { path: 'transferTo', name: 'TransferTo', meta: { name: '转入记录' }, component: () => import('../views/manager/TransferTo') },
      { path: 'transferInfo', name: 'TransferInfo', meta: { name: '转账记录' }, component: () => import('../views/manager/TransferInfo') },
    ]
  },
  { path: '/login', name: 'Login', meta: { name: '登录' }, component: () => import('../views/Login.vue') },
  { path: '/register', name: 'Register', meta: { name: '注册' }, component: () => import('../views/Register.vue') },
  { path: '*', name: 'NotFound', meta: { name: '无法访问' }, component: () => import('../views/404.vue') },
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
