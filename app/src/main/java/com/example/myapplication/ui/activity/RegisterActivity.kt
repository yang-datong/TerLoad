package com.example.myapplication.ui.activity

import com.example.myapplication.R
import com.example.myapplication.contract.RegisterContract
import com.example.myapplication.presenter.RegisterPresenter
import kotlinx.android.synthetic.main.activity_main_login_navigation_bar.*
import kotlinx.android.synthetic.main.activity_main_register_step_two.*
import org.jetbrains.anko.toast

class RegisterActivity : BaseActivity(), RegisterContract.View {
    override fun getLayoutResID() = R.layout.activity_main_register_step_two
    val presenter =  RegisterPresenter(this)

    override fun inits() {
        ib_navigation_back.setOnClickListener { finish() }
        bt_register_submit.setOnClickListener {
            presenter.register(et_register_username.text.trim().toString(),
                    et_register_pwd_input.text.trim().toString(),
                    et_register_pwd_confirm.text.trim().toString())
        }
    }

    override fun onUserNameError() {
        et_register_username.error = "用户名错误"
    }

    override fun onPassWordError() {
        et_register_pwd_input.error = "密码错误"
    }

    override fun onConfirmPassWordError() {
        et_register_pwd_confirm.error = "两次密码不一致"
    }

    override fun onStartRegister() {
        showProgress("注册账号中...")
    }

    override fun onRegisterInSuccess() {
        dismissProgress()
        finish()
    }

    override fun onRegisterInFailed() {
        dismissProgress()
        toast("注册失败")
    }
}
