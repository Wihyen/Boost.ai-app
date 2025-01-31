package com.example.boostai_app

import android.app.Application
import com.glia.androidsdk.SiteApiKey
import com.glia.widgets.GliaWidgets
import com.glia.widgets.GliaWidgetsConfig

class Application : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize GliaWidgets
        GliaWidgets.onAppCreate(this)

        // Set the custom card adapter
        GliaWidgets.setCustomCardAdapter(ExampleCustomCardAdapter())

        // Configure Glia with required settings
        val gliaConfig = GliaWidgetsConfig.Builder()
            .setCompanyName("boost.ai")
            .setSiteApiKey(
                SiteApiKey(
                    "2f9135dc-76bb-478a-8e7a-6bf3c666b0b7",
                    "gls_REg7vgWlGcAOeQ5yJgl020uKgPEk7e4nYvF9"
                )
            )
            .setSiteId("05e25516-4ef2-4c7c-9efd-15d14807ff3b")
            .setRegion("us")
            .setContext(applicationContext)
            .build()

        // Initialize GliaWidgets with the configuration
        GliaWidgets.init(gliaConfig)
    }
}