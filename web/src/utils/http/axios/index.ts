import axios from 'axios';
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse, AxiosError, InternalAxiosRequestConfig } from 'axios';
import { showMessage } from './status';
import { IResponse } from './type';
import { getToken } from '/@/utils/auth';
import { TokenPrefix } from '/@/utils/auth';
import {ADMIN_USER_TOKEN, USER_TOKEN, BASE_URL} from '/@/store/constants'
/**
 * 创建一个axios实例并配置基础URL和超时时间
 * @returns 返回一个配置好的AxiosInstance实例
 */
const service: AxiosInstance = axios.create({
  // 使用环境变量中的BASE_URL或直接使用全局常量
  baseURL: BASE_URL + '',
  timeout: 15000,
});

/**
 * 在请求发送前添加拦截器，用于设置请求头
 */
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 设置管理员令牌和用户令牌到请求头
    config.headers.ADMINTOKEN = localStorage.getItem(ADMIN_USER_TOKEN)
    config.headers.TOKEN = localStorage.getItem(USER_TOKEN)

    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  },
);

/**
 * 在响应返回时添加拦截器，用于处理响应数据或错误
 */
service.interceptors.response.use(
  (response: AxiosResponse) => {
    // 根据响应状态和数据代码处理成功响应
    if(response.status == 200) {
      if(response.data.code == 0 || response.data.code == 200) {
        return response
      }else {
        return Promise.reject(response.data)
      }
    } else {
      return Promise.reject(response.data)
    }
  },
  // 处理请求失败的情况
  (error: any) => {
    // 根据响应状态码处理特定错误
    console.log(error.response.status)
    if(error.response.status == 404) {
      // todo
    } else if(error.response.status == 403) {
      // todo
    }
    return Promise.reject(error)
  },
);


/**
 * 创建一个泛型的请求函数，用于封装axios的请求过程。
 * @param config AxiosRequestConfig类型的配置对象，用于配置请求。
 * @returns 返回一个Promise<T>，其中T代表响应数据的类型。
 */
const request = <T = any>(config: AxiosRequestConfig): Promise<T> => {
  // 复制传入的配置对象
  const conf = config;
  // 创建一个新的Promise，用于处理异步请求和响应
  return new Promise((resolve, reject) => {
    // 使用axios发起请求
    service.request<any, AxiosResponse<IResponse>>(conf).then((res: AxiosResponse<IResponse>) => {
      // 请求成功后，解析响应数据并将数据作为Promise的结果
      const data = res.data
      resolve(data as T);
    }).catch(err => {
      // 请求失败后，将错误作为Promise的结果
      reject(err)
    });
  });
};


/**
 * 发起GET请求的封装函数
 * @param config - Axios请求配置对象
 * @returns 返回一个泛型Promise，解析为响应数据
 */
export function get<T = any>(config: AxiosRequestConfig): Promise<T> {
  return request({ ...config, method: 'GET' });
}

/**
 * 发起POST请求的封装函数
 * @param config - Axios请求配置对象
 * @returns 返回一个泛型Promise，解析为响应数据
 */
export function post<T = any>(config: AxiosRequestConfig): Promise<T> {
  return request({ ...config, method: 'POST' });
}

export default request;

export type { AxiosInstance, AxiosResponse };
