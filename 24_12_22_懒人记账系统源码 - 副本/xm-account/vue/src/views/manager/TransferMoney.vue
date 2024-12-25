<template>
  <div>
    <div class="search">
      <el-date-picker placeholder="请选择开始日期查询" type="date" format="yyyy-MM-dd" value-format="yyyy-MM-dd" v-model="startTime" style="width: 200px;"></el-date-picker>
      <el-date-picker placeholder="请选择结束日期查询" type="date" format="yyyy-MM-dd" value-format="yyyy-MM-dd" v-model="endTime" style="width: 200px; margin-left: 10px"></el-date-picker>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>

    <div class="operation">
      <el-button type="primary" plain @click="handleAdd">发起转账</el-button>
      <el-button type="danger" plain @click="delBatch">批量删除</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" strip @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="draweeName" label="用户"></el-table-column>
        <el-table-column prop="payeeName" label="目标账户"></el-table-column>
        <el-table-column prop="price" label="金额"></el-table-column>
        <el-table-column prop="remark" label="备注"></el-table-column>
        <el-table-column prop="time" label="时间"></el-table-column>
        <el-table-column prop="status" label="转账状态"></el-table-column>
        <el-table-column label="操作" align="center" width="180">
          <template v-slot="scope">
            <el-button size="mini" type="primary" plain @click="handleEdit(scope.row)" :disabled="scope.row.status !== '待处理'">编辑</el-button>
            <el-button size="mini" type="danger" plain @click="del(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

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

    <el-dialog title="转账记录" :visible.sync="fromVisible" width="50%" :close-on-click-modal="false" destroy-on-close>
      <div v-if="!flag">
        <div class="search">
          <el-input placeholder="请输入手机号查询" style="width: 200px; margin-left: 10px" v-model="searchPhone"></el-input>
          <el-input placeholder="请输入邮箱查询" style="width: 200px; margin-left: 10px" v-model="searchEmail"></el-input>
          <el-button type="info" plain style="margin-left: 10px" @click="loadUser()">查询</el-button>
        </div>
        <div style="margin-top: 20px">
          <el-table :data="tableUserData" strip>
            <el-table-column prop="name" label="姓名"></el-table-column>
            <el-table-column prop="phone" label="电话"></el-table-column>
            <el-table-column prop="email" label="邮箱"></el-table-column>
            <el-table-column label="操作" align="center" width="180">
              <template v-slot="scope">
                <el-button size="mini" type="primary" plain @click="selectUser(scope.row)" :disabled="user.id === scope.row.id">打款</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

      </div>

      <el-form v-else :model="form" label-width="100px" style="padding-right: 50px" :rules="rules" ref="formRef">
        <el-form-item label="收款方" prop="payeeId">
          <el-input v-model="form.payeeName" placeholder="请输入收款方" disabled></el-input>
        </el-form-item>
        <el-form-item label="金额" prop="price">
          <el-input-number :min="1" :max="100000" v-model="form.price" placeholder="请输入金额"></el-input-number>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" type="textarea" rows="3"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>


  </div>
</template>
<script>
export default {
  name: "TransferMoney",
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      name: null,
      searchPhone: null,
      searchEmail: null,
      tableUserData: [],
      fromVisible: false,
      startTime: null,
      endTime: null,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
      },
      flag: false,
      ids: [],
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    selectUser(row) {
      this.form.payeeId = row.id
      this.form.payeeName = row.name
      this.flag = true
    },
    loadUser() {  // 分页查询
      if (this.searchPhone === null && this.searchEmail === null) {
        this.$message.info("请输入手机号或者邮箱查询用户")
        return
      }
      this.$request.get('/user/selectAll', {
        params: {
          phone: this.searchPhone,
          email: this.searchEmail
        }
      }).then(res => {
        if (res.code === '200') {
          this.tableUserData = res.data
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
      this.flag = true
      this.fromVisible = true   // 打开弹窗
    },
    save() {   // 保存按钮触发的逻辑  它会触发新增或者更新
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.$request({
            url: this.form.id ? '/transferMoney/update' : '/transferMoney/add',
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
        this.$request.delete('/transferMoney/delete/' + id).then(res => {
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
      this.ids = rows.map(v => v.id)
    },
    delBatch() {   // 批量删除
      if (!this.ids.length) {
        this.$message.warning('请选择数据')
        return
      }
      this.$confirm('您确定批量删除这些数据吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/transferMoney/delete/batch', {data: this.ids}).then(res => {
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
      this.$request.get('/transferMoney/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          draweeId: this.user.id,
          startTime: this.startTime,
          endTime: this.endTime
        }
      }).then(res => {
        if (res.code === '200') {
          this.tableData = res.data?.list
          this.total = res.data?.total
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    reset() {
      this.startTime = null
      this.endTime = null
      this.load(1)
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
  }
}
</script>

<style scoped>

</style>