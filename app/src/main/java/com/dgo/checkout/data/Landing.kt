package com.dgo.checkout.data

import com.dgo.checkout.R

data class TitleCard(
    val title: String,
    val tag: String,
    val year: String = "",
    val rating: String = "",
    val duration: String = "",
    val subtitle: String = "",
    val desc: String = "",
    val poster: Int? = null,
    val hero: Int? = null,
)

data class ContentRail(val title: String, val items: List<TitleCard>)

data class LandingTab(
    val id: String,
    val label: String,
    val color: Long,
    val secondary: Long,
    val hero: List<TitleCard>,
    val rails: List<ContentRail>,
    val partnerEyebrow: String? = null,
    val partnerTagline: String? = null,
)

private fun card(
    title: String,
    tag: String,
    year: String = "",
    rating: String = "",
    duration: String = "",
    subtitle: String = "",
    desc: String = "",
    poster: Int? = null,
    hero: Int? = null,
) = TitleCard(title, tag, year, rating, duration, subtitle, desc, poster, hero)

private val posters = mapOf(
    "Asur" to R.drawable.poster_asur,
    "Asur 2" to R.drawable.poster_asur2,
    "Bajao" to R.drawable.poster_bajao,
    "Empire" to R.drawable.poster_empire,
    "Ghar Waapsi" to R.drawable.poster_ghar,
    "Honeymoon Photographer" to R.drawable.poster_honeymoon,
    "Illegal 2" to R.drawable.poster_illegal,
    "Inspector Avinash" to R.drawable.poster_inspector,
    "Khalbali Records" to R.drawable.poster_khalbali,
    "London Files" to R.drawable.poster_london,
    "Special Ops" to R.drawable.poster_special_ops,
    "Taaza Khabar" to R.drawable.poster_taaza,
)

private val heroes = mapOf(
    "Asur" to R.drawable.hero_asur,
    "Asur 2" to R.drawable.hero_asur2,
    "Special Ops" to R.drawable.hero_special_ops,
    "Taaza Khabar" to R.drawable.hero_taaza,
    "Inspector Avinash" to R.drawable.hero_inspector,
)

private fun show(
    title: String,
    tag: String,
    year: String = "",
    rating: String = "",
    duration: String = "",
    subtitle: String = "",
    desc: String = "",
    fallbackHero: Int? = null,
) = card(
    title, tag, year, rating, duration, subtitle, desc,
    poster = posters[title],
    hero = heroes[title] ?: fallbackHero ?: posters[title],
)

private val home = LandingTab(
    id = "home",
    label = "Home",
    color = 0xFF8A3FFC,
    secondary = 0xFFFF00BD,
    hero = listOf(
        show("Prem Geet", "OSR Digital", "2016", "8.4", "2h 17m", "The film that defined Nepali YouTube", "Pooja Sharma and Pradeep Khadka — the crown jewel of the OSR Digital library.", fallbackHero = R.drawable.hero_osr),
        show("Special Ops", "JioHotstar", "2020", "8.6", "Eps", "Kay Kay Menon · Hotstar Specials", "The flagship thriller from JioHotstar — included with Mobile and Plus."),
        show("Buhari", "OSR Serial", "2026", "8.6", "Eps", "कथा चेलीको · 290+ episodes", "Nepal's most-watched sentimental serial.", fallbackHero = R.drawable.hero_home),
    ),
    rails = listOf(
        ContentRail("From OSR Digital", listOf(
            show("Prem Geet", "Romance", "2016", "8.4", "2h 17m", "Pooja Sharma, Pradeep Khadka", fallbackHero = R.drawable.hero_osr),
            show("Prasad 2", "Drama", "2026", "8.1", "2h 10m", "Bipin Karki, Keki Adhikari", fallbackHero = R.drawable.hero_osr),
            show("Jhingedaau", "Comedy", "2026", "7.8", "2h 05m", fallbackHero = R.drawable.hero_osr),
            show("Behuli from Meghauli", "Drama", "2025", "8.0", "2h 12m", fallbackHero = R.drawable.hero_osr),
            show("Prem Geet 3", "Romance", "2022", "7.6", "2h 22m", fallbackHero = R.drawable.hero_osr),
            show("Buhari", "OSR Serial", "2026", "8.6", "290+ Eps", fallbackHero = R.drawable.hero_home),
        )),
        ContentRail("JioHotstar on DGO", listOf(
            show("Special Ops", "Hotstar Specials", "2020", "8.6", "Eps", "Kay Kay Menon"),
            show("Asur", "Crime", "2020", "8.4", "Eps", "Arshad Warsi, Barun Sobti"),
            show("Asur 2", "Crime", "2023", "8.5", "8 Eps"),
            show("Taaza Khabar", "Fantasy", "2023", "8.1", "Eps", "Bhuvan Bam"),
            show("Inspector Avinash", "Crime", "2023", "7.8", "Eps", "Randeep Hooda"),
            show("Ghar Waapsi", "Drama", "2022", "8.4", "12 Eps"),
        )),
        ContentRail("Serials people finish", listOf(
            show("Buhari", "OSR Serial", "2026", "8.6", "290+ Eps", fallbackHero = R.drawable.hero_home),
            show("Asur", "Hotstar Specials", "2020", "8.4", "Eps"),
            show("Kill Me Heal Me", "K-Drama", "2015", "8.3", "20 Eps"),
            show("Hospital Ship", "Medical", "2017", "7.5", "40 Eps"),
        )),
        ContentRail("Movies tonight", listOf(
            show("Prem Geet", "OSR", "2016", "8.4", "2h 17m", fallbackHero = R.drawable.hero_osr),
            show("Honeymoon Photographer", "JioHotstar", "2024", "7.1", "6 Eps"),
            show("Prasad", "OSR", "2018", "8.2", "2h 15m", fallbackHero = R.drawable.hero_osr),
            show("Empire", "JioHotstar", "2021", "7.3", "8 Eps"),
            show("Imitation Game", "Drama", "2014", "8.0", "1h 54m"),
        )),
    ),
)

