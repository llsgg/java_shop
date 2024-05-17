<template>
  <div>
    <!--页面区域-->
    <div class="page-view">
      <div class="table-operations">
        <a-space>
          <a-button type="primary" @click="handleAdd">新增</a-button>
          <a-button @click="handleBatchDelete">批量删除</a-button>
        </a-space>
      </div>
      <a-table
          size="middle"
          rowKey="id"
          :loading="data.loading"
          :columns="columns"
          :data-source="data.dataList"
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
          <template v-if="column.key === 'operation'">
            <span>
              <a @click="handleEdit(record)">编辑</a>
              <a-divider type="vertical" />
              <a-popconfirm title="确定删除?" ok-text="是" cancel-text="否" @confirm="confirmDelete(record)">
                <a href="#">删除</a>
              </a-popconfirm>
            </span>
          </template>
        </template>
      </a-table>
    </div>

    <!--弹窗区域-->
    <div>
      <a-modal
          :visible="modal.visile"
          :forceRender="true"
          :title="modal.title"
          width="880px"
          ok-text="确认"
          cancel-text="取消"
          @cancel="handleCancel"
          @ok="handleOk"
      >
        <div>
          <a-form ref="myform" :label-col="{ style: { width: '80px' } }" :model="modal.form" :rules="modal.rules">
            <a-row :gutter="24">
              <a-col span="24">
                <a-form-item label="商品名称" name="title">
                  <a-input placeholder="请输入" v-model:value="modal.form.title"></a-input>
                </a-form-item>
              </a-col>
              <a-col span="12">
                <a-form-item label="分类" name="classificationId">
                  <a-select placeholder="请选择"
                            allowClear
                            :options="modal.cData"
                            :field-names="{ label: 'title', value: 'id',}"
                            v-model:value="modal.form.classificationId">
                  </a-select>
                </a-form-item>
              </a-col>

              <a-col span="24">
                <a-form-item label="封面">
                  <a-upload-dragger
                      name="file"
                      accept="image/*"
                      :multiple="false"
                      :before-upload="beforeUpload"
                      v-model:file-list="fileList"
                  >
                    <p class="ant-upload-drag-icon">
                      <template v-if="modal.form.coverUrl">
                        <img :src="modal.form.coverUrl"  style="width: 60px;height: 80px;"/>
                      </template>
                      <template v-else>
                        <file-image-outlined />
                      </template>
                    </p>
                    <p class="ant-upload-text">
                      请选择要上传的封面图片
                    </p>
                  </a-upload-dragger>
                </a-form-item>
              </a-col>

              <a-col span="24">
                <a-form-item label="内容简介">
                  <a-textarea placeholder="请输入" v-model:value="modal.form.description"></a-textarea>
                </a-form-item>
              </a-col>
              <a-col span="12">
                <a-form-item label="原价" name="price">
                  <a-input-number  placeholder="请输入" :min="0" v-model:value="modal.form.price" style="width: 100%;"></a-input-number>
                </a-form-item>
              </a-col>

              <a-col span="12">
                <a-form-item label="秒杀价" name="seckillPrice">
                  <a-input-number  placeholder="请输入" :min="0" v-model:value="modal.form.seckillPrice" style="width: 100%;"></a-input-number>
                </a-form-item>
              </a-col>

              <a-col span="12">
                <a-form-item label="库存" name="count">
                  <a-input-number placeholder="请输入" :min="0" v-model:value="modal.form.count" style="width: 100%;"></a-input-number>
                </a-form-item>
              </a-col>

              <a-col span="12">
                <a-form-item label="秒杀库存" name="stockCount">
                  <a-input-number placeholder="请输入" :min="0" v-model:value="modal.form.stockCount" style="width: 100%;"></a-input-number>
                </a-form-item>
              </a-col>

              <a-col span="12">
                <a-form-item label="秒杀开始时间" name="startDate">
                  <a-input-number placeholder="请输入" :min="0" v-model:value="modal.form.startDate" style="width: 100%;"></a-input-number>
                </a-form-item>
              </a-col>

              <a-col span="12">
                <a-form-item label="秒杀结束时间" name="endDate" style="font-size: small">
                  <a-input-number placeholder="请输入" :min="0" v-model:value="modal.form.endDate" style="width: 100%;"></a-input-number>
                </a-form-item>
              </a-col>


              <a-col span="12">
                <a-form-item label="状态" name="status">
                  <a-select placeholder="请选择" allowClear v-model:value="modal.form.status">
                    <a-select-option key="0" value="0">上架</a-select-option>
                    <a-select-option key="1" value="1">下架</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
            </a-row>
          </a-form>
        </div>
      </a-modal>
    </div>
  </div>
