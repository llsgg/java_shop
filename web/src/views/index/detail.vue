<template>
  <div class="detail">
    <Header/>
    <div class="thing-infos-view">

      <div>
        <div class="thing-img-box">
          <img :src="detailData.cover" />
        </div>

        <div class="thing-info-box">
          <div class="thing-state">
            <span class="state">商品状态</span>
            <span>已上架</span>
          </div>

          <div class="translators">
            <span class="author">商品：</span>
            <span class="name">{{detailData.title}}</span>
          </div>

          <div class="translators">
            <span class="author">价格：</span>
            <span class="price">{{detailData.price}}￥</span>
            <span class="seckillPrice ">{{detailData.seckillPrice}}￥</span>
          </div>



          <div class="translators">
            <span class="author">库存：</span>
            <span class="name">{{detailData.stockCount}}</span>
          </div>

          <div class="translators">
            <span class="collect author" @click="collect">收藏：</span>
            <span class="name">{{detailData.collectCount}}</span>
          </div>

          <div class="translators">
            <span class="author">开始时间：</span>
            <span class="name">{{formatDate(new Date(detailData.startDate), "yyyy-MM-dd HH:mm:ss")}}</span>
          </div>

          <div class="translators">
            <span class="author">结束时间：</span>
            <span class="name">{{formatDate(new Date(detailData.endDate), "yyyy-MM-dd HH:mm:ss")}}</span>
          </div>

          <div class="translators">
<!--            <span class="author">状态：</span>-->
            <span class="name">{{seckillTip}}</span>
          </div>

          <div class="row">
            <div class="form-inline">
              <img v-if="display" id="captchaImg" :src="checkImgSrc" width="130" height="32"  @click="refreshCaptcha()"/>
              <a-input v-if="display"  class="form-control" v-model:value="value" placeholder="输入验证码" />
              <button :disabled="!display" class="buy-btn" @click="getSeckillPath()">
                立即秒杀
              </button>
            </div>
          </div>

        </div>

        <div class="thing-content-view">
          <h4 class="main-tab">简介</h4>
          <div class="main-content">
            <textarea class="text">{{detailData.description}}</textarea>
          </div>
        </div>
      </div>
      <div class="recommend">
        <h3 class="main-tab">相似推荐</h3>
        <ul class="recommend-list">
          <li v-for="(item, index) in recommendData.values()" :key="index">
            <a >
              <img :src="item.cover" @click=toDetail(item.id)>
            </a>

            <div>
              <span class="author">{{item.title}}</span>
            </div>
            <div>
              <span class="author">价格：</span>
              <span class="price">{{item.price}}￥</span>
              <span class="seckillPrice ">{{item.seckillPrice}}￥</span>
            </div>

          </li>
        </ul>

      </div>


    </div>
    <Footer/>
  </div>
</template>
<script lang="ts" setup>
import {message} from "ant-design-vue";
import Header from '/@/views/index/components/header.vue'
import Footer from '/@/views/index/components/footer.vue'
import AddIcon from '/@/assets/images/add.svg';
import WantIcon from '/@/assets/images/want-read-hover.svg';
import RecommendIcon from '/@/assets/images/recommend-hover.svg';
import ShareIcon from '/@/assets/images/share-icon.svg';
import WeiboShareIcon from '/@/assets/images/wb-share.svg';
import AvatarIcon from '/@/assets/images/avatar.jpg';
import {
  detailApi,
  listApi as listThingList, seckillListApi
} from "/src/api/goods";

import {collectApi} from '/src/api/goodsCollect'
import {BASE_URL} from "/@/store/constants";
import {useRoute, useRouter} from "vue-router/dist/vue-router";
import {useUserStore} from "/@/store";
import {getFormatTime} from "/@/utils";
import Content from "/@/views/index/components/content.vue";
import {seckillApi, resultApi, seckillPathApi} from "/@/api/detail";
import {formatDate} from "/@/utils/common"

const router = useRouter()
const route = useRoute()
const userStore = useUserStore();

let checkImgSrc = ref('')
let value:string

let thingId = ref('')
let detailData = ref({})
let tabUnderLeft = ref(6)
let tabData = ref(['简介', '评论'])
let selectTabIndex = ref(0)

let commentData = ref([])
let recommendData = ref([])
let sortIndex = ref(0)
let order = ref('recent') // 默认排序最新

let commentRef = ref()