private val hotstar = LandingTab(
    id = "hotstar",
    label = "JioHotstar",
    color = 0xFF0B5FFF,
    secondary = 0xFFFF4D9A,
    partnerEyebrow = "Partner hub",
    partnerTagline = "Hotstar Specials — Special Ops, Asur, Taaza Khabar and the rest of the Spark catalogue on DGO.",
    hero = listOf(
        show("Special Ops", "Hotstar Specials", "2020", "8.6", "Eps", "Kay Kay Menon", "A wounded agency and a vanishing asset — the flagship JioHotstar thriller."),
        show("Asur", "Hotstar Specials", "2020", "8.4", "Eps", "Arshad Warsi · Barun Sobti", "Forensic science versus a killer who thinks in myths."),
        show("Taaza Khabar", "Hotstar Specials", "2023", "8.1", "Eps", "Bhuvan Bam", "See tomorrow, pay for it today."),
        show("Inspector Avinash", "Hotstar Specials", "2023", "7.8", "Eps", "Randeep Hooda", "A no-rules UP cop, inspired by true events."),
    ),
    rails = listOf(
        ContentRail("Hotstar Specials", listOf(
            show("Special Ops", "Thriller", "2020", "8.6", "Eps", "Kay Kay Menon"),
            show("Asur", "Crime", "2020", "8.4", "Eps", "Arshad Warsi, Barun Sobti"),
            show("Asur 2", "Crime", "2023", "8.5", "8 Eps"),
            show("Taaza Khabar", "Fantasy", "2023", "8.1", "Eps", "Bhuvan Bam"),
            show("Inspector Avinash", "Crime", "2023", "7.8", "Eps", "Randeep Hooda"),
            show("Illegal 2", "Courtroom", "2021", "7.6", "Eps", "Neha Sharma"),
        )),
        ContentRail("Binge now", listOf(
            show("Asur 2", "Crime", "2023", "8.5", "8 Eps"),
            show("Ghar Waapsi", "Drama", "2022", "8.4", "12 Eps", "Vineet Kumar"),
            show("London Files", "Thriller", "2022", "6.9", "6 Eps", "Arjun Rampal"),
            show("Honeymoon Photographer", "Thriller", "2024", "7.1", "6 Eps", "Asha Negi"),
        )),
        ContentRail("From the vault", listOf(
            show("Bajao", "Comedy", "2023", "7.5", "8 Eps", "Raftaar"),
            show("Khalbali Records", "Music", "2024", "7.4", "8 Eps", "Ram Kapoor"),
            show("Empire", "Historical", "2021", "7.3", "8 Eps", "Kunal Kapoor"),
            show("Taaza Khabar", "Fantasy", "2023", "8.1", "Eps"),
        )),
    ),
)