</template>

<script setup lang="ts">
import { FormInstance, message, SelectProps } from 'ant-design-vue';
import { createApi, listApi, updateApi, deleteApi, seckillListApi } from "/src/api/goods";
import {listApi as listClassificationApi} from '/@/api/classification'
import {listApi as listTagApi} from '/@/api/tag'
import {BASE_URL} from "/@/store/constants";
import { FileImageOutlined } from '@ant-design/icons-vue';

const columns = reactive([

  {
    title: '序号',
    dataIndex: 'index',
    key: 'index',
    width: 60
  },
  {
    title: '名称',
    dataIndex: 'title',
    key: 'title',
    customRender: ({ text, record, index, column }) => text ? text.substring(0, 7) + '...' : '--'
  },
  {
    title: '原价',
    dataIndex: 'price',
    key: 'price'
  },
  {
    title: '秒杀价',
    dataIndex: 'seckillPrice',
    key: 'seckillPrice'
  },
  {
    title: '库存',
    dataIndex: 'count',
    key: 'count'
  },
  {
    title: '秒杀库存',
    dataIndex: 'stockCount',
    key: 'stockCount'
  },
  // {
  //   title: '简介',
  //   dataIndex: 'description',
  //   key: 'description',
  //   customRender: ({ text, record, index, column }) => text ? text.substring(0, 10) + '...' : '--'
  // },
  {
    title: '开始时间',
    dataIndex: 'startDate',
    key: 'startDate',
    align: 'center', // 居中显示
    // 自定义渲染函数，用于格式化订单时间
    customRender: ({text}) => format(text)
  },
  {
    title: '结束时间',
    dataIndex: 'endDate',
    key: 'endDate',
    align: 'center', // 居中显示
    // 自定义渲染函数，用于格式化订单时间
    customRender: ({text}) => format(text)
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    customRender: ({ text, record, index, column }) => text === '0' ? '上架' : '下架'
  },
  {
    title: '操作',
    dataIndex: 'action',
    key: 'operation',
    align: 'center',
    fixed: 'right',
    width: 140,
  },
]);

const beforeUpload = (file: File) => {
  // 改文件名
  const fileName = new Date().getTime().toString() + '.' + file.type.substring(6);
  const copyFile = new File([file], fileName);
  console.log(copyFile);
  modal.form.imageFile = copyFile;
  return false;
};

// 文件列表
const fileList = ref<any[]>([]);

// 页面数据
const data = reactive({
  dataList: [],
  loading: false,
  keyword: '',
  selectedRowKeys: [] as any[],
  pageSize: 10,
  page: 1,
});

// 弹窗数据源
/**
 * 创建一个响应式对象modal，用于管理模态窗口的状态和数据
 * @returns {Object} 返回一个包含模态窗口各种状态和数据的响应式对象
 */
const modal = reactive({
  visile: false, // 控制模态窗口是否可见
  editFlag: false, // 编辑标志，用于区分是新增还是编辑操作
  title: '', // 模态窗口的标题
  cData: [], // 用于存储从后端获取的数据列表
  tagData: [{}], // 标签数据，初始化为空对象
  form: { // 表单数据对象
    id: undefined, // 实体ID，初始为未定义
    title: undefined, // 标题，初始为未定义
    classificationId: undefined, // 分类ID，初始为未定义
    tags: [], // 标签列表，初始化为空数组
    count: undefined, // 库存，初始为未定义
    stockCount: undefined, // 库存，初始为未定义
    price: undefined, // 定价，初始为未定义
    seckillPrice: undefined, // 定价，初始为未定义
    status: undefined, // 状态，初始为未定义
    cover: undefined, // 封面图片地址，初始为未定义
    coverUrl: undefined, // 封面图片的URL，初始为未定义
    imageFile: undefined, // 图片文件对象，初始为未定义
    startDate: undefined,
    endDate: undefined
  },
  rules: { // 表单校验规则
    title: [{ required: true, message: '请输入名称', trigger: 'change' }],
    classificationId: [{ required: true, message: '请选择分类', trigger: 'change' }],
    count: [{ required: true, message: '请输入库存', trigger: 'change' }],
    stockCount: [{ required: true, message: '请输入秒杀库存', trigger: 'change' }],
    price: [{ required: true, message: '请输入原价', trigger: 'change' }],
    seckillPrice: [{ required: true, message: '请输入秒杀价', trigger: 'change' }],
    startDate: [{ required: true, message: '请输入秒杀开始时间', trigger: 'change' }],
    endDate: [{ required: true, message: '请输入秒杀结束时间', trigger: 'change' }],
    status: [{ required: true, message: '请选择状态', trigger: 'change' }]
  },
});

