// 权限问题后期增加
import { get, post } from '/@/utils/http/axios';
import { UserState } from '/@/store/modules/user/types';
// import axios from 'axios';
enum URL {
    list = '/api/thing/list',
    seckillList = '/api/thing/seckillList',
    create = '/api/thing/create',
    createSeckill = '/api/thing/createSeckill',
    update = '/api/thing/update',
    updateSeckill = '/api/thing/updateSeckill',
    delete = '/api/thing/delete',
    detail = '/api/thing/detail',
}

const listApi = async (params: any) => get<any>({ url: URL.list, params: params, data: {}, headers: {} });
const seckillListApi = async (params: any) => get<any>({ url: URL.seckillList, params: params, headers: {} });
const createApi = async (data: any) =>
    post<any>({ url: URL.create, params: {}, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });

const createSeckillApi = async (data: any) =>
  post<any>({ url: URL.createSeckill, params: {}, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });

const updateApi = async (data: any) =>
    post<any>({ url: URL.update,data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });

const updateSeckillApi = async (data: any) =>
  post<any>({ url: URL.updateSeckill,data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });

const deleteApi = async (params: any) => post<any>({ url: URL.delete, params: params, headers: {} });
const detailApi = async (params: any) => get<any>({ url: URL.detail, params: params, headers: {} });

export { listApi, seckillListApi, createApi, createSeckillApi, updateApi, updateSeckillApi, deleteApi, detailApi };