let cid = ref()
let remainSeconds = ref()
let seckillTip = ref("1")
let display = ref(false)

onMounted(()=>{
  thingId.value = route.query.id.trim()
  getThingDetail()
  refreshCaptcha()
})

interface DataItem {
  title: string;
  cover: string;
  price: string;
}
let data: DataItem[] = new Array(7).fill({ title: '', cover: '', price: '' });

const toDetail=(goodsId)=> {
  router.push({name: 'detail', query: {id: goodsId}})
  thingId.value = goodsId
  getThingDetail()
  refreshCaptcha()
}

const selectTab =(index)=> {
  selectTabIndex.value = index
  tabUnderLeft.value = 6 + 54 * index
}

const getThingDetail =()=> {
  detailApi({id: thingId.value}).then(res => {
    detailData.value = res.data.goodsVo
    detailData.value.cover = BASE_URL + '/api/upload/image/' + detailData.value.cover
    remainSeconds.value = res.data.remainSeconds
    cid = res.data.classificationId

    getRecommendThing()
    countDown()
    // console.log(remainSeconds )
  }).catch(err => {
    message.error('获取详情失败')
  })
}
let timeout
function countDown() {
  console.log(remainSeconds);
  if (remainSeconds.value > 0) {
    display.value = false;
    seckillTip.value = "秒杀倒计时： " + remainSeconds.value + "秒"
    timeout = setTimeout(function () {
        remainSeconds.value = remainSeconds.value - 1;
        countDown();
      }, 1000);

  } else if (remainSeconds.value === 0) {
    if (timeout) {
      clearTimeout(timeout);
    }
    seckillTip.value = "秒杀进行中";
    display.value = true;

  } else {
    seckillTip.value = "秒杀已结束";
    display.value = false;
    console.log(display.value);
  }
}

function refreshCaptcha() {
  console.log("验证码");
  checkImgSrc.value = BASE_URL + "/api/seckill/captcha?userId=" + userStore.user_id + "&goodsId=" + thingId.value + "&time=" + new Date();
}

const collect =()=> {
  let userId = userStore.user_id
  if (userId) {
    collectApi({goodId: thingId.value, userId: userId}).then(res => {
      message.success(res.msg)
      getThingDetail()
    }).catch(err => {
      console.log('收藏失败')
    })
  } else {
    message.warn('请先登录')
  }
}

const getSeckillPath = ()=> {
  seckillPathApi({userId: userStore.user_id, goodsId: thingId.value, captcha: value}).then(res => {
    if (res.code == 200) {
      var path = res.data;
      doSeckill(path);
    } else {
      message.warn(res.trace);
    }
  }).catch(err => {
    message.warn(err.trace);
  })
}

const doSeckill =(path)=> {
  var seckillurl = "/api/seckill/" + path + "/doSeckill";
  const userId = userStore.user_id;
  if (userId) {
    seckillApi(seckillurl, {userId: 1 * userId, goodsId: thingId.value}).then(res => {
      if (res.code == 200) {
        getResult(thingId.value);
      } else {
        console.log(res.msg);
      }
      // router.push({name: 'orderView'})
    }).catch(err => {
      message.warn(err.trace)
    })
  } else {
    message.warn('请先登录')
  }
  // router.push({name: 'confirm'})
}

function getResult(goodsId) {
  // g_showLoading();

  resultApi({"userId": userStore.user_id, "goodsId": goodsId}).then(res => {
    if (res.code == 200) {
      var result = res.data;
      if (result < 0) {
        message.warn("对不起，秒杀失败!");
      } else if (result==0) {
        setTimeout(function () {
          getResult(goodsId);
        }, 50);
      } else {
        if (confirm("恭喜您，秒杀成功！是否查看订单？")) {
          // 用户点击确认按钮时的逻辑
          router.push({ name: 'orderView' });
        } else {
            close();
        }
      }
    }

  }).catch(err => {
    message.warn(err.trace)
  })
}

function g_showLoading(){
  var idx = this.$layer.msg('处理中...', {icon: 16,shade: [0.5, '#f5f5f5'],scrollbar: false,offset: '0px', time:100000}) ;
  return idx;
}

const handleOrder =(detailData)=> {
  console.log(detailData)
  const userId = userStore.user_id
  router.push({name: 'confirm',
    query:
        {
          id: detailData.id,
          title: detailData.title,
          cover: detailData.cover,
          price: detailData.price
        }})
}
const getRecommendThing =()=> {
  console.log("cid");
  seckillListApi({c: cid}).then(res => {
    res.data.forEach((item, index) => {
      if (item.cover) {
        item.cover = BASE_URL + '/api/upload/image/' + item.cover
      }
    })
    recommendData.value = res.data.slice(0, 6);
    console.log(data);
  }).catch(err => {
    console.log(err)
  })
}
const handleDetail =(item)=> {
  // 跳转新页面
  let text = router.resolve({name: 'detail', query: {id: item.id}})
  window.open(text.href, '_blank')
}
const sendComment =()=> {
  console.log(commentRef.value)
  let text = commentRef.value.value.trim()
  console.log(text)
  if (text.length <= 0) {
    return
  }
  commentRef.value.value = ''
  let userId = userStore.user_id
  if (userId) {
    createCommentApi({content: text, thingId: thingId.value, userId: userId}).then(res => {
      getCommentList()
    }).catch(err => {
      console.log(err)
    })
  } else {
    message.warn('请先登录！')
    router.push({name: 'login'})
  }
}
const like =(commentId)=> {
  likeApi({id: commentId}).then(res => {
    getCommentList()
  }).catch(err => {
    console.log(err)
  })
}
const getCommentList =()=> {
  listThingCommentsApi({thingId: thingId.value, order: order.value}).then(res => {
    res.data.forEach(item => {
      item.commentTime = getFormatTime(item.commentTime, true)
    })
    commentData.value = res.data
  }).catch(err => {
    console.log(err)
  })
}
const sortCommentList =(sortType)=> {
  if (sortType === 'recent') {
    sortIndex.value = 0
  } else {
    sortIndex.value = 1
  }
  order.value = sortType
  getCommentList()
}

</script>
<style scoped lang="less">

.hide {
  display: none;
}

.detail-content {
  display: flex;
  flex-direction: column;
  width: 1100px;
  margin: 4px auto;
}

.flex-view {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
}

.hidden-lg {
  display: none !important;
}