const myform = ref<FormInstance>();

function format (date) {
  if (date == null) return "未设置"
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

onMounted(() => {
  getDataList();
  getCDataList();
});

const getDataList = () => {
  data.loading = true;
  seckillListApi({
    keyword: data.keyword,
  })
      .then((res) => {
        data.loading = false;
        console.log(res);
        res.data.forEach((item: any, index: any) => {
          item.index = index + 1;
        });
        data.dataList = res.data;
      })
      .catch((err) => {
        data.loading = false;
        console.log(err);
      });
}

const getCDataList = () => {
  listClassificationApi({}).then(res => {
    modal.cData = res.data
  })
}

const rowSelection = ref({
  onChange: (selectedRowKeys: (string | number)[], selectedRows: DataItem[]) => {
    console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
    data.selectedRowKeys = selectedRowKeys;
  },
});

const handleAdd = () => {
  resetModal();
  modal.visile = true;
  modal.editFlag = false;
  modal.title = '新增';
  // 重置
  for (const key in modal.form) {
    modal.form[key] = undefined;
  }
  modal.form.cover = undefined
};
const handleEdit = (record: any) => {
  resetModal();
  modal.visile = true;
  modal.editFlag = true;
  modal.title = '编辑';
  // 重置
  for (const key in modal.form) {
    modal.form[key] = undefined;
  }
  for (const key in record) {
    if(record[key]) {
      modal.form[key] = record[key];
    }
  }
  if(modal.form.cover) {
    modal.form.coverUrl = BASE_URL + '/api/upload/image/' + modal.form.cover
    modal.form.cover = undefined
  }
};

const confirmDelete = (record: any) => {
  console.log('delete', record);
  deleteApi({ ids: record.id })
      .then((res) => {
        getDataList();
      })
      .catch((err) => {
        message.error(err.msg || '操作失败');
      });
};

const handleBatchDelete = () => {
  console.log(data.selectedRowKeys);
  if (data.selectedRowKeys.length <= 0) {
    console.log('hello');
    message.warn('请勾选删除项');
    return;
  }
  deleteApi({ ids: data.selectedRowKeys.join(',') })
      .then((res) => {
        message.success('删除成功');
        data.selectedRowKeys = [];
        getDataList();
      })
      .catch((err) => {
        message.error(err.msg || '操作失败');
      });
};

const handleOk = () => {
  myform.value
      ?.validate()
      .then(() => {
        const formData = new FormData();
        if(modal.editFlag) {
          formData.append('id', modal.form.id)
        }
        formData.append('title', modal.form.title)
        if (modal.form.classificationId) {
          formData.append('classificationId', modal.form.classificationId)
        }
        if (modal.form.tags) {
          modal.form.tags.forEach(function (value) {
            if(value){
              formData.append('tags[]', value)
            }
          })
        }
        if (modal.form.imageFile) {
          formData.append('imageFile', modal.form.imageFile)
        }
        formData.append('description', modal.form.description || '')
        formData.append('price', modal.form.price || '')
        if (modal.form.repertory >= 0) {
          formData.append('repertory', modal.form.repertory)
        }
        if (modal.form.status) {
          formData.append('status', modal.form.status)
        }
        if (modal.editFlag) {
          updateApi(formData)
              .then((res) => {
                hideModal();
                getDataList();
              })
              .catch((err) => {
                console.log(err);
                message.error(err.msg || '操作失败');
              });
        } else {
          createApi(formData)
              .then((res) => {
                hideModal();
                getDataList();
              })
              .catch((err) => {
                console.log(err);
                message.error(err.msg || '操作失败');
              });
        }
      })
      .catch((err) => {
        console.log('不能为空');
      });
};

const handleCancel = () => {
  hideModal();
};

// 恢复表单初始状态
const resetModal = () => {
  myform.value?.resetFields();
  fileList.value = []
};

// 关闭弹窗
const hideModal = () => {
  modal.visile = false;
};
</script>

<style scoped lang="less">
.page-view {
  min-height: 100%;
  background: #fff;
  padding: 24px;
  display: flex;
  flex-direction: column;
}

.table-operations {
  margin-bottom: 16px;
  text-align: right;
}

.table-operations > button {
  margin-right: 8px;
}
</style>