private val osr = LandingTab(
    id = "osr",
    label = "OSR",
    color = 0xFFE10600,
    secondary = 0xFFF5C518,
    partnerEyebrow = "Partner hub",
    partnerTagline = "Nepali films, music, serials and reality from OSR Digital.",
    hero = listOf(
        show("Prem Geet", "OSR Digital", "2016", "8.4", "2h 17m", "Pooja Sharma · Pradeep Khadka", "The most-viewed Nepali film on YouTube.", fallbackHero = R.drawable.hero_osr),
        show("Prasad 2", "New on OSR Movies", "2026", "8.1", "2h 10m", "Bipin Karki · Keki Adhikari", "The 2026 follow-up, now in the DGO OSR hub.", fallbackHero = R.drawable.hero_osr),
        show("Buhari", "OSR Serial", "2026", "8.6", "290+ Eps", "कथा चेलीको", "Hundreds of episodes, millions of weekly views.", fallbackHero = R.drawable.hero_osr),
    ),
    rails = listOf(
        ContentRail("OSR superhits", listOf(
            show("Prem Geet", "Romance", "2016", "8.4", "2h 17m", fallbackHero = R.drawable.hero_osr),
            show("Prem Geet 2", "Romance", "2018", "8.0", "2h 20m", fallbackHero = R.drawable.hero_osr),
            show("Prem Geet 3", "Romance", "2022", "7.6", "2h 22m", fallbackHero = R.drawable.hero_osr),
            show("Prasad", "Drama", "2018", "8.2", "2h 15m", fallbackHero = R.drawable.hero_osr),
            show("Prasad 2", "Drama", "2026", "8.1", "2h 10m", fallbackHero = R.drawable.hero_osr),
            show("Jhingedaau", "Comedy", "2026", "7.8", "2h 05m", fallbackHero = R.drawable.hero_osr),
            show("Behuli from Meghauli", "Drama", "2025", "8.0", "2h 12m", fallbackHero = R.drawable.hero_osr),
        )),
        ContentRail("New on OSR Movies", listOf(
            show("Gobar Ganesh", "Coming soon", "2026", duration = "Trailer", subtitle = "Barsha Siwakoti", fallbackHero = R.drawable.hero_osr),
            show("Pahad", "Drama", "2026", "7.9", "2h 08m", fallbackHero = R.drawable.hero_osr),
            show("Bar & Badhu", "Drama", "2024", "7.5", "Feature", fallbackHero = R.drawable.hero_osr),
            show("The Break Up", "Romance", "2019", "7.1", "2h 05m", fallbackHero = R.drawable.hero_osr),
        )),
        ContentRail("OSR serials", listOf(
            show("Buhari", "Serial", "2026", "8.6", "290+ Eps", "कथा चेलीको", fallbackHero = R.drawable.hero_home),
            show("Juthe", "Serial", "2026", "8.1", "S2", fallbackHero = R.drawable.hero_home),
            show("Katha Cheliko", "Serial", "2025", "7.8", "Eps", fallbackHero = R.drawable.hero_home),
        )),
    ),
)

private val sports = LandingTab(
    id = "sports",
    label = "Sports",
    color = 0xFF8A3FFC,
    secondary = 0xFFFF4D00,
    hero = listOf(
        show("Asia Cup Tonight", "Cricket", "2026", duration = "Live", subtitle = "Live & highlights", desc = "Live sports is included on 3-month and 12-month plans.", fallbackHero = R.drawable.hero_hotstar),
        show("Premier League Weekend", "Football", "2026", duration = "90 min", fallbackHero = R.drawable.hero_hotstar),
    ),
    rails = listOf(
        ContentRail("Live & highlights", listOf(
            show("Asia Cup Tonight", "Cricket", "2026", duration = "Live", fallbackHero = R.drawable.hero_hotstar),
            show("Premier League Weekend", "Football", "2026", duration = "90 min", fallbackHero = R.drawable.hero_hotstar),
            show("Kabaddi Nationals", "Kabaddi", "2025", duration = "2h 10m", fallbackHero = R.drawable.hero_hotstar),
            show("NSL Matchday", "Football", "2025", duration = "Live", fallbackHero = R.drawable.hero_hotstar),
            show("Court Side", "Basketball", "2026", duration = "Highlights", fallbackHero = R.drawable.hero_hotstar),
        )),
    ),
)

