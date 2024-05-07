<template>
  <div class="content">
    <div class="content-left">
      <div class="left-search-item">
        <h4>商品分类</h4>
        <a-tree :tree-data="contentData.cData" :selected-keys="contentData.selectedKeys" @select="onSelect"
                style="min-height: 220px;">
        </a-tree>
      </div>
    </div>
    <div class="content-right">

      <div class="top-select-view flex-view">
        <h4>商品列表</h4>
      </div>
      <a-spin :spinning="contentData.loading" style="min-height: 200px;">
        <div class="pc-thing-list flex-view">
          <div v-for="item in contentData.pageData" :key="item.id" @click="handleDetail(item)"
               class="thing-item item-column-3"><!---->
            <div class="img-view">
              <img :src="item.cover">
            </div>
            <div class="info-view">
              <h3 class="thing-name">{{ item.title.substring(0, 12) }}</h3>
              <span>
                <span class="a-price-symbol">¥</span>
                <span class="a-price">{{ item.price }}</span>
              </span>
            </div>
          </div>
          <div v-if="contentData.pageData.length <= 0 && !contentData.loading" class="no-data" style="">暂无数据</div>
        </div>
      </a-spin>
      <div class="page-view" style="">
        <a-pagination v-model="contentData.page" size="small" @change="changePage"
                      :defaultPageSize="contentData.pageSize" :total="contentData.total" :showSizeChanger="false"/>
      </div>
    </div>
  </div>
</template>

<script setup>
import {listApi as listClassificationList} from '/@/api/classification'
import {listApi as listTagList} from '/@/api/tag'
import {listApi as listThingList} from '/@/api/thing'
import {BASE_URL} from "/@/store/constants";
import {useUserStore} from "/@/store";

// 使用reactive声明一个响应式对象contentData，用于存储各种数据和状态
const contentData = reactive({
  selectX: 0,
  selectTagId: -1,
  cData: [], // 存储分类数据
  selectedKeys: [], // 存储被选中的分类的key
  tagData: [], // 存储标签数据
  loading: false, // 是否加载中

  tabData: ['最新', '最热', '推荐'], // 页面顶部选项卡数据
  selectTabIndex: 0, // 当前选中的选项卡索引
  tabUnderLeft: 12, // 选项卡下划线左偏移

  thingData: [], // 存储商品数据
  pageData: [], // 存储当前页的商品数据

  page: 1, // 当前页码
  total: 0, // 总数
  pageSize: 9, // 每页数量
})

// 使用useUserStore引入用户状态管理
const userStore = useUserStore()
const router = useRouter();

// 初始化函数，在组件挂载时调用，用于获取分类数据
onMounted(() => {
  initSider()
  getThingList({})
})

/**
 * 初始化侧边栏数据
 * 该函数无参数和返回值，主要用于填充contentData的cData和tagData属性，
 * 分别通过调用listClassificationList和listTagList接口获取分类和标签数据。
 */
const initSider = () => {
  // 初始化分类数据
  contentData.cData.push({key:'-1', title:'全部'})
  listClassificationList().then(res => {
    res.data.forEach(item=>{
      item.key = item.id // 将获取的item的id赋值给key
      contentData.cData.push(item) // 将处理后的item添加到cData数组中
    })
  })
}

/**
 * 获取当前选中的键值。
 * 检查 `contentData.selectedKeys` 数组的长度，如果大于0，则返回数组的第一个元素；否则，返回 -1。
 * @returns {number|string} 选中的键值或 -1（如果没有任何键被选中）。
 */
const getSelectedKey = () => {
  if (contentData.selectedKeys.length > 0) {
    return contentData.selectedKeys[0]
  } else {
    return -1
  }
}

/**
 * 处理选择操作的回调函数。
 * 更新 `contentData.selectedKeys` 为传入的键数组，并在有键被选中时调用 `getThingList`。
 * @param {Array} selectedKeys 被选择的键数组。
 */
const onSelect = (selectedKeys) => {
  contentData.selectedKeys = selectedKeys
  // 当有键被选中时，调用 getThingList，并传递第一个选中的键。
  if (contentData.selectedKeys.length > 0) {
    getThingList({c: getSelectedKey()})
  }
  // 重置 selectTagId 为 -1。
  contentData.selectTagId = -1
}


/**
 * 处理并打开项目详情页
 * @param {Object} item - 包含项目信息的对象
 * @description 根据传入的项目对象，解析路由并打开一个新的页面显示项目详情
 */
const handleDetail = (item) => {
  // 解析路由，准备跳转
  let text = router.resolve({name: 'detail', query: {id: item.id}})
  // 在新标签页打开详情页链接
  window.open(text.href, '_blank')
}


// 分页事件
const changePage = (page) => {
  contentData.page = page
  let start = (contentData.page - 1) * contentData.pageSize
  contentData.pageData = contentData.thingData.slice(start, start + contentData.pageSize)
}

// 获取商品列表数据
const getThingList = (data) => {
  contentData.loading = true
  listThingList(data).then(res => {
    contentData.loading = false
    res.data.forEach((item, index) => {
      if (item.cover) {
        item.cover = BASE_URL + '/api/upload/image/' +  item.cover
      }
    })
    contentData.thingData = res.data
    contentData.total = contentData.thingData.length
    changePage(1)
  }).catch(err => {
    console.log(err)
    contentData.loading = false
  })
}


</script>

<style scoped lang="less">
.content {
  display: flex;
  flex-direction: row;
  width: 1100px;
  margin: 80px auto;
}

