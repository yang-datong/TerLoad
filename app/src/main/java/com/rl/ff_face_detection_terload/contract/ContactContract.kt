package com.rl.ff_face_detection_terload.contract

/**
 * @author 杨景
 * @description:
 * @date :2021/1/3 0:05
 */
interface ContactContract {
    interface Presenter : BasePresenter{
        fun loadData()
    }
    interface View {
        fun onLoadSuccess()
        fun onLoadFailed()
    }
}