import {get, post} from '/@/utils/http/axios';

enum URL {
  doSeckill = '/api/seckill/doSeckill2',
  getResult = '/api/seckill/result',
}

const seckillApi = async (params: any) =>
  post<any>({url: URL.doSeckill, params: params, data: {}, headers: {}});

const resultApi = async (params: any) =>
  get<any>({url: URL.getResult, params: params, data: {}, headers: {}});

export {seckillApi, resultApi};
