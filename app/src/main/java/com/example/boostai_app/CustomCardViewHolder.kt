package com.example.boostai_app

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.glia.androidsdk.chat.ChatMessage
import com.glia.widgets.chat.adapter.holder.CustomCardViewHolder
import com.google.android.material.imageview.ShapeableImageView
import org.json.JSONArray
import org.json.JSONException

class CustomViewHolder(itemView: View) : CustomCardViewHolder(itemView) {
    private val textView: TextView? = itemView.findViewById(R.id.card_text_view)
    private val buttonContainer: LinearLayout = itemView.findViewById(R.id.button_container)
    private val image: ShapeableImageView = itemView.findViewById(R.id.card_image)

    init {
        // Optional: Check if textView is null to debug
        if (textView == null) {
            Log.e("CustomViewHolder", "TextView not found!")
        }
    }

    override fun bind(message: ChatMessage, callback: ResponseCallback) {
        // Set the default text for the TextView
        textView?.text = message.content
        Log.d("CustomViewHolder", "message body: ${message}")

        try {
            message.metadata?.let {
                if (it.has("buttons"))
                handleButtons(it.getJSONArray("buttons"), callback)
                if (it.has("image_url")) {
                    handleImage(it.getString("image_url"), it.getString("alt_text"))
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace() // Handle errors with metadata parsing
        }
    }

    private fun handleButtons(buttons: JSONArray, callback: ResponseCallback) {
        buttonContainer.removeAllViews() // Clear existing buttons
        for (i in 0 until buttons.length()) {
            val button = buttons.getJSONObject(i)
            val buttonText: String? = button.getString("text")
            val buttonValue: String? = button.getString("value")
            val newButton: Button = Button(itemView.context).apply {
                text = buttonText
                if (button.has("type") && button.getString("type") == "external") {
                    setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(buttonValue)
                        itemView.context.startActivity(intent)
                    }
                } else {
                    setOnClickListener {
                        callback.sendResponse(buttonText, buttonValue)
                    }
                }
            }
            buttonContainer.addView(newButton)
        }
    }

    private fun handleImage(imageUrl: String?, altText: String?) {
        imageUrl?.let {
            Glide.with(itemView.context)
                .load(it)
                .into(image)
            image.contentDescription = altText ?: "Image"
        }
    }
}
