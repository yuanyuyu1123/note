<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryForm" :inline="true" :model="queryParams" label-width="68px">
      <el-form-item label="标题" prop="title">
        <el-input
          v-model="queryParams.title"
          clearable
          placeholder="请输入标题"
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button icon="el-icon-search" size="mini" type="primary" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['biz:note:add']"
          icon="el-icon-plus"
          plain
          size="mini"
          type="primary"
          @click="handleAdd"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['biz:note:edit']"
          :disabled="single"
          icon="el-icon-edit"
          plain
          size="mini"
          type="success"
          @click="handleUpdate"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['biz:note:remove']"
          :disabled="multiple"
          icon="el-icon-delete"
          plain
          size="mini"
          type="danger"
          @click="handleDelete"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['biz:note:export']"
          :loading="exportLoading"
          icon="el-icon-download"
          plain
          size="mini"
          type="warning"
          @click="handleExport"
        >导出
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['biz:note:list']"
          icon="el-icon-search"
          plain
          size="mini"
          type="success"
          @click="handleGetSelfNote"
        >我的
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="noteList" @selection-change="handleSelectionChange" @sort-change="sortChange">
      <el-table-column align="center" type="selection" width="55"/>
      <el-table-column align="center" label="标题" prop="title"/>
      <el-table-column align="center" label="点赞数" prop="likeNumber" sortable="custom"/>
      <el-table-column align="center" label="评论数" prop="commentNumber" sortable="custom"/>
      <el-table-column align="center" label="标签" prop="tag">
        <el-tag
          :key="t"
          v-for="t in tag"
          closable
          :disable-transitions="false"
          @close="handleClose(t)">
          {{t}}
        </el-tag>
        <el-input
          class="input-new-tag"
          v-if="inputVisible"
          v-model="inputValue"
          ref="saveTagInput"
          size="small"
          @keyup.enter.native="handleInputConfirm"
          @blur="handleInputConfirm"
        >
        </el-input>
      </el-table-column>
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作">
        <template slot-scope="scope">
          <el-button
            v-hasPermi="['biz:note:edit']"
            icon="el-icon-edit"
            size="mini"
            type="text"
            @click="handleUpdate(scope.row)"
          >修改
          </el-button>
          <el-button
            v-hasPermi="['biz:note:remove']"
            icon="el-icon-delete"
            size="mini"
            type="text"
            @click="handleDelete(scope.row)"
          >删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :limit.sync="queryParams.pageSize"
      :page.sync="queryParams.pageNum"
      :total="total"
      @pagination="getList"
    />

    <!-- 添加或修改笔记对话框 -->
    <el-dialog :title="title" :visible.sync="open" append-to-body width="1000px">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题"/>
        </el-form-item>
        <el-form-item label="笔记内容">
          <editor v-model="form.content" :min-height="500"/>
        </el-form-item>
        <el-form-item label="标签">
          <el-tag
            :key="t"
            v-for="t in form.tag"
            closable
            :disable-transitions="false"
            @close="handleClose(t)">
            {{t}}
          </el-tag>
          <el-input
            class="input-new-tag"
            v-if="inputVisible"
            v-model="inputValue"
            ref="saveTagInput"
            size="small"
            @keyup.enter.native="handleInputConfirm"
            @blur="handleInputConfirm"
          >
          </el-input>
          <el-button v-else class="button-new-tag" size="small" @click="showInput">+ New Tag</el-button>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>

  <!-- 查看笔记评论对话框 -->

</template>

<script>
import {addNote, delNote, exportNote, getNote, listNote, listSelfNote, updateNote} from "@/api/biz/note";

export default {
  name: "Note",
  data() {
    return {
      inputVisible: false,
      inputValue: '',
      // 遮罩层
      loading: true,
      // 导出遮罩层
      exportLoading: false,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 笔记表格数据
      noteList: [],
      count: 10,
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {}
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询笔记列表 */
    getList() {
      this.loading = true;
      listNote(this.queryParams).then(response => {
        this.noteList = response.rows;
        for (let note of this.noteList) {

          console.log(note)
          note.tag = JSON.parse(JSON.stringify(note.tag))||[]
          let reg = /^["|'](.*)["|']$/g;
          note.tag = note.tag.replace(reg,"$1");
          note.tag = note.tag.replace(/\"/g,"\'");
          console.log(note.tag)
        }
        this.total = response.total;
        this.loading = false;
      });
    },

    /** 查询自己的笔记列表 */
    handleGetSelfNote(){
      this.loading = true;
      listSelfNote().then(response=>{
        this.noteList = response.rows;
        for (let note of this.noteList) {
          note.tag = JSON.parse(JSON.stringify(note.tag))||[]
          let reg = /^["|'](.*)["|']$/g;
          note.tag = note.tag.replace(reg,"$1");
        }
        this.total = response.total;
        this.loading = false;
      })
    },

    handleClose(t) {
      this.form.tag.splice(this.form.tag.indexOf(t), 1);
    },

    showInput() {
      this.inputVisible = true;
      this.$nextTick(_ => {
        this.$refs.saveTagInput.$refs.input.focus();
      });
    },

    handleInputConfirm() {
      let inputValue = this.inputValue;
      if (inputValue) {
        this.form.tag.push(inputValue);
      }
      this.inputVisible = false;
      this.inputValue = '';
    },

    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        title: null,
        content: null,
        likeNumber: null,
        commentNumber: null,
        tag: [],
        createBy: null,
        createTime: null,
        updateTime: null
      };
      this.resetForm("form");
    },

    sortChange({prop}) {
      this.queryParams.orderByColumn = prop;
      //console.log(this.queryParams.orderByColumn)
      if (this.queryParams.isAsc === "asc") {
        this.queryParams.isAsc = "desc";
      } else {
        this.queryParams.isAsc = "asc";
      }
      this.getList();
    },

    /**打开评论*/
    handleComment(){

    },

    /**处理滚动*/
    load () {

    },

    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.form.tag = [];
      this.open = true;
      this.title = "添加笔记";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getNote(id).then(response => {
        this.form = response.data;
        this.form.tag = JSON.parse(this.form.tag);
        this.open = true;
        this.title = "修改笔记";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.form.tag = JSON.stringify(this.form.tag)
          if (this.form.id != null) {
            updateNote(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addNote(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const titles = row.title || this.title;
      const id = row.id || this.ids;
      this.$modal.confirm('是否确认删除笔记标题为"' + titles + '"的数据项？').then(function () {
        return delNote(id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams;
      this.$modal.confirm('是否确认导出所有笔记数据项？').then(() => {
        this.exportLoading = true;
        return exportNote(queryParams);
      }).then(response => {
        this.$download.name(response.msg);
        this.exportLoading = false;
      }).catch(() => {
      });
    }
  }
};
</script>

<style scoped>
.item {
  margin-top: 10px;
  margin-right: 40px;
}

 .el-tag + .el-tag {
   margin-left: 10px;
 }
.button-new-tag {
  margin-left: 10px;
  height: 32px;
  line-height: 30px;
  padding-top: 0;
  padding-bottom: 0;
}
.input-new-tag {
  width: 90px;
  margin-left: 10px;
  vertical-align: bottom;
}
</style>
