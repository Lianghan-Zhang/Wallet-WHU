<template>
  <div>
    <div class="search">
      <el-input placeholder="请选择账单类型查询" style="width: 200px; margin-right: 10px" v-model="type"></el-input>
      <el-input placeholder="请选择账单分类查询" style="width: 200px; margin-right: 10px" v-model="category"></el-input>
      <el-date-picker value-format="yyyy-MM-dd" format="yyyy-MM-dd" v-model="start" placeholder="请选择开始日期查询" style="width: 200px; margin-right: 10px"></el-date-picker>
      <el-date-picker value-format="yyyy-MM-dd" format="yyyy-MM-dd" v-model="end" placeholder="请选择结束日期查询" style="width: 200px;"></el-date-picker>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>

    <div class="operation">
      <el-button type="primary" plain @click="handleAdd">记一笔</el-button>
      <el-button type="danger" plain @click="delBatch">批量删除</el-button>
      <el-button type="info" plain @click="exportBatch">批量导出</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" strip @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="id" label="序号" width="70" align="center" sortable></el-table-column>
        <el-table-column prop="type" label="账单类型"></el-table-column>
        <el-table-column prop="category" label="账单分类"></el-table-column>
        <el-table-column prop="payType" label="账户类型"></el-table-column>
        <el-table-column prop="money" label="金额"></el-table-column>
        <el-table-column prop="comment" label="备注"></el-table-column>
        <el-table-column prop="time" label="时间"></el-table-column>
        <el-table-column prop="userName" label="用户"></el-table-column>
        <el-table-column label="操作" align="center" width="180">
          <template v-slot="scope">
            <el-button size="mini" type="primary" plain @click="handleEdit(scope.row)">编辑</el-button>
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


    <el-dialog title="信息" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="form" label-width="100px" style="padding-right: 50px" :rules="rules" ref="formRef">
        <el-form-item label="账单类型" prop="type">
          <el-select style="width: 100%" v-model="form.type" @change="getCategoryList" :disabled="form.id">
            <el-option value="支出"></el-option>
            <el-option value="收入"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="账单分类" prop="category">
          <el-select style="width: 100%" v-model="form.category">
            <el-option v-for="item in categoryList" :key="item.id" :value="item.name"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="账户类型" prop="payType">
          <el-select v-model="form.payType" style="width: 100%" placeholder="账户">
            <el-option value="支付宝"></el-option>
            <el-option value="微信支付"></el-option>
            <el-option value="银行卡"></el-option>
            <el-option value="现金"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="金额" prop="money">
          <el-input v-model="form.money" placeholder="金额" :disabled="form.id" :min="1" ></el-input>
        </el-form-item>
        <el-form-item label="备注" prop="comment">
          <el-input type="textarea" v-model="form.comment" placeholder="备注"></el-input>
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
  name: "Bill",
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      type: null,
      category: null,
      start: null,
      end: null,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        type: [
          {required: true, message: '请选择账单类型', trigger: 'blur'},
        ],
        category: [
          {required: true, message: '请选择账单分类', trigger: 'blur'},
        ],
        payType: [
          {required: true, message: '请选择账户类型', trigger: 'blur'},
        ],
        money: [
          {required: true, message: '请输入金额', trigger: 'blur'},
          { pattern: /^(([1-9]{1}\d*)|([0]{1}))(\.(\d){1,2})?$/, message: '金额只能输入数字，最多2位小数', trigger: 'blur' }
        ]
      },
      ids: [],
      categoryList: []
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    exportBatch() {
      let url = this.$baseUrl + '/bill/export'
      if (this.user.role === 'USER') {
        url += '?userId=' + this.user.id
      }
      window.open(url)
    },
    getCategoryList() {
      if (this.form.category) {
        this.form.category = ''  // 先清空账单分类  再选择
      }
      this.$request.get('/category/selectAll', {
        params: { type: this.form.type }
      }).then(res => {
        this.categoryList = res.data || []
      })
    },
    handleAdd() {   // 新增数据
      this.form = {}  // 新增数据的时候清空数据
      this.fromVisible = true   // 打开弹窗
    },
    handleEdit(row) {   // 编辑数据
      this.form = JSON.parse(JSON.stringify(row))  // 给form对象赋值  注意要深拷贝数据
      this.$request.get('/category/selectAll', {
        params: { type: this.form.type }
      }).then(res => {
        this.categoryList = res.data || []
      })
      this.fromVisible = true   // 打开弹窗
    },
    save() {   // 保存按钮触发的逻辑  它会触发新增或者更新
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.$request({
            url: this.form.id ? '/bill/update' : '/bill/add',
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
        this.$request.delete('/bill/delete/' + id).then(res => {
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
        this.$request.delete('/bill/delete/batch', {data: this.ids}).then(res => {
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
      this.$request.get('/bill/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          type: this.type,
          category: this.category,
          start: this.start,
          end: this.end,
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    reset() {
      this.type = null
      this.category = null
      this.start = null
      this.end = null
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
