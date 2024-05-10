import {get, post} from '/@/utils/http/axios';

enum URL {
  doSeckill = '/api/seckill/doSeckill2',
}

const seckillApi = async (params: any) =>
  post<any>({url: URL.doSeckill, params: params, data: {}, headers: {}});


export {seckillApi};
