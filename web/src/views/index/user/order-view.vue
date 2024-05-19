<template>
  <div class="content-list">
    <div class="list-title">我的订单</div>
    <div class="list-content">

      <a-table
        size="middle"
        rowKey="id"
        :loading="data.loading"
        :columns="columns"
        :data-source="data.tagList"
        :scroll="{ x: 'max-content' }"
        :row-selection="rowSelection"
        :pagination="{
          size: 'default',
          current: data.page,
          pageSize: data.pageSize,
          onChange: (current) => (data.page = current),
          showSizeChanger: false,
          showTotal: (total) => `共${total}条数据`,
        }"
      >
        <template #bodyCell="{ text, record, index, column }">
          <template v-if="column.key === 'status'">
            <a-tag :color="text === '1'? '#2db7f5':'#87d068'">
              {{text == '1'? '待支付': text == '2' ? '已支付':'已取消'}}
            </a-tag>
          </template>
          <template v-if="column.key === 'operation'">
            <span>
               <a @click="pay(record)">支付</a>
              <a-divider type="vertical" />
              <a-popconfirm title="确定删除?" ok-text="是" cancel-text="否" @confirm="confirmDelete(record)">
                <a>删除</a>
              </a-popconfirm>
            </span>
          </template>
        </template>
      </a-table>


    </div>
  </div>
</template>

<script setup>
import {message} from "ant-design-vue";

import { userOrderListApi, listApi, updateApi, deleteApi, cancelApi ,payApi} from '/@/api/order';
import {getFormatTime} from "/@/utils";

import {updateUserPwdApi} from '/@/api/user'
import {useUserStore} from "/@/store";

const router = useRouter();
const userStore = useUserStore();

function format (date) {
  if (date == null) return "未支付"
  return new Date(date).format("yyyy-MM-dd HH:mm:ss")
}
//设定时间格式化函数，使用new Date().format("yyyy-MM-dd HH:mm:ss");
Date.prototype.format = function (format) {
  var args = {
    "M+": this.getMonth() + 1,
    "d+": this.getDate(),
    "H+": this.getHours(),
    "m+": this.getMinutes(),
    "s+": this.getSeconds(),
  };
  if (/(y+)/.test(format))
    format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
  for (var i in args) {
    var n = args[i];
    if (new RegExp("(" + i + ")").test(format))
      format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? n : ("00" + n).substr(("" + n).length));
  }
  return format;
};

// 创建表格列的配置
const columns = reactive([
  {
    title: '序号',
    dataIndex: 'index',
    key: 'index',
    align: 'center' // 居中显示
  },
  {
    title: '商品',
    dataIndex: 'title',
    key: 'title',
    align: 'center', // 居中显示
    // 自定义渲染函数，用于截取商品标题的前10个字符并添加省略号，如果标题为空则显示"--"
    customRender: ({text}) => text ? text.substring(0, 10) + '...' : '--'
  },

  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    align: 'center', // 居中显示
    // 使用scopedSlots进行自定义渲染，渲染内容由模板决定
    scopedSlots: {customRender: 'status'}
  },
  {
    title: '订单时间',
    dataIndex: 'orderTime',
    key: 'orderTime',
    align: 'center', // 居中显示
    // 自定义渲染函数，用于格式化订单时间
    customRender: ({text}) => format(text)
  },
  {
    title: '支付时间',
    dataIndex: 'payTime',
    key: 'payTime',
    align: 'center', // 居中显示
    // 自定义渲染函数，用于格式化订单时间
    customRender: ({text}) => format(text)
  },
  {
    title: '价格',
    dataIndex: 'price',
    key: 'price',
    align: 'center', // 居中显示
    // 自定义渲染函数，用于截取商品标题的前10个字符并添加省略号，如果标题为空则显示"--"
    customRender: ({text}) => (text + "￥")
  },
  {
    title: '操作',
    dataIndex: 'action',
    key: 'operation',
    align: 'center', // 居中显示
    fixed: 'right', // 固定在表格的右端
    width: 120, // 操作列宽度设置为120px
  },
]);

// 页面数据
const data = reactive({
  tagList: [],
  loading: false,
  keyword: userStore.user_id,
  selectedRowKeys: [],
  pageSize: 10,
  page: 1,
});


onMounted(() => {
  getDataList();
});


/**
 * 获取数据列表的函数。
 * 无参数。
 * 无显式返回值，但会更新data对象中的tagList和loading属性。
 */
const getDataList = () => {
  // 设置加载状态为true
  data.loading = true;
  // 调用listApi，传入关键字参数
  userOrderListApi({
    userId: userStore.user_id,
  })
    .then((res) => {
      // 请求成功后，设置加载状态为false
      data.loading = false;
      console.log(res);
      // 为响应数据的每一项添加索引
      res.data.forEach((item, index) => {
        item.index = index + 1;
      });
      // 更新data对象中的tagList为处理后的数据
      data.tagList = res.data;
    })
    .catch((err) => {
      // 请求失败后，设置加载状态为false
      data.loading = false;
      console.log(err);
    });
};


// 创建并初始化 rowSelection 变量，作为一个 ref 对象，用于处理表格行选择的逻辑
const rowSelection = ref({
  // 定义onChange回调函数，当选择的行发生变化时被调用
  onChange: (selectedRowKeys, selectedRows) => {
    // 打印选中行的 key 数组和选中行的数据数组到控制台
    console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
    // 更新 data 对象中的 selectedRowKeys 属性，以保持数据同步
    data.selectedRowKeys = selectedRowKeys;
  },
});

const pay = (record) => {
  console.log('pay', record);
  const payUrl = 'http://localhost:9100/api/pay/alipay?orderId=' + record.id;
  window.open(payUrl);
};

const confirmDelete = (record) => {
  console.log('delete', record);
  deleteApi({ ids: record.id })
    .then((res) => {
      getDataList();
      message.success('操作成功');
    })
    .catch((err) => {
      message.error(err.msg || '操作失败');
    });
};

</script>
<style scoped lang="less">
progress {
  vertical-align: baseline;
}

.flex-view {
  display: flex;
}

input, textarea {
  outline: none;
  border-style: none;
}

.content-list {
  flex: 1;

  .list-title {
    color: #152844;
    font-weight: 600;
    font-size: 18px;
    //line-height: 24px;
    height: 48px;
    margin-bottom: 4px;
    border-bottom: 1px solid #cedce4;
  }
}


</style>
