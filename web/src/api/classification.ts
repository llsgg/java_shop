/**
 * 导出与商品分类相关的API函数。
 * 包括商品列表获取、创建、更新和删除分类的API。
 */
import { get, post } from '/@/utils/http/axios';
enum URL {
    goodsList = '/api/classification/list', // 获取商品分类列表的API路径
    create = '/api/classification/create', // 创建商品分类的API路径
    update = '/api/classification/update', // 更新商品分类的API路径
    delete = '/api/classification/delete', // 删除商品分类的API路径
}

/**
 * 获取商品分类列表。
 * @param params 用于请求的参数对象。
 * @returns 返回请求结果的Promise对象。
 */
const listApi = async (params: any) => get<any>({ url: URL.goodsList, params: params, data: {}, headers: {} });

/**
 * 创建商品分类。
 * @param data 创建分类时需要提交的数据对象。
 * @returns 返回请求结果的Promise对象。
 */
const createApi = async (data: any) =>
    post<any>({ url: URL.create, params: {}, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });

/**
 * 更新商品分类。
 * @param params 请求参数对象，通常包含需要更新的分类的ID等信息。
 * @param data 包含更新内容的数据对象。
 * @returns 返回请求结果的Promise对象。
 */
const updateApi = async (params: any, data: any) =>
    post<any>({ url: URL.update, params: params, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });

/**
 * 删除商品分类。
 * @param params 请求参数对象，通常包含需要删除的分类的ID等信息。
 * @returns 返回请求结果的Promise对象。
 */
const deleteApi = async (params: any) => post<any>({ url: URL.delete, params: params, headers: {} });

export { listApi, createApi, updateApi, deleteApi };
