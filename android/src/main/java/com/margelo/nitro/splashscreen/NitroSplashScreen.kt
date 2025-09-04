package com.margelo.nitro.splashscreen

import com.facebook.proguard.annotations.DoNotStrip

@DoNotStrip
class NitroSplashScreen : HybridNitroSplashScreenSpec() {
    override fun show(theme: String?) {
        NitroSplashScreenManager.instance.show(theme)
    }

    override fun hide() {
        NitroSplashScreenManager.instance.hide()
    }
}
