package com.syntxr.anohikari3.data.kotpref

import com.chibatching.kotpref.KotprefModel
import com.chibatching.kotpref.enumpref.enumOrdinalPref

object UserPreferences : KotprefModel() {

    var isDarkTheme by booleanPref(true)
    var currentLanguage by enumOrdinalPref(Language.ID)
    var currentQori by enumOrdinalPref(Qori.ABD_SUDAIS)
    var isTajweed by booleanPref(true)
    var drawerGesture by booleanPref(false)
    var isOnBoarding by booleanPref(true)

    val a = "18.00".toDouble()

    override fun clear() {
        super.clear()
        isDarkTheme = true
        currentLanguage = Language.ID
        currentQori = Qori.ABD_SUDAIS
        isTajweed = true
        drawerGesture = false
    }

    enum class Language(
        val tag: String,
        val language: String
    ){
        ID(
            "id",
            "Indonesia"
        ),
        EN(
            "en-US",
            "English"
        )
    }
    enum class Qori(
        val qoriName: String,
        val url: String
    ){
        ABD_SUDAIS(
            qoriName = "Abdurrahman As-Sudais",
            url = "Abdurrahmaan_As-Sudais_64kbps",
        ),
        HUDHAIFY(
            qoriName = "Ali bin Abdurrahman al-Hudzaifi",
            url = "Hudhaify_64kbps"
        ),
        ALAFASY(
            qoriName = "Mishari Alafasy",
            url = "Alafasy_64kbps"
        ),
        HANI_RIFAI(
            qoriName = "Hani Ar-Rifai",
            url = "Hani_Rifai_64kbps"
        ),
        IBRAHIM_AKHDAR(
            qoriName = "Ibrahim Al-Akhdar",
            url = "Ibrahim_Akhdar_64kbps"
        )
    }
}

//https://www.everyayah.com/data/