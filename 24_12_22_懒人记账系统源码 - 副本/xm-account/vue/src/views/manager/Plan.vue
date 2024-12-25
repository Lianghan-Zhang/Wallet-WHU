<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入标题查询" style="width: 200px" v-model="name"></el-input>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>

    <div class="operation">
      <el-button type="primary" plain @click="handleAdd">新增</el-button>
    </div>

    <div style="margin-top: 10px" v-if="tableData.length">
      <div>
        <el-row :gutter="10" style="margin-bottom: 10px">
          <el-col v-for="item in tableData" :key="item.id" :span="12" style="margin-bottom: 5px">
            <div style="display: flex; align-items: center; cursor: pointer" class="card" @click="$router.push('/planDetail?planId=' + item.id)">
              <img :src="item.cover" alt="" style="width: 80px; height: 80px">
              <div style="flex: 1; margin-left: 15px">
                <div style="margin-bottom: 10px; display: flex; align-items: center" >
                  <el-tag type="warning" v-if="item.status === '未开始'">{{ item.status }}</el-tag>
                  <el-tag type="primary" v-if="item.status === '进行中'">{{ item.status }}</el-tag>
                  <el-tag type="danger" v-if="item.status === '已结束'">{{ item.status }}</el-tag>
                  <el-tag type="success" v-if="item.status === '已完成'">{{ item.status }}</el-tag>
                  <div style="font-size: 20px; flex: 1; width: 0" class="line1">{{ item.name }}</div>
                  <div style="color: #666; margin-left: 20px; font-size: 13px"><i class="el-icon-date"></i>  {{ item.end }}</div>
                </div>
                <div style="margin-bottom: 10px">
                  <el-progress :percentage="item.percent"></el-progress>
                </div>
                <div style="display: flex; align-items: center">
                  <div style="flex: 1; color: #666">
                    <span><i class="el-icon-user"></i> {{ item.userName }}</span>
                    <span style="margin-left: 10px"><i class="el-icon-date"></i> {{ item.date }}</span>
                  </div>
                  <div style="width: 50px">
                    <el-button type="primary" @click.stop="addPlanDetail(item.id)">存一笔</el-button>
                  </div>
                  <div style="flex: 1; text-align: right">
                    <el-button @click.stop="handleEdit(item)" type="text" style="font-size: 13px; color: #2a60c9"><i class="el-icon-edit"></i> 编辑</el-button>
                    <el-button @click.stop="del(item.id)" type="text" style="font-size: 13px; color: red"><i class="el-icon-delete"></i> 删除</el-button>
                  </div>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
      <div class="pagination">
        <el-pagination
            background
            @current-change="handleCurrentChange"
            :current-page="pageNum"
            :page-sizes="[5, 10, 20]"
            :page-size="pageSize"
            layout="total, prev, pager, next"
            :total="total">
        </el-pagination>
      </div>
    </div>


    <el-dialog title="信息" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="form" label-width="100px" style="padding-right: 50px" :rules="rules" ref="formRef">
        <el-form-item label="计划名称" prop="name">
          <el-input v-model="form.name" placeholder="计划名称"></el-input>
        </el-form-item>
        <el-form-item label="封面" prop="cover">
          <el-upload
              :action="$baseUrl + '/files/upload'"
              :headers="{ token: user.token }"
              list-type="picture"
              :on-success="handleImgSuccess"
          >
            <el-button type="primary">上传封面</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="总金额" prop="money">
          <el-input v-model="form.money" placeholder="总金额"></el-input>
        </el-form-item>
        <el-form-item label="开始日期" prop="start">
          <el-date-picker value-format="yyyy-MM-dd" format="yyyy-MM-dd" v-model="form.start"
                          placeholder="开始日期" style="width: 100%"></el-date-picker>
        </el-form-item>
        <el-form-item label="结束日期" prop="end">
          <el-date-picker value-format="yyyy-MM-dd" format="yyyy-MM-dd" v-model="form.end"
                          placeholder="结束日期" style="width: 100%"></el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>

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
  name: "Plan",
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      name: null,
      fromVisible: false,
      detailFromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        name: [
          {required: true, message: '请输入标题', trigger: 'blur'},
        ],
        money: [
          { required: true, message: '请输入金额', trigger: 'blur'},
          { pattern: /^\d+$/, message: '请输入正整数', trigger: 'blur'},
        ],
        start: [
          {required: true, message: '请输入开始日期', trigger: 'blur'},
        ],
        end: [
          {required: true, message: '请输入结束日期', trigger: 'blur'},
        ]
      },
      ids: [],
      detailForm: {}
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    addPlanDetail(planId) {
      this.detailFromVisible = true
      this.detailForm = { planId: planId }
    },
    savePlanDetail() {
      this.$request.post('/planDetail/add', this.detailForm).then(res => {
        if (res.code === '200') {  // 表示成功保存
          this.$message.success('操作成功')
          this.load(1)
          this.detailFromVisible = false
        } else {
          this.$message.error(res.msg)  // 弹出错误的信息
        }
      })
    },
    handleAdd() {   // 新增数据
      this.form = {}  // 新增数据的时候清空数据
      this.fromVisible = true   // 打开弹窗
    },
    handleEdit(row) {   // 编辑数据
      this.form = JSON.parse(JSON.stringify(row))  // 给form对象赋值  注意要深拷贝数据
      this.fromVisible = true   // 打开弹窗
    },
    save() {   // 保存按钮触发的逻辑  它会触发新增或者更新
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.$request({
            url: this.form.id ? '/plan/update' : '/plan/add',
            method: this.form.id ? 'PUT' : 'POST',
            data: this.form
          }).then(res => {
            if (res.code === '200') {  // 表示成功保存
              this.$message.success('保存成功')
              this.load(1)
              this.fromVisible = false
            } else {
              this.$message.error(res.msg)  // 弹出错误的信息
            }
          })
        }
      })
    },
    del(id) {   // 单个删除
      this.$confirm('您确定删除吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/plan/delete/' + id).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }).catch(() => {
      })
    },
    handleSelectionChange(rows) {   // 当前选中的所有的行数据
      this.ids = rows.map(v => v.id)   //  [1,2]
    },
    delBatch() {   // 批量删除
      if (!this.ids.length) {
        this.$message.warning('请选择数据')
        return
      }
      this.$confirm('您确定批量删除这些数据吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/plan/delete/batch', {data: this.ids}).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }).catch(() => {
      })
    },
    load(pageNum) {  // 分页查询
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/plan/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    reset() {
      this.name = null
      this.load(1)
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
    handleImgSuccess(res) {
      this.form.cover = res.data
    }
  }
}
</script>

<style scoped>

</style>
