package com.example.boostai_app

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.adapter.FragmentStateAdapter
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
                    primaryColor = getColor(R.color.primaryColor),
                    contrastColor = getColor(R.color.contrastColor),
                    chatBubbles = ChatBubbles(
                        vaTextColor = getColor(R.color.vaTextColor),
                        vaBackgroundColor = getColor(R.color.vaBackgroundColor)
                    ),
                    buttons = Buttons(
                        multiline = true,
                        textColor = getColor(R.color.buttonTextColor) // Set the font color for buttons here
                    )
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

        if (toolbar != null && viewPager != null && tabLayout != null) {
            setSupportActionBar(toolbar)
            tabLayout!!.background = ColorDrawable(getColor(R.color.purple))

            // Create viewPager adapter
            val adapter = ViewPagerAdapter(this).apply {
                addFragment(ChatViewFragment(customConfig = customConfig), getString(R.string.fullscreen))
                addFragment(FloatingAvatarFragment(customConfig = customConfig), getString(R.string.avatar))
            }

            viewPager!!.adapter = adapter

            TabLayoutMediator(tabLayout!!, viewPager!!) { tab, position ->
                tab.text = adapter.getPageTitle(position)
            }.attach()
        } else {
            Log.e("MainActivity", "Failed to initialize UI components")
        }

        tabLayout?.background = ColorDrawable(getColor(R.color.purple))

        updateStyling(ChatBackend.config)
        ChatBackend.addConfigObserver(this)
        ChatBackend.addEventObserver(this)
        BoostUIEvents.addObserver(this)
    }

    private inner class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

        private val fragments = mutableListOf<Fragment>()
        private val fragmentTitles = mutableListOf<String>()

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

    override fun onDestroy() {
        super.onDestroy()
        ChatBackend.removeConfigObserver(this)
        ChatBackend.removeEventObserver(this)
        BoostUIEvents.removeObserver(this)
    }

    override fun onUIEventReceived(event: BoostUIEvents.Event, detail: Any?) {
        println("Boost UI event: $event, detail: $detail")
    }

}
