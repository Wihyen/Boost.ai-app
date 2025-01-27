package com.example.boostai_app

import androidx.fragment.app.Fragment
import com.glia.widgets.GliaWidgets
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.glia.widgets.chat.ChatActivity
import android.content.Intent

class GliaMobileFragment: Fragment(R.layout.glia_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val queueIds: List<String> = listOf("23802d4c-b363-4737-9fe2-c9abce33191d", "QUEUE_ID2") // Define a list of queue IDs

        view.findViewById<Button>(R.id.glia_button).setOnClickListener(View.OnClickListener {
            val intent = Intent(requireContext(), ChatActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra(GliaWidgets.COMPANY_NAME, "Boost.ai")
                putExtra(GliaWidgets.QUEUE_IDS, ArrayList(queueIds))
            }
            startActivity(intent)
        })

    }


}