.thing-infos-view {
  display: flex;
  margin: 89px 0 40px;
  overflow: hidden;

  .thing-infos {
    -webkit-box-flex: 1;
    -ms-flex: 1;
    flex: 1;
    display: flex;
  }



  .thing-img-box {
    -webkit-box-flex: 0;
    -ms-flex: 0 0 235px;
    flex: 0 0 235px;
    margin: 0 40px 0 100px;
    float: left;
    background: #0ac2b0;

    img {
      width: 200px;
      height: 186px;
      display: block;
    }
  }

  .thing-info-box {
    //background: #0F1111;
    text-align: left;
    padding: 0;
    margin: 0;
    float: left;
    width:500px;
  }

  .form-control {
    width: 40%;
    margin-left: 20px;
    //border: solid 2px rgba(0, 0, 0, .1);
  }

  .thing-state {
    height: 26px;
    line-height: 26px;
    //background: #0ac2b0;

    .state {
      font-weight: 500;
      color: #4684e2;
      background: rgba(70, 132, 226, .1);
      border-radius: 2px;
      padding: 5px 8px;
      margin-right: 16px;
    }

    span {
      font-size: 14px;
      color: #152844;
    }
  }

  .thing-name {
    //background: #0F1111;
    //line-height: 32px;
    margin: 16px 0;
    color: #0F1111!important;
    font-size: 15px!important;
    font-weight: 400!important;
    font-style: normal!important;
    text-transform: none!important;
    text-decoration: none!important;
  }

  .price {
    text-decoration: line-through;
  }

  .seckillPrice {
    color: #ff7b31;
    font-size: 20px;
    line-height: 20px;
    margin-top: 4px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .translators, .authors {
    line-height: 18px;
    font-size: 15px;
    margin: 12px 0;
    -webkit-box-align: start;
    -ms-flex-align: start;
    align-items: flex-start;
    -webkit-box-pack: start;
    -ms-flex-pack: start;
    justify-content: flex-start;

    .name {
      color: #315c9e;
      white-space: normal;
      font-size: 15px;
    }
  }

  .collect {
    -webkit-box-flex: 0;
    -ms-flex: 0 0 235px;
    flex: 0 0 235px;
    margin-left: 0px;
    color: #315c9e;

    cursor: pointer;
    position: relative;
    border-bottom: 1px solid #cedce4;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    -webkit-box-pack: justify;
    -ms-flex-pack: justify;
    justify-content: space-between;
    -webkit-box-flex: 1;
    -ms-flex: 1;
    flex: 1;
    height: 100%;
  }



  .recommend-list {
    li {
      border-bottom: 1px solid #ccc;
      padding-bottom: 10px;
      margin-bottom: 10px;
    }

    img {
      max-width: 100px; /* Adjust the maximum width of the image */
      height: auto; /* Maintain the aspect ratio */
    }
  }


  .thing-counts {
    -webkit-box-flex: 0;
    -ms-flex: 0 0 235px;
    flex: 0 0 235px;
    margin-left: 0px;
    font-size: 25px;

  }

  .pointer {
    cursor: pointer;
  }

  .count-item {
    height: 64px;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    cursor: pointer;
  }

  .count-img {
    -webkit-box-flex: 0;
    -ms-flex: 0 0 32px;
    flex: 0 0 32px;
    margin-right: 24px;
    font-size: 0;

    img {
      width: 100%;
      display: block;
    }
  }

  .count-box {
    position: relative;
    border-bottom: 1px solid #cedce4;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    -webkit-box-pack: justify;
    -ms-flex-pack: justify;
    justify-content: space-between;
    -webkit-box-flex: 1;
    -ms-flex: 1;
    flex: 1;
    height: 100%;
  }

  .count-text-box {
    font-size: 0;

    .count-title {
      color: #152844;
      font-weight: 600;
      font-size: 16px;
      line-height: 18px;
      display: block;
      height: 18px;
    }
  }

  .count-num-box {
    font-weight: 600;
    font-size: 20px;
    line-height: 24px;
    color: #152844;
  }
}

.buy-btn {
  cursor: pointer;
  display: block;
  background: #4684e2;
  border-radius: 4px;
  text-align: center;
  color: #fff;
  font-size: 14px;
  height: 36px;
  line-height: 36px;
  width: 110px;
  outline: none;
  border: none;
  margin-top: 18px;
}

.buy-btn img {
  width: 12px;
  margin-right: 2px;
  vertical-align: middle;
}

.buy-btn span {
  vertical-align: middle;
}

.buy-way {
  overflow: hidden;

  .title {
    font-weight: 600;
    font-size: 18px;
    height: 26px;
    line-height: 26px;
    color: #152844;
    margin-bottom: 12px;
  }
}

.thing-content-view {
  float: left;
  //background: #2a9a44;
  padding-bottom: 50px;
  margin-top: 20px;
  margin-left: 100px;
  width: 750px;
}

.main-content {
  -webkit-box-flex: 1;
  -ms-flex: 1;
  flex: 1;

  .text {
    color: #484848;
    font-size: 16px;
    line-height: 26px;
    padding-left: 12px;
    margin: 15px 0;
    white-space: pre-wrap;
    width: 100%;
    height: 500px;
  }
}

.main-tab {
  border-bottom: 1px solid #cedce4;
  font-size: 20px!important;
  font-weight: bold;
  //margin-bottom: 20px;
  padding-bottom: 20px;
}

.order-view {
  position: relative;
  color: #6c6c6c;
  font-size: 15px;
  line-height: 40px;

  .title {
    margin-right: 8px;
  }

  .tab {
    margin-right: 20px;
    cursor: pointer;
    color: #5f77a6;
    font-size: 16px;
    cursor: pointer;
  }

  .tab-select {
    color: #152844;
    font-weight: 600;
  }

  .tab-underline {
    position: absolute;
    bottom: 0;
    left: 84px;
    width: 16px;
    height: 4px;
    background: #4684e2;
    -webkit-transition: left .3s;
    transition: left .3s;
  }
}

.recommend {
  //background: #0ac2b0;
  -webkit-box-flex: 0;
  //-ms-flex: 0 0 235px;
  flex: 0 0 300px;
  //margin-left: 0px;

  .title {
    font-weight: 600;
    font-size: 18px;
    line-height: 26px;
    color: #152844;
    margin-bottom: 12px;
  }

  .things {
    border-top: 1px solid #cedce4;

    .thing-item {
      min-width: 255px;
      max-width: 255px;
      position: relative;
      flex: 1;
      margin-right: 20px;
      height: fit-content;
      overflow: hidden;
      margin-top: 26px;
      margin-bottom: 36px;
      padding-bottom: 24px;
      border-bottom: 1px #e1e1e1 solid;
      cursor: pointer;

      .img-view {
        //background: #f3f3f3;
        //text-align: center;
        height: 200px;
        width: 255px;
        //border: 1px #f3f3f3 solid;

        img {
          height: 200px;
          width: 186px;
          overflow: hidden;
          margin: 0 auto;
          background-size: contain;
          object-fit: contain;
        }
      }

      .info-view {
        //background: #f6f9fb;
        overflow: hidden;
        padding: 0 16px;
        .thing-name {
          line-height: 32px;
          margin-top: 12px;
          color: #0F1111!important;
          font-size: 15px!important;
          font-weight: 400!important;
          font-style: normal!important;
          text-transform: none!important;
          text-decoration: none!important;
        }

        .price {
          color: #ff7b31;
          font-size: 20px;
          line-height: 20px;
          margin-top: 4px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .translators {
          color: #6f6f6f;
          font-size: 12px;
          line-height: 14px;
          margin-top: 8px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }
    }

  }
}

.flex-view {
  display: flex;
}

.thing-comment {
  .title {
    font-weight: 600;
    font-size: 14px;
    line-height: 22px;
    height: 22px;
    color: #152844;
    margin: 24px 0 12px;
  }

  .publish {
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;

    .mine-img {
      -webkit-box-flex: 0;
      -ms-flex: 0 0 40px;
      flex: 0 0 40px;
      margin-right: 12px;
      border-radius: 50%;
      width: 40px;
      height: 40px;
    }

    .content-input {
      -webkit-box-flex: 1;
      -ms-flex: 1;
      flex: 1;
      background: #f6f9fb;
      border-radius: 4px;
      height: 32px;
      line-height: 32px;
      color: #484848;
      padding: 5px 12px;
      white-space: nowrap;
      outline: none;
      border: 0px;
    }

    .send-btn {
      margin-left: 10px;
      background: #4684e2;
      border-radius: 4px;
      -webkit-box-flex: 0;
      -ms-flex: 0 0 80px;
      flex: 0 0 80px;
      color: #fff;
      font-size: 14px;
      text-align: center;
      height: 32px;
      line-height: 32px;
      outline: none;
      border: 0px;
      cursor: pointer;
    }
  }

  .tab-view {
    -webkit-box-pack: justify;
    -ms-flex-pack: justify;
    justify-content: space-between;
    font-size: 14px;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    margin: 24px 0;

    .count-text {
      color: #484848;
      float: left;
    }

    .tab-box {
      color: #5f77a6;
      -webkit-box-align: center;
      -ms-flex-align: center;
      align-items: center;

      .tab-select {
        color: #152844;
      }

      span {
        cursor: pointer;
      }
    }

    .line {
      width: 1px;
      height: 12px;
      margin: 0 12px;
      background: #cedce4;
    }
  }
}


.comments-list {
  .comment-item {
    .flex-item {
      -webkit-box-align: center;
      -ms-flex-align: center;
      align-items: center;
      padding-top: 16px;

      .avator {
        -webkit-box-flex: 0;
        -ms-flex: 0 0 40px;
        flex: 0 0 40px;
        width: 40px;
        height: 40px;
        margin-right: 12px;
        border-radius: 50%;
        cursor: pointer;
      }

      .person {
        -webkit-box-flex: 1;
        -ms-flex: 1;
        flex: 1;
      }

      .name {
        color: #152844;
        font-weight: 600;
        font-size: 14px;
        line-height: 22px;
        height: 22px;
        cursor: pointer;
      }

      .time {
        color: #5f77a6;
        font-size: 12px;
        line-height: 16px;
        height: 16px;
        margin-top: 2px;
      }

      .float-right {
        color: #4684e2;
        font-size: 14px;
        float: right;

        span {
          margin-left: 19px;
          cursor: pointer;
        }

        .num {
          color: #152844;
          margin-left: 6px;
          cursor: auto;
        }
      }
    }
  }
}

.comment-content {
  margin-top: 8px;
  color: #484848;
  font-size: 14px;
  line-height: 22px;
  padding-bottom: 16px;
  border-bottom: 1px dashed #cedce4;
  margin-left: 52px;
  overflow: hidden;
  word-break: break-word;
}

.infinite-loading-container {
  clear: both;
  text-align: center;
}

.a-price-symbol {
  top: -0.5em;
  font-size: 12px;
}
.a-price {
  color: #0F1111;
  font-size:21px;
}
</style>