private val entertainment = LandingTab(
    id = "entertainment",
    label = "Entertainment",
    color = 0xFF8A3FFC,
    secondary = 0xFFFF00BD,
    hero = listOf(
        show("Imitation Game", "Drama", "2014", "8.0", "1h 54m", subtitle = "Hindi dubbed & more", fallbackHero = R.drawable.hero_home),
        show("Kill Me Heal Me", "Romance", "2015", "8.3", "20 Eps", fallbackHero = R.drawable.hero_home),
    ),
    rails = listOf(
        ContentRail("Hindi Dubbed", listOf(
            show("After Math", "Action", "2016", "5.3", "1h 30m"),
            show("24 Hours To Live", "Thriller", "2017", "5.8", "1h 33m"),
            show("Imitation Game", "Drama", "2014", "8.0", "1h 54m"),
            show("Killing Them Softly", "Crime", "2012", "6.2", "1h 37m"),
            show("47 Meters Down", "Horror", "2017", "5.6", "1h 29m"),
        )),
        ContentRail("Nepali Movies", listOf(
            show("Kalo Barsa", "Drama", "2018", "7.1", "2h 10m", fallbackHero = R.drawable.hero_osr),
            show("Mann Manai Manparaye", "Romance", "2019", "6.5", "2h 5m", fallbackHero = R.drawable.hero_osr),
            show("Teen Ghumti", "Classic", "2015", "7.5", "2h 15m", fallbackHero = R.drawable.hero_osr),
            show("Hasideu Ek Fera", "Comedy", "2020", "7.0", "1h 55m", fallbackHero = R.drawable.hero_osr),
        )),
        ContentRail("Korean Drama", listOf(
            show("Bad Papa", "Drama", "2018", "7.8", "16 Eps"),
            show("Hospital Ship", "Medical", "2017", "7.5", "40 Eps"),
            show("Kill Me Heal Me", "Romance", "2015", "8.3", "20 Eps"),
            show("Sweet Revenge", "Teen", "2017", "7.2", "22 Eps"),
            show("Two Cops", "Fantasy", "2017", "7.3", "32 Eps"),
        )),
    ),
)

private val specials = LandingTab(
    id = "specials",
    label = "Specials",
    color = 0xFF8A3FFC,
    secondary = 0xFFFF00BD,
    hero = listOf(
        show("Documentary: The Himalayas", "Documentary", "2023", "9.2", "1h 45m", subtitle = "Premium rental", desc = "Unlock a single premium title. Separate from a DGO subscription.", fallbackHero = R.drawable.hero_home),
    ),
    rails = listOf(
        ContentRail("Exclusive Specials", listOf(
            show("Comedy Night Live", "Comedy", "2024", "8.5", "1h 30m"),
            show("Music Awards 2024", "Music", "2024", "9.0", "3h 00m"),
            show("Documentary: The Himalayas", "Documentary", "2023", "9.2", "1h 45m", fallbackHero = R.drawable.hero_home),
        )),
    ),
)

private val junior = LandingTab(
    id = "junior",
    label = "Junior",
    color = 0xFF22D3EE,
    secondary = 0xFF8A3FFC,
    hero = listOf(
        show("Boonie Bears Homeward Journey", "Family", "2013", "7.0", "1h 08m", subtitle = "Kids choice", fallbackHero = R.drawable.hero_home),
    ),
    rails = listOf(
        ContentRail("Kids Choice", listOf(
            show("Sir Billi", "Animation", "2012", "4.5", "1h 20m"),
            show("Atomicron", "Action", "2014", "6.0", "1h 10m"),
            show("Dinofroz The Origin", "Adventure", "2015", "6.5", "1h 15m"),
            show("Boonie Bears Homeward Journey", "Family", "2013", "7.0", "1h 08m"),
            show("Felix All Around The World", "Adventure", "2005", "5.8", "1h 22m"),
        )),
    ),
)

object LandingCatalog {
    val tabs: List<LandingTab> = listOf(home, hotstar, osr, sports, entertainment, specials, junior)

    fun tab(id: String): LandingTab = tabs.firstOrNull { it.id == id } ?: home

    fun search(query: String): List<TitleCard> {
        val q = query.trim().lowercase()
        if (q.isBlank()) return emptyList()
        return tabs.flatMap { tab -> tab.rails.flatMap { rail -> rail.items } + tab.hero }
            .distinctBy { it.title }
            .filter {
                it.title.lowercase().contains(q) ||
                    it.tag.lowercase().contains(q) ||
                    it.subtitle.lowercase().contains(q)
            }
            .take(20)
    }
}
