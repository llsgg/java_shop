import {get, post} from '/@/utils/http/axios';

enum URL {
  doSeckill = '/api/seckill/doSeckill',
  getResult = '/api/seckill/result',
  getSeckillPath = '/api/seckill/path'
}

const seckillApi = async (url: string, params: any) =>
  get<any>({url: url, params: params, data: {}, headers: {}});

const resultApi = async (params: any) =>
  get<any>({url: URL.getResult, params: params, data: {}, headers: {}});

const seckillPathApi = async (params: any) =>
  get<any>({url: URL.getSeckillPath, params: params, data: {}, headers: {}});

export {seckillApi, resultApi, seckillPathApi};
