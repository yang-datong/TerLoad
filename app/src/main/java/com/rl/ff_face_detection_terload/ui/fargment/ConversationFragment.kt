package com.rl.ff_face_detection_terload.ui.fargment

import androidx.core.view.isGone
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rl.ff_face_detection_terload.R
import com.rl.ff_face_detection_terload.adapter.ConversationListAdapter
import com.rl.ff_face_detection_terload.adapter.MessageListenerAdapter
import com.rl.ff_face_detection_terload.contract.ConversationContract
import com.rl.ff_face_detection_terload.presenter.ConversationPresenter
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import kotlinx.android.synthetic.main.fragment_contact.recyclerview
import kotlinx.android.synthetic.main.fragment_conversation.*
import kotlinx.android.synthetic.main.title_bar.*
import org.jetbrains.anko.runOnUiThread
import java.util.*

/**
 * @author 杨景
 * @description:
 * @date :2021/1/2 22:24
 */
//对话页面
class ConversationFragment : BaseFragment(), ConversationContract.View {
    override fun getLayoutResID() = R.layout.fragment_conversation


    val presenter by lazy { ConversationPresenter(this) }

    private val msgListener = object : MessageListenerAdapter() {
        override fun onMessageReceived(messages: MutableList<EMMessage>?) {
            presenter.onConversations()
            context?.runOnUiThread { onUpdate() }
        }
    }

    private val callback = object : ItemTouchHelper.Callback() {
        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            return makeMovementFlags(dragFlags, 0)//监听上下左右移动Item
        }

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            recyclerView.adapter?.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
            Collections.swap(presenter.conversations, viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {//处理左右移动item
        }

        override fun isLongPressDragEnabled() = true   // 拖动排序动画
    }


    override fun inits() {
        tv_title.text = "聊天"
        img_ret.isGone = true
        recyclerview.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = ConversationListAdapter(context, presenter.conversations)
            ItemTouchHelper(callback).attachToRecyclerView(this)
        }

        smartrefresh.setOnRefreshListener {
            it.finishRefresh(1000)
            presenter.onConversations()
            onUpdate()
        }
        presenter.onConversations()
        EMClient.getInstance().chatManager().addMessageListener(msgListener)
    }


    override fun onUpdate() {
        if (presenter.conversations.size == 0) {
            tv_mes_empt.isGone = false
            recyclerview.isGone = true
        } else {
            tv_mes_empt.isGone = true
            recyclerview.isGone = false
        }
        recyclerview.adapter?.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        onUpdate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }
}