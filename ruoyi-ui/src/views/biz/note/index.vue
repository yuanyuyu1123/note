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
          v-hasPermi="['biz:note:list']"
          icon="el-icon-search"
          plain
          size="mini"
          type="success"
          @click="handleGetSelfNote"
        >我的
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['biz:note:list']"
          icon="el-icon-search"
          plain
          size="mini"
          type="success"
          @click="handleGetAllNote"
        >全部
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="noteList" @selection-change="handleSelectionChange" @sort-change="sortChange">
      <el-table-column align="center" type="selection" width="55"/>
      <el-table-column align="center" label="标题" prop="title"/>
      <el-table-column align="center" label="点赞数" prop="likeNumber" sortable="custom"/>
      <el-table-column align="center" label="评论数" prop="commentNumber" sortable="custom"/>
      <el-table-column prop="tag" label="标签" align="center">
        <template slot-scope="scope">
          <el-tag
            :key="t"
            v-for="t in scope.row.tag"
            :disable-transitions="false"
            @close="handleClose(t)">
            {{t}}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作">
          <template slot-scope="scope">
            <el-button
              v-hasPermi="['biz:note:edit']"
              icon="el-icon-edit"
              size="mini"
              type="text"
              v-show="me"
              @click="handleUpdate(scope.row)"
            >修改
            </el-button>
            <el-button
              v-hasPermi="['biz:note:remove']"
              icon="el-icon-delete"
              size="mini"
              type="text"
              v-show="me"
              @click="handleDelete(scope.row)"
            >删除
            </el-button>
            <el-button
              v-hasPermi="['biz:note:list']"
              icon="el-icon-view"
              size="mini"
              type="text"
              @click="handleLook(scope.row)"
            >查看
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

        <el-form-item label="笔记内容">
          <editor v-model="form.content" :min-height="500"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

  <!-- 查看笔记评论对话框 -->
  <el-dialog :title="title" :visible.sync="look" append-to-body width="1000px">
    <el-form ref="form" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title" disabled/>
      </el-form-item>
      <el-form-item label="标签">
        <el-tag
          :key="t"
          v-for="t in form.tag">
          {{t}}
        </el-tag>
      </el-form-item>
      <el-form-item label="赞">
        <el-button  icon="el-icon-star-off" @click="handleLike" circle v-show="like1" plain></el-button>
        <el-button  icon="el-icon-star-on" @click="handleLike" circle v-show="like2" type="danger"></el-button>
      </el-form-item>

      <el-form-item label="笔记内容">
        <editor v-model="form.content" :min-height="500"/>
      </el-form-item>

      <el-form-item label="评论">
        <template>
          <div>
            <div v-clickoutside="hideReplyBtn" @click="inputFocus" class="my-reply">
              <el-avatar class="header-img" :size="40" :src="myHeader"></el-avatar>
              <div class="reply-info" >
                <div
                  tabindex="0"
                  contenteditable="true"
                  id="replyInput"
                  spellcheck="false"
                  placeholder="输入评论..."
                  class="reply-input"
                  @focus="showReplyBtn"
                  @input="onDivInput($event)"
                >
                </div>
              </div>
              <div class="reply-btn-box" v-show="btnShow">
                <el-button class="reply-btn" size="medium" @click="sendComment" type="primary">发表评论</el-button>
              </div>
            </div>
            <div v-for="(item,i) in comments" :key="i" class="author-title reply-father">
              <el-avatar class="header-img" :size="40" :src="item.headImg"></el-avatar>
              <div class="author-info">
                <span class="author-name">{{item.name}}</span>
                <span class="author-time">{{item.time}}</span>
              </div>
              <div class="icon-btn">
                <span @click="showReplyInput(i,item.name,item.id)"><i class="iconfont el-icon-s-comment"></i>{{item.commentNum}}</span>
                <i class="iconfont el-icon-caret-top"></i>{{item.like}}
              </div>
              <div class="talk-box">
                <p>
                  <span class="reply">{{item.comment}}</span>
                </p>
              </div>
              <div class="reply-box">
                <div v-for="(reply,j) in item.reply" :key="j" class="author-title">
                  <el-avatar class="header-img" :size="40" :src="reply.fromHeadImg"></el-avatar>
                  <div class="author-info">
                    <span class="author-name">{{reply.from}}</span>
                    <span class="author-time">{{reply.time}}</span>
                  </div>
                  <div class="icon-btn">
                    <span @click="showReplyInput(i,reply.from,reply.id)"><i class="iconfont el-icon-s-comment"></i>{{reply.commentNum}}</span>
                    <i class="iconfont el-icon-caret-top"></i>{{reply.like}}
                  </div>
                  <div class="talk-box">
                    <p>
                      <span>回复 {{reply.to}}:</span>
                      <span class="reply">{{reply.comment}}</span>
                    </p>
                  </div>
                  <div class="reply-box">

                  </div>
                </div>
              </div>
              <div  v-show="_inputShow(i)" class="my-reply my-comment-reply">
                <el-avatar class="header-img" :size="40" :src="myHeader"></el-avatar>
                <div class="reply-info" >
                  <div tabindex="0" contenteditable="true" spellcheck="false" placeholder="输入评论..."   @input="onDivInput($event)"  class="reply-input reply-comment-input"></div>
                </div>
                <div class=" reply-btn-box">
                  <el-button class="reply-btn" size="medium" @click="sendCommentReply(i,j)" type="primary">发表评论</el-button>
                </div>
              </div>
            </div>
          </div>
        </template>
      </el-form-item>
    </el-form>
  </el-dialog>
  </div>

