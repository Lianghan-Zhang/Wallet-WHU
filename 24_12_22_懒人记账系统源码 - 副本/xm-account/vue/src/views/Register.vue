<template>
  <div class="container">
    <div style="width: 350px; padding: 30px; background-color: rgba(255, 255, 255, .9); border-radius: 5px;">
      <div style="text-align: center; font-size: 24px; margin-bottom: 40px; color: #000">欢迎注册</div>
      <el-form :model="form" :rules="rules" ref="registerForm">
        <el-form-item prop="username">
          <el-input prefix-icon="el-icon-user" placeholder="请输入账号" v-model="form.username"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input prefix-icon="el-icon-lock" placeholder="请输入密码" show-password  v-model="form.password"></el-input>
        </el-form-item>
        <el-form-item prop="confirmPass">
          <el-input prefix-icon="el-icon-lock" placeholder="请确认密码" show-password  v-model="form.confirmPass"></el-input>
        </el-form-item>
        <el-form-item prop="email">
          <el-input prefix-icon="el-icon-message" placeholder="请输入邮箱" v-model="form.email"></el-input>
        </el-form-item>
        <el-form-item prop="phone">
          <el-input prefix-icon="el-icon-phone" placeholder="请输入手机号" v-model="form.phone"></el-input>
        </el-form-item>
        <el-form-item prop="ssn">
          <el-input prefix-icon="el-icon-user-solid" placeholder="请输入SSN" v-model="form.ssn"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button style="width: 100%; background-color: #009788; border-color: #009788; color: white" @click="register">注 册</el-button>
        </el-form-item>
        <div style="display: flex; align-items: center">
          <div style="flex: 1"></div>
          <div style="flex: 1; text-align: right">
            已有账号？请 <a href="/login">登录</a>
          </div>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: "Register",
  data() {
    // 验证码校验
    const validatePassword = (rule, confirmPass, callback) => {
      if (confirmPass === '') {
        callback(new Error('请确认密码'))
      } else if (confirmPass !== this.form.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    return {
      form: {
        role: 'USER',
        password: '',
        confirmPass: '',
        email: '',
        phone: '',
        ssn: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入账号', trigger: 'blur' },
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
        ],
        confirmPass: [
          {required: true, message: '请确认密码', trigger: 'blur'},
          { validator: validatePassword, trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱', trigger: ['blur', 'change'] }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3456789]\d{9}$/, message: '请输入正确的手机号', trigger: ['blur', 'change'] }
        ],
        ssn: [
          { required: true, message: '请输入SSN', trigger: 'blur' },
          { pattern: /^[0-9]{3}-[0-9]{2}-[0-9]{4}$/, message: '请输入正确的SSN', trigger: ['blur', 'change'] }
        ]
      }
    }
  },
  created() {

  },
  methods: {
    validateConfirmPass(rule, value, callback){
      if(value !== this.form.password){
        callback(new Error('两次输入的密码不一致'))
      }else{
        callback()
      }
    },

    register() {
      this.$refs['formRef'].validate((valid) => {
        if (valid) {
          // 验证通过
          this.$request.post('/register', this.form).then(res => {
            if (res.code === '200') {
              this.$router.push('/login')  // 跳转登录页面
              this.$message.success('注册成功')
            } else {
              this.$message.error(res.msg)
            }
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.container {
  height: 100vh;
  overflow: hidden;
  background-image: url("@/assets/imgs/bg1.jpg");
  background-size: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
}
a {
  color: #2a60c9;
}
</style>