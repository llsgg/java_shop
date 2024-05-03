/**
 * 导出与标签相关的API函数，包括用户列表、创建、更新和删除操作。
 */

import {get, post} from '/@/utils/http/axios';

// 定义API的URL枚举
enum URL {
    userList = '/api/tag/list', // 获取用户标签列表
    create = '/api/tag/create', // 创建标签
    update = '/api/tag/update', // 更新标签
    delete = '/api/tag/delete', // 删除标签
}

/**
 * 获取标签用户列表的API函数。
 * @param params 用于请求的参数对象。
 * @returns 返回请求结果的Promise对象。
 */
const listApi = async (params: any) =>
    get<any>({url: URL.userList, params: params, data: {}, headers: {}});

/**
 * 创建标签的API函数。
 * @param data 包含标签信息的数据对象。
 * @returns 返回请求结果的Promise对象。
 */
const createApi = async (data: any) =>
    post<any>({
        url: URL.create,
        params: {},
        data: data,
        headers: {'Content-Type': 'multipart/form-data;charset=utf-8'}
    });

/**
 * 更新标签的API函数。
 * @param params 用于请求的参数对象，通常包含标签ID等信息。
 * @param data 包含更新后的标签信息的数据对象。
 * @returns 返回请求结果的Promise对象。
 */
const updateApi = async (params: any, data: any) =>
    post<any>({
        url: URL.update,
        params: params,
        data: data,
        headers: {'Content-Type': 'multipart/form-data;charset=utf-8'}
    });

/**
 * 删除标签的API函数。
 * @param params 用于请求的参数对象，通常包含标签ID等信息。
 * @returns 返回请求结果的Promise对象。
 */
const deleteApi = async (params: any) =>
    post<any>({url: URL.delete, params: params, headers: {}});

export {listApi, createApi, updateApi, deleteApi};