</template>

<script>
import {addNote, delNote, exportNote, getNote, getUser, listNote, listSelfNote, updateNote} from "@/api/biz/note";
import {getLike,listLike,addLike,delLike} from "@/api/biz/like";

const clickoutside = {
  // 初始化指令
  bind(el, binding, vnode) {
    function documentHandler(e) {
      // 这里判断点击的元素是否是本身，是本身，则返回
      if (el.contains(e.target)) {
        return false;
      }
      // 判断指令中是否绑定了函数
      if (binding.expression) {
        // 如果绑定了函数 则调用那个函数，此处binding.value就是handleClose方法
        binding.value(e);
      }
    }
    // 给当前元素绑定个私有变量，方便在unbind中可以解除事件监听
    el.vueClickOutside = documentHandler;
    document.addEventListener('click', documentHandler);
  },
  update() {},
  unbind(el, binding) {
    // 解除事件监听
    document.removeEventListener('click', el.vueClickOutside);
    delete el.vueClickOutside;
  },
};

export default {
  name: "Note",
  data() {
    return {
      me: false,
      like1: true,
      like2: false,
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
      look: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {},
      btnShow: false,
      index:'0',
      replyComment:'',
      myName:'Lana Del Rey',
      myHeader:'https://ae01.alicdn.com/kf/Hd60a3f7c06fd47ae85624badd32ce54dv.jpg',
      myId:19870621,
      to:'',
      toId:-1,
      comments:[
        {
          name:'Lana Del Rey',
          id:19870621,
          headImg:'https://ae01.alicdn.com/kf/Hd60a3f7c06fd47ae85624badd32ce54dv.jpg',
          comment:'我发布一张新专辑Norman Fucking Rockwell,大家快来听啊',
          time:'2019年9月16日 18:43',
          commentNum:2,
          like:15,
          inputShow:false,
          reply:[
            {
              from:'Taylor Swift',
              fromId:19891221,
              fromHeadImg:'https://ae01.alicdn.com/kf/H94c78935ffa64e7e977544d19ecebf06L.jpg',
              to:'Lana Del Rey',
              toId:19870621,
              comment:'我很喜欢你的新专辑！！',
              time:'2019年9月16日 18:43',
              commentNum:1,
              like:15,
              inputShow:false
            },
            {
              from:'Ariana Grande',
              fromId:1123,
              fromHeadImg:'https://ae01.alicdn.com/kf/Hf6c0b4a7428b4edf866a9fbab75568e6U.jpg',
              to:'Lana Del Rey',
              toId:19870621,
              comment:'别忘记宣传我们的合作单曲啊',
              time:'2019年9月16日 18:43',
              commentNum:0,
              like:5,
              inputShow:false

            }
          ]
        },
        {
          name:'Taylor Swift',
          id:19891221,
          headImg:'https://ae01.alicdn.com/kf/H94c78935ffa64e7e977544d19ecebf06L.jpg',
          comment:'我发行了我的新专辑Lover',
          time:'2019年9月16日 18:43',
          commentNum:1,
          like:5,
          inputShow:false,
          reply:[
            {
              from:'Lana Del Rey',
              fromId:19870621,
              fromHeadImg:'https://ae01.alicdn.com/kf/Hd60a3f7c06fd47ae85624badd32ce54dv.jpg',
              to:'Taylor Swift',
              toId:19891221,
              comment:'新专辑和speak now 一样棒！',
              time:'2019年9月16日 18:43',
              commentNum:25,
              like:5,
              inputShow:false

            }
          ]
        },
        {
          name:'Norman Fucking Rockwell',
          id:20190830,
          headImg:'https://ae01.alicdn.com/kf/Hdd856ae4c81545d2b51fa0c209f7aa28Z.jpg',
          comment:'Plz buy Norman Fucking Rockwell on everywhere',
          time:'2019年9月16日 18:43',
          commentNum:0,
          like:5,
          inputShow:false,
          reply:[]
        },
      ]
    };
  },
  created() {
    this.getList();
  },
  directives: {clickoutside},
  methods: {

    /** 查询笔记列表 */
    getList() {
      this.loading = true;
      this.me = false
      listNote(this.queryParams).then(response => {
        this.noteList = response.rows;
        for (let note of this.noteList) {
          note.tag = JSON.parse(note.tag) || []
        }
        this.total = response.total;
        this.loading = false;
      });
    },

    /** 查询自己的笔记列表 */
    handleGetSelfNote(){
      this.loading = true;
      this.me = true
      listSelfNote().then(response=>{
        this.noteList = response.rows;
        for (let note of this.noteList) {
          note.tag = JSON.parse(note.tag) || []
        }
        this.total = response.total;
        this.loading = false;
      })
    },

    /** 查询所有的笔记列表 */
    handleGetAllNote(){
      this.getList()
    },

    /** 查看笔记详情 */
    handleLook(row){
      this.reset();
      const id = row.id || this.ids
      this.form.id = id
      listLike(this.form).then(reponse =>{
        if(reponse.total === 1){
          this.like1 = false
          this.like2 = true
        }else {
          this.like1 = true
          this.like2 = false
        }
      })
      getUser().then(response =>{
        this.myId = response.data.userId
        this.myName =response.data.nickName
        this.myHeader = response.data.avatar
      })
      getNote(id).then(response => {
        this.form = response.data;
        if(this.form.comments!==""&&this.form.comments!==null){
          this.comments = JSON.parse(this.form.comments);
        }
        this.form.tag = JSON.parse(this.form.tag);
        this.look = true;
        this.title = "笔记";
      });
    },

    /**处理点赞事件*/
    handleLike(){
      if(this.like1){
        addLike(this.form.id).then(response=>{
          this.like1 = false
          this.like2 = true
        })
      }else {
        delLike(this.form.id).then(reponse=>{
          this.like1 = true
          this.like2 = false
      })
      }
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
      if (inputValue&&inputValue.length<10&&this.form.tag.length<4) {
        this.form.tag.push(inputValue);
      }else {
        this.$message("最多有四个标签,每个标签长度不大于10!")
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
          this.form.comments = JSON.stringify(this.comments)
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
    },
    inputFocus(){
      var replyInput = document.getElementById('replyInput');
      replyInput.style.padding= "8px 8px"
      replyInput.style.border ="2px solid blue"
      replyInput.focus()
    },
    showReplyBtn(){
      this.btnShow = true
    },
    hideReplyBtn(){
      this.btnShow = false
      replyInput.style.padding= "10px"
      replyInput.style.border ="none"
    },
    showReplyInput(i,name,id){
      this.comments[this.index].inputShow = false
      this.index =i
      this.comments[i].inputShow = true
      this.to = name
      this.toId = id
    },
    _inputShow(i){
      return this.comments[i].inputShow
    },
    sendComment(){
      if(!this.replyComment){
        this.$message({
          showClose: true,
          type:'warning',
          message:'评论不能为空'
        })
      }else{
        let a ={}
        let input =  document.getElementById('replyInput')
        let timeNow = new Date().getTime();
        let time= this.dateStr(timeNow);
        a.name= this.myName
        a.comment =this.replyComment
        a.headImg = this.myHeader
        a.time = time
        a.commentNum = 0
        a.like = 0
        this.comments.push(a)
        this.replyComment = ''
        input.innerHTML = '';

      }
    },
    sendCommentReply(i,j){
      if(!this.replyComment){
        this.$message({
          showClose: true,
          type:'warning',
          message:'评论不能为空'
        })
      }else{
        let a ={}
        let timeNow = new Date().getTime();
        let time= this.dateStr(timeNow);
        a.from= this.myName
        a.to = this.to
        a.fromHeadImg = this.myHeader
        a.comment =this.replyComment
        a.time = time
        a.commentNum = 0
        a.like = 0
        this.comments[i].reply.push(a)
        this.replyComment = ''
        document.getElementsByClassName("reply-comment-input")[i].innerHTML = ""
      }
    },
    onDivInput: function(e) {
      this.replyComment = e.target.innerHTML;
    },
    dateStr(date){
      //获取js 时间戳
      var time=new Date().getTime();
      //去掉 js 时间戳后三位，与php 时间戳保持一致
      time=parseInt((time-date)/1000);
      //存储转换值
      var s;
      if(time<60*10){//十分钟内
        return '刚刚';
      }else if((time<60*60)&&(time>=60*10)){
        //超过十分钟少于1小时
        s = Math.floor(time/60);
        return  s+"分钟前";
      }else if((time<60*60*24)&&(time>=60*60)){
        //超过1小时少于24小时
        s = Math.floor(time/60/60);
        return  s+"小时前";
      }else if((time<60*60*24*30)&&(time>=60*60*24)){
        //超过1天少于30天内
        s = Math.floor(time/60/60/24);
        return s+"天前";
      }else{
        //超过30天ddd
        var date= new Date(parseInt(date));
        return date.getFullYear()+"/"+(date.getMonth()+1)+"/"+date.getDate();
      }
    }
  },
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

.my-reply {
  padding: 10px;
  background-color: #fafbfc;
}
.my-reply .header-img {
  display: inline-block;
  vertical-align: top;
}
.my-reply .reply-info {
  display: inline-block;
  margin-left: 5px;
  width: 90%;
}
@media screen and (max-width: 1200px) {
  .my-reply .reply-info {
    width: 80%;
  }
}
.my-reply .reply-info .reply-input {
  min-height: 20px;
  line-height: 22px;
  padding: 10px 10px;
  color: #ccc;
  background-color: #fff;
  border-radius: 5px;
}
.my-reply .reply-info .reply-input:empty:before {
  content: attr(placeholder);
}
.my-reply .reply-info .reply-input:focus:before {
  content: none;
}
.my-reply .reply-info .reply-input:focus {
  padding: 8px 8px;
  border: 2px solid #00f;
  box-shadow: none;
  outline: none;
}
.my-reply .reply-btn-box {
  height: 25px;
  margin: 10px 0;
}
.my-reply .reply-btn-box .reply-btn {
  position: relative;
  float: right;
  margin-right: 15px;
}
.my-comment-reply {
  margin-left: 50px;
}
.my-comment-reply .reply-input {
  width: flex;
}
.author-title:not(:last-child) {
  border-bottom: 1px solid rgba(178,186,194,0.3);
}
.author-title {
  padding: 10px;
}
.author-title .header-img {
  display: inline-block;
  vertical-align: top;
}
.author-title .author-info {
  display: inline-block;
  margin-left: 5px;
  width: 60%;
  height: 40px;
  line-height: 20px;
}
.author-title .author-info >span {
  display: block;
  cursor: pointer;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
.author-title .author-info .author-name {
  color: #000;
  font-size: 18px;
  font-weight: bold;
}
.author-title .author-info .author-time {
  font-size: 14px;
}
.author-title .icon-btn {
  width: 30%;
  padding: 0 !important;
  float: right;
}
@media screen and (max-width: 1200px) {
  .author-title .icon-btn {
    width: 20%;
    padding: 7px;
  }
}
.author-title .icon-btn >span {
  cursor: pointer;
}
.author-title .icon-btn .iconfont {
  margin: 0 5px;
}
.author-title .talk-box {
  margin: 0 50px;
}
.author-title .talk-box >p {
  margin: 0;
}
.author-title .talk-box .reply {
  font-size: 16px;
  color: #000;
}
.author-title .reply-box {
  margin: 10px 0 0 50px;
  background-color: #efefef;
}
</style>
