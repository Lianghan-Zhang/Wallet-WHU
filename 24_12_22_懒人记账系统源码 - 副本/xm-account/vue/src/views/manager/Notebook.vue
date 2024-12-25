<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入标题查询" style="width: 200px" v-model="title"></el-input>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>

    <div class="operation">
      <el-button type="primary" plain @click="handleAdd">新增</el-button>
      <el-button type="danger" plain @click="delBatch">批量删除</el-button>
    </div>

    <div class="table"  v-if="user.role === 'ADMIN' && tableData.length">
      <el-table :data="tableData" stripe  @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="id" label="序号" width="80" align="center" sortable></el-table-column>
        <el-table-column label="封面">
          <template v-slot="scope">
            <div style="display: flex; align-items: center">
              <el-image style="width: 50px;" v-if="scope.row.cover"
                        :src="scope.row.cover" :preview-src-list="[scope.row.cover]"></el-image>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" show-overflow-tooltip></el-table-column>
        <el-table-column label="内容">
          <template v-slot="scope">
            <el-button @click="showContent(scope.row.content)">查看内容</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="date" label="创建日期"></el-table-column>
        <el-table-column prop="userName" label="创建人"></el-table-column>

        <el-table-column label="操作" width="180" align="center">
          <template v-slot="scope">
            <el-button plain type="primary" @click="handleEdit(scope.row)" size="mini">编辑</el-button>
            <el-button plain type="danger" size="mini" @click=del(scope.row.id)>删除</el-button>
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

    <div v-else-if="tableData.length" style="margin-top: 10px">
      <el-row :gutter="10" style="margin-bottom: 10px">
        <el-col v-for="item in tableData" :key="item.id" :span="12" style="margin-bottom: 5px">
          <div style="display: flex" class="card">
            <div style="flex: 1">
              <div style="font-size: 20px; margin-bottom: 10px" class="line1">{{ item.title }}</div>
              <div style="color: #666; display: flex; align-items: center">
                <div style="flex: 1">
                  <span><i class="el-icon-user"></i> {{ item.userName }}</span>
                  <span style="margin-left: 10px"><i class="el-icon-date"></i> {{ item.date }}</span>
                </div>
                <div style="flex: 1;">
                  <el-button @click="showContent(item.content)" type="text" style="font-size: 14px"><i class="el-icon-reading"></i> 查看</el-button>
                  <el-button @click="handleEdit(item)" type="text" style="font-size: 14px; color: #2a60c9"><i class="el-icon-edit"></i> 编辑</el-button>
                  <el-button @click="del(item.id)" type="text" style="font-size: 14px; color: red"><i class="el-icon-delete"></i> 删除</el-button>
                </div>
              </div>
            </div>
            <img :src="item.cover" alt="" style="width: 120px; height: 60px">
          </div>
        </el-col>
      </el-row>

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




    <el-dialog title="信息" :visible.sync="fromVisible" width="60%" :close-on-click-modal="false" destroy-on-close>
      <el-form label-width="100px" style="padding-right: 50px" :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="title" label="标题">
          <el-input v-model="form.title" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="封面" prop="cover">
          <el-upload
              :action="$baseUrl + '/files/upload'"
              :headers="{ token: user.token }"
              list-type="picture"
              :on-success="handleCoverSuccess"
          >
            <el-button type="primary">上传封面</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item prop="content" label="内容">
          <div id="editor"></div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="信息" :visible.sync="contentVisible" width="50%" :close-on-click-modal="false" destroy-on-close>
      <div class="w-e-text">
        <div v-html="content"></div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="contentVisible = false">确 定</el-button>
      </div>
    </el-dialog>


  </div>
</template>

<script>
import E from 'wangeditor'

export default {
  name: "Notebook",
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      title: null,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        title: [
          {required: true, message: '请输入标题', trigger: 'blur'},
        ],
      },
      ids: [],
      editor: null,
      contentVisible: false,
      content: ''
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    showContent(content) {
      this.content = content
      this.contentVisible = true
    },
    handleAdd() {   // 新增数据
      this.form = {}  // 新增数据的时候清空数据
      this.setRichText()  // 创建富文本区域
      this.fromVisible = true   // 打开弹窗
    },
    handleEdit(row) {   // 编辑数据
      this.form = JSON.parse(JSON.stringify(row))  // 给form对象赋值  注意要深拷贝数据
      this.setRichText(this.form.content)  // 创建富文本区域
      this.fromVisible = true   // 打开弹窗
    },
    save() {   // 保存按钮触发的逻辑  它会触发新增或者更新
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.form.content = this.editor.txt.html()   // 获取富文本的内容
          this.$request({
            url: this.form.id ? '/notebook/update' : '/notebook/add',
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
        this.$request.delete('/notebook/delete/' + id).then(res => {
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
        this.$request.delete('/notebook/delete/batch', {data: this.ids}).then(res => {
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
      this.$request.get('/notebook/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          title: this.title,
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    reset() {
      this.title = null
      this.load(1)
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
    handleCoverSuccess(res) {
      this.form.cover = res.data
    },
    setRichText(content) {
      this.$nextTick(() => {
        this.editor = new E(`#editor`)
        this.editor.config.uploadImgServer = this.$baseUrl + '/files/editor/upload'
        this.editor.config.uploadFileName = 'file'
        this.editor.config.uploadImgHeaders = {
          token: this.user.token
        }
        this.editor.config.uploadImgParams = {
          type: 'img',
        }
        this.editor.create()  // 创建
        if (content) {  // 赋值
          this.editor.txt.html(content)
        }
      })
    },
  }
}
</script>

<style scoped>

</style>