.content-left {
  width: 220px;
  margin-right: 32px;
}

.left-search-item {
  overflow: hidden;
  border-bottom: 1px solid #cedce4;
  margin-top: 24px;
  padding-bottom: 24px;
}

h4 {
  color: #4d4d4d;
  font-weight: 600;
  font-size: 16px;
  line-height: 24px;
  height: 24px;
}

.category-item {
  cursor: pointer;
  color: #333;
  margin: 12px 0 0;
  padding-left: 16px;
}

ul {
  margin: 0;
  padding: 0;
}

ul {
  list-style-type: none;
}

li {
  margin: 4px 0 0;
  display: list-item;
  text-align: -webkit-match-parent;
}

.child {
  color: #333;
  padding-left: 16px;
}

.child:hover {
  color: #4684e2;
}

.select {
  color: #4684e2;
}

.flex-view {
  -webkit-box-pack: justify;
  -ms-flex-pack: justify;
  //justify-content: space-between;
  display: flex;
}

.name {
  font-size: 14px;
}

.name:hover {
  color: #4684e2;
}

.count {
  font-size: 14px;
  color: #999;
}

.check-item {
  font-size: 0;
  height: 18px;
  line-height: 12px;
  margin: 12px 0 0;
  color: #333;
  cursor: pointer;
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
}

.check-item input {
  cursor: pointer;
}

.check-item label {
  font-size: 14px;
  margin-left: 12px;
  cursor: pointer;
  -webkit-box-flex: 1;
  -ms-flex: 1;
  flex: 1;
}

.tag-view {
  -ms-flex-wrap: wrap;
  flex-wrap: wrap;
  margin-top: 4px;
}

.tag-flex-view {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
}

.tag {
  background: #fff;
  border: 1px solid #a1adc6;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  border-radius: 16px;
  height: 20px;
  line-height: 18px;
  padding: 0 8px;
  margin: 8px 8px 0 0;
  cursor: pointer;
  font-size: 12px;
  color: #152833;
}

.tag:hover {
  background: #4684e3;
  color: #fff;
  border: 1px solid #4684e3;
}

.tag-select {
  background: #4684e3;
  color: #fff;
  border: 1px solid #4684e3;
}

.content-right {
  -webkit-box-flex: 1;
  -ms-flex: 1;
  flex: 1;
  padding-top: 12px;

  .pc-search-view {
    margin: 0 0 24px;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;

    .search-icon {
      width: 20px;
      height: 20px;
      -webkit-box-flex: 0;
      -ms-flex: 0 0 20px;
      flex: 0 0 20px;
      margin-right: 16px;
    }

    input {
      outline: none;
      border: 0px;
      -webkit-box-flex: 1;
      -ms-flex: 1;
      flex: 1;
      border-bottom: 1px solid #cedce4;
      color: #152844;
      font-size: 14px;
      height: 22px;
      line-height: 22px;
      -ms-flex-item-align: end;
      align-self: flex-end;
      padding-bottom: 8px;
    }

    .clear-search-icon {
      position: relative;
      left: -20px;
      cursor: pointer;
    }

    button {
      outline: none;
      border: none;
      font-size: 14px;
      color: #fff;
      background: #288dda;
      border-radius: 32px;
      width: 88px;
      height: 32px;
      line-height: 32px;
      margin-left: 2px;
      cursor: pointer;
    }

    .float-count {
      color: #999;
      margin-left: 24px;
    }
  }

  .flex-view {
    display: flex;
  }

  .top-select-view {
    -webkit-box-pack: justify;
    -ms-flex-pack: justify;
    justify-content: space-between;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    height: 40px;
    line-height: 40px;

    .type-view {
      position: relative;
      font-weight: 400;
      font-size: 18px;
      color: #5f77a6;

      .type-tab {
        margin-right: 32px;
        cursor: pointer;
      }

      .type-tab-select {
        color: #152844;
        font-weight: 600;
        font-size: 20px;
      }

      .tab-underline {
        position: absolute;
        bottom: 0;
        //left: 22px;
        width: 16px;
        height: 4px;
        background: #4684e2;
        -webkit-transition: left .3s;
        transition: left .3s;
      }
    }

    .order-view {
      position: relative;
      color: #6c6c6c;
      font-size: 14px;

      .title {
        margin-right: 8px;
      }

      .tab {
        color: #999;
        margin-right: 20px;
        cursor: pointer;
      }

      .tab-select {
        color: #152844;
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

  }

  .pc-thing-list {
    -ms-flex-wrap: wrap;
    flex-wrap: wrap;

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
      cursor: pointer;

      .img-view {
        //text-align: center;
        height: 200px;
        width: 255px;

        img {
          height: 200px;
          width: 186px;
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
          color: #0F1111 !important;
          font-size: 15px !important;
          font-weight: 400 !important;
          font-style: normal !important;
          text-transform: none !important;
          text-decoration: none !important;
        }



        .translators {
          color: #6f6f6f;
          font-size: 12px;
          line-height: 14px;
          margin-top: 4px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }
    }

    .no-data {
      height: 200px;
      line-height: 200px;
      text-align: center;
      width: 100%;
      font-size: 16px;
      color: #152844;
    }
  }

  .page-view {
    width: 100%;
    text-align: center;
    margin-top: 48px;
  }
}

.a-price-symbol {
  top: -0.5em;
  font-size: 12px;
}

.a-price {
  color: #0F1111;
  font-size: 21px;
}

</style>
