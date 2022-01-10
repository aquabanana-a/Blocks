package com.fromfinalform.blocks.core.locale

import com.fromfinalform.blocks.locale.R
import android.app.Application
import android.content.Context
import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import com.lokalise.sdk.Lokalise
import com.lokalise.sdk.LokaliseContextWrapper
import com.lokalise.sdk.LokaliseResources
import java.util.Locale

object LocaleUtils {
    private const val TAG = "LocaleUtils"

    private const val RU = "ru"
    private const val EN = "en"
    private const val FR = "fr"
    private const val ES = "es"
    private const val HI = "hi"
    private const val DE = "de"
    private const val IT = "it"
    private const val VI = "vi"
    private const val PT = "pt"
    private const val KO = "ko"

    private lateinit var app: Application
    private lateinit var lokaliseResources: LokaliseResources

    var userLanguage: String? = null; private set

    @Suppress("ConstantConditionIf")
    @JvmStatic
    private val languageCodes: Array<String> by lazy {
        arrayOf(RU, EN, FR, ES, HI, DE, IT, VI, PT, KO)
    }

    @JvmStatic
    fun getLanguageFlagImageResId(languageCode: String): Int {
        return when (languageCode) {
            RU -> R.drawable.ic_russia
            EN -> R.drawable.ic_united_kingdom
            FR -> R.drawable.ic_france
            ES -> R.drawable.ic_spain
            HI -> R.drawable.ic_india
            DE -> R.drawable.ic_germany
            IT -> R.drawable.ic_italy
            VI -> R.drawable.ic_vietnam
            PT -> R.drawable.ic_portugal
            KO -> R.drawable.ic_korea
            else -> android.R.color.transparent
        }
    }

    fun initLocale(application: Application, language: String?) {
        this.app = application
        this.lokaliseResources = LokaliseResources(app)
        this.userLanguage = language ?: Locale.getDefault().language

        Lokalise.init(
            application,
            app.resources.getString(R.string.lokalise_sdk_token),
            app.resources.getString(R.string.lokalise_project_id)
        )
        Lokalise.setLocale(userLanguage!!)
        Lokalise.updateTranslations()
    }

    @JvmStatic
    fun getLocaleString(@StringRes resId: Int): String {
        return lokaliseResources.getString(resId)
    }

    @JvmStatic
    fun getLocaleString(resKey: String): String {
        return lokaliseResources.getString(resKey) ?: ""
    }

    fun wrapActivityContext(newBase: Context): Context {
        return LokaliseContextWrapper.wrap(newBase)
    }

    fun translateToolbar(resources: Resources, toolbar: Toolbar?) {
        if (toolbar == null) return
        (resources as? LokaliseResources)?.translateToolbarItems(toolbar)
    }
}