package com.dgo.checkout.data

import android.content.Context
import org.json.JSONObject

data class TermsSection(val title: String, val body: String)

data class TermsDocument(
    val title: String,
    val effective: String,
    val scope: String,
    val preamble: String,
    val sections: List<TermsSection>,
    val acknowledgement: String,
)

/** Verbatim regional terms shared with the DHGO web prototype. */
object Terms {
    const val PRIVACY_POLICY_URL = "https://www.watchdgo.com/en/page/privacy-policy"

    /** Splits a section body into paragraphs (blank lines) and bullets ("• …"). */
    fun paragraphs(body: String): List<String> =
        body.split(Regex("\\s*\\n\\s*\\n\\s*|\\s+(?=\\d+\\.\\d+\\s|•\\s)")).filter { it.isNotBlank() }

    fun load(context: Context): Map<Boolean, TermsDocument> {
        val root = JSONObject(context.assets.open("terms.json").bufferedReader().use { it.readText() })
        return listOf(false to "nepal", true to "international").associate { (international, key) ->
            val document = root.getJSONObject(key)
            val sections = document.getJSONArray("sections")
            international to TermsDocument(
                title = document.getString("title"),
                effective = document.getString("effective"),
                scope = document.getString("scope"),
                preamble = document.getString("preamble"),
                sections = List(sections.length()) { index ->
                    val section = sections.getJSONObject(index)
                    TermsSection(section.getString("title"), section.getString("body"))
                },
                acknowledgement = document.getString("acknowledgement"),
            )
        }
    }
}
