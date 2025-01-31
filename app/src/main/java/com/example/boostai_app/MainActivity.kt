package com.example.boostai_app

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.glia.widgets.chat.adapter.holder.WebViewViewHolder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.serialization.json.JsonElement
import no.boostai.sdk.ChatBackend.ChatBackend
import no.boostai.sdk.ChatBackend.Objects.*
import no.boostai.sdk.UI.Events.BoostUIEvents
import no.boostai.sdk.UI.ChatViewFragment

class MainActivity : AppCompatActivity(R.layout.activity_main),
    ChatBackend.ConfigObserver,
    BoostUIEvents.Observer,
    ChatBackend.EventObserver {

    private var toolbar: Toolbar? = null
    private var viewPager: ViewPager2? = null
    private var tabLayout: TabLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ChatBackend.domain = "william.boost.ai" // Replace with your boost.ai server domain name, i.e. "your-name.boost.ai"
        ChatBackend.languageCode = "en-US"

        val customConfig = ChatConfig(
            chatPanel = ChatPanel(
                styling = Styling(
                    // Style colors etc.
                    /*primaryColor = getColor(android.R.color.holo_red_dark),
                    contrastColor = getColor(android.R.color.holo_orange_light),
                    chatBubbles = ChatBubbles(
                        vaTextColor = getColor(android.R.color.white),
                        vaBackgroundColor = getColor(android.R.color.holo_green_dark)
                    ),
                    buttons = Buttons(
                        multiline = true
                    )*/
                ),
                settings = Settings(
                    //conversationId = "[pass a stored conversationId here to resume conversation]",
                    //startLanguage = "[set preferred BCP47 language for welcome message, i.e. en-US]"
                )
            )
        )

        toolbar = findViewById(R.id.toolbar)
        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout)

        tabLayout?.background = ColorDrawable(getColor(R.color.purple))

        // Create viewPager adapter
        val adapter = ViewPagerAdapter(this).apply {
            addFragment(GliaMobileFragment(),getString(R.string.glia_test))
        }

        /*
        addFragment(ChatViewFragment(customConfig = customConfig), getString(R.string.fullscreen))
        addFragment(FloatingAvatarFragment(customConfig = customConfig), getString(R.string.avatar))

        adapter.addFragment(
            ChatViewFragment(customConfig = customConfig),
            getString(R.string.fullscreen)
        )

        adapter.addFragment(
            FloatingAvatarFragment(customConfig = customConfig),
            getString(R.string.avatar)
        )

        adapter.addFragment(
            GliaMobileFragment(),
            getString(R.string.glia_test)
        )
        */

        viewPager?.adapter = adapter

        TabLayoutMediator(tabLayout!!, viewPager!!) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()

        setSupportActionBar(toolbar)

        updateStyling(ChatBackend.config)
        ChatBackend.addConfigObserver(this)
        ChatBackend.addEventObserver(this)
        BoostUIEvents.addObserver(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        ChatBackend.removeConfigObserver(this)
        ChatBackend.removeEventObserver(this)
        BoostUIEvents.removeObserver(this)
    }

    internal class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

        private var fragments: ArrayList<Fragment> = ArrayList()
        private var fragmentTitles: ArrayList<String> = ArrayList()

        // Add fragment to the viewPager
        fun addFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            fragmentTitles.add(title)
        }

        fun getPageTitle(position: Int): CharSequence = fragmentTitles[position]

        override fun getItemCount(): Int = fragments.size

        override fun createFragment(position: Int): Fragment = fragments[position]
    }

    private fun updateStyling(config: ChatConfig?) {
        config?.chatPanel?.styling?.apply {
            // Update toolbar and TabLayout background color
            primaryColor?.let {
                val primaryColorDrawable = ColorDrawable(it)
                toolbar?.background = primaryColorDrawable
                tabLayout?.background = primaryColorDrawable
                viewPager?.background = primaryColorDrawable
            }

            // Update tab text and selected indicator colors
            contrastColor?.let { contrastColor ->
                tabLayout?.tabTextColors = ColorStateList.valueOf(contrastColor)
                tabLayout?.setSelectedTabIndicatorColor(contrastColor)
            }
        }
    }

    override fun onConfigReceived(backend: ChatBackend, config: ChatConfig) {
        updateStyling(config)
    }

    override fun onFailure(backend: ChatBackend, error: Exception) {}

    override fun onBackendEventReceived(backend: ChatBackend, type: String, detail: JsonElement?) {
        println("Boost backend event: $type, detail: $detail")
    }

    override fun onUIEventReceived(event: BoostUIEvents.Event, detail: Any?) {
        println("Boost UI event: $event, detail: $detail")
    }

}