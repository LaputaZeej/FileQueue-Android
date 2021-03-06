package com.bugu.things.storage.retrofit

/**
 * Author by xpl, Date on 2021/2/8.
 */
interface ObserverCallBack {
    /**
     * @param data     返回的数据 json
     * @param encoding 网络请求的状态(成功,失败,网络失败等)
     * @param method   判断是哪个接口返回的数据
     */
    fun handleResult(data: String?, encoding: Int, method: Int)
}