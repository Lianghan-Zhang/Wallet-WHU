<template>
  <div>
    <div class="card">
      <div style="margin-bottom: 10px; display: flex; align-items: center" >
        <el-tag type="warning" v-if="plan.status === '未开始'">{{ plan.status }}</el-tag>
        <el-tag type="primary" v-if="plan.status === '进行中'">{{ plan.status }}</el-tag>
        <el-tag type="danger" v-if="plan.status === '已结束'">{{ plan.status }}</el-tag>
        <el-tag type="success" v-if="plan.status === '已完成'">{{ plan.status }}</el-tag>
        <div style="font-size: 20px; flex: 1; width: 0; margin-left: 5px" class="line1">{{ plan.name }}

          <span style="color: red; font-size: 16px; font-weight: bold; margin-left: 10px">￥{{ plan.money }}</span>

          <el-button type="primary" style="margin-left: 10px" @click="addPlanDetail(planId)">存一笔</el-button>
        </div>
        <div style="color: #666; margin-left: 20px; font-size: 13px; padding-right: 50px"><i class="el-icon-date"></i>  {{ plan.end }}</div>
      </div>
      <div style="margin-bottom: 10px">
        <el-progress :percentage="plan.percent"></el-progress>
      </div>
    </div>

    <div style="margin-top: 10px">
      <el-row :gutter="10">
        <el-col :span="6" v-for="item in tableData" :key="item.id">
          <div  class="card" style="background-color: #fff1e0; margin-bottom: 10px">
            <div style="display: flex; align-items: center;  margin-bottom: 10px">
              <div style="color: #666; flex: 1">存入 ￥{{ item.money }}</div>
              <div style="display: flex; align-items: center">累计 <span style="color: red; font-size: 20px">{{ item.sum }}</span></div>
            </div>

            <div style="display: flex; color: #666; font-size: 13px">
              <div style="flex: 1">{{ item.date }}</div>
              <div @click="del(item.id)" style="width: fit-content; cursor: pointer"><i class="el-icon-delete"></i><span>删除</span></div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <el-dialog title="存钱" :visible.sync="detailFromVisible" width="30%" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="detailForm" label-width="100px" style="padding-right: 50px" :rules="rules" ref="formRef">
        <el-form-item label="金额" prop="money">
          <el-input v-model="detailForm.money" placeholder="金额"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailFromVisible = false">取 消</el-button>
        <el-button type="primary" @click="savePlanDetail">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "PlanDetail",
  data() {
    return {
      tableData: [],
      planId: this.$route.query.planId,
      plan: {},
      detailForm: {},
      detailFromVisible: false,
      rules: {
        money: [
          { required: true, message: '请输入金额', trigger: 'blur'},
          { pattern: /^\d+$/, message: '请输入正整数', trigger: 'blur'},
        ],

      },
    }
  },
  created() {
    this.load()   // 查询计划和详情列表
  },
  methods: {
    load() {
      this.$request.get('/plan/selectById/' + this.planId).then(res => {
        this.plan = res.data || {}
      })

      this.$request.get('/planDetail/selectAll', { params: { planId: this.planId } }).then(res => {
        this.tableData = res.data || []
      })
    },
    addPlanDetail(planId) {
      this.detailFromVisible = true
      this.detailForm = { planId: planId }
    },
    savePlanDetail() {
      this.$request.post('/planDetail/add', this.detailForm).then(res => {
        if (res.code === '200') {  // 表示成功保存
          this.$message.success('操作成功')
          this.load()
          this.detailFromVisible = false
        } else {
          this.$message.error(res.msg)  // 弹出错误的信息
        }
      })
    },
    del(id) {   // 单个删除
      this.$confirm('您确定删除吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/planDetail/delete/' + id).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('操作成功')
            this.load()
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }).catch(() => {
      })
    },
  }
}
</script>

<style scoped>

</style>