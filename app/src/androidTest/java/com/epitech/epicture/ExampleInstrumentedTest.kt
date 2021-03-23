package com.epitech.epicture

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import com.epitech.epicture.config.Config
import com.epitech.epicture.config.Config.Companion.REDIRECT_URI
import com.epitech.epicture.config.Config.Companion.CLIENT_ID
import com.epitech.epicture.config.Config.Companion.CLIENT_SECRET

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.epitech.epicture", appContext.packageName)
    }

    @Test
    fun verifClientID(){
        assertEquals("70782f45d7b1c5b",CLIENT_ID)
    }
    @Test
    fun verifClientSecret(){
        assertEquals("c7b3127989839444efd4a21899dba5381ee89267",CLIENT_SECRET)
    }
    @Test
    fun verifRedirectURI(){
        assertEquals("https://www.epicture.eu/callback",REDIRECT_URI)
    }

}