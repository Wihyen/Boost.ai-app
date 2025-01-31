package com.example.boostai_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.glia.androidsdk.chat.ChatMessage
import com.glia.widgets.UiTheme
import com.glia.widgets.chat.adapter.CustomCardAdapter
import com.glia.widgets.chat.adapter.holder.CustomCardViewHolder

class ExampleCustomCardAdapter : CustomCardAdapter() {
    override fun getItemViewType(message: ChatMessage): Int {
        return when {
            message.metadata?.has("customField") == true -> CUSTOM_VIEW_TYPE
            message.metadata?.has("anotherCustomField") == true -> ANOTHER_CUSTOM_VIEW_TYPE
            else -> DEFAULT_VIEW_TYPE // Default case when no custom fields match
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        inflater: LayoutInflater,
        uiTheme: UiTheme,
        viewType: Int
    ): CustomCardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.custom_view, parent, false)
        return CustomViewHolder(itemView)
    }

    companion object {
        private const val CUSTOM_VIEW_TYPE = 1
        private const val ANOTHER_CUSTOM_VIEW_TYPE = 2
        private const val DEFAULT_VIEW_TYPE = 0 // Default case
    }
}
