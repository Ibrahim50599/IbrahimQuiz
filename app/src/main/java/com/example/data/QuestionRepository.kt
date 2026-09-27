package com.example.data

import android.content.Context
import com.example.model.QuizCategory
import com.example.model.QuizQuestion
import org.json.JSONObject

class QuestionRepository(private val context: Context) {

    private var cachedQuestions: List<QuizQuestion> = emptyList()
    private var cachedCategories: List<QuizCategory> = emptyList()

    init {
        loadData()
    }

    private fun loadData() {
        try {
            val jsonString = context.assets.open("questions.json").bufferedReader().use { it.readText() }
            val root = JSONObject(jsonString)

            // Categories
            val categoriesJson = root.optJSONArray("categories")
            val categoriesList = mutableListOf<QuizCategory>()
            if (categoriesJson != null) {
                for (i in 0 until categoriesJson.length()) {
                    val catObj = categoriesJson.getJSONObject(i)
                    val id = catObj.getString("id")
                    val nameObj = catObj.getJSONObject("name")
                    val nameMap = mutableMapOf<String, String>()
                    val keys = nameObj.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        nameMap[key] = nameObj.getString(key)
                    }
                    categoriesList.add(QuizCategory(id, nameMap))
                }
            }
            cachedCategories = categoriesList

            // Questions
            val questionsJson = root.optJSONArray("questions")
            val questionsList = mutableListOf<QuizQuestion>()
            if (questionsJson != null) {
                for (i in 0 until questionsJson.length()) {
                    val qObj = questionsJson.getJSONObject(i)
                    val id = qObj.getString("id")
                    val category = qObj.getString("category")

                    // categoryName
                    val catNameObj = qObj.getJSONObject("categoryName")
                    val catNameMap = mutableMapOf<String, String>()
                    val catKeys = catNameObj.keys()
                    while (catKeys.hasNext()) {
                        val k = catKeys.next()
                        catNameMap[k] = catNameObj.getString(k)
                    }

                    // question text
                    val qTextObj = qObj.getJSONObject("question")
                    val qTextMap = mutableMapOf<String, String>()
                    val qKeys = qTextObj.keys()
                    while (qKeys.hasNext()) {
                        val k = qKeys.next()
                        qTextMap[k] = qTextObj.getString(k)
                    }

                    // options
                    val optObj = qObj.getJSONObject("options")
                    val optMap = mutableMapOf<String, List<String>>()
                    val optKeys = optObj.keys()
                    while (optKeys.hasNext()) {
                        val k = optKeys.next()
                        val arr = optObj.getJSONArray(k)
                        val list = mutableListOf<String>()
                        for (j in 0 until arr.length()) {
                            list.add(arr.getString(j))
                        }
                        optMap[k] = list
                    }

                    val correctIndex = qObj.getInt("correctIndex")

                    // explanation
                    val expObj = qObj.getJSONObject("explanation")
                    val expMap = mutableMapOf<String, String>()
                    val expKeys = expObj.keys()
                    while (expKeys.hasNext()) {
                        val k = expKeys.next()
                        expMap[k] = expObj.getString(k)
                    }

                    // hint (optional)
                    val hintMap = mutableMapOf<String, String>()
                    val hintObj = qObj.optJSONObject("hint")
                    if (hintObj != null) {
                        val hKeys = hintObj.keys()
                        while (hKeys.hasNext()) {
                            val k = hKeys.next()
                            hintMap[k] = hintObj.getString(k)
                        }
                    }

                    questionsList.add(
                        QuizQuestion(
                            id = id,
                            category = category,
                            categoryName = catNameMap,
                            question = qTextMap,
                            options = optMap,
                            correctIndex = correctIndex,
                            explanation = expMap,
                            hint = hintMap
                        )
                    )
                }
            }
            cachedQuestions = questionsList
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAllCategories(): List<QuizCategory> = cachedCategories

    fun getQuestions(
        categoryId: String = "all",
        count: Int = 10,
        shuffle: Boolean = true,
        shuffleOptions: Boolean = true
    ): List<QuizQuestion> {
        val filtered = if (categoryId == "all" || categoryId.isBlank()) {
            cachedQuestions
        } else {
            cachedQuestions.filter { it.category.equals(categoryId, ignoreCase = true) }
        }

        val pool = if (shuffle) filtered.shuffled() else filtered
        val selected = if (count > 0 && count < pool.size) {
            pool.take(count)
        } else {
            pool
        }

        return if (shuffleOptions) {
            selected.map { it.withShuffledOptions() }
        } else {
            selected
        }
    }

    fun getDailyWisdomList(): List<com.example.model.DailyWisdom> {
        return listOf(
            com.example.model.DailyWisdom(
                title = mapOf(
                    "pt" to "A Força da Confissão",
                    "en" to "The Power of Confession",
                    "sn" to "Simba reKureurura"
                ),
                teaching = mapOf(
                    "pt" to "«A confissão sincera de pecados (Kureurura) diante de Deus traz a cura do corpo e a libertação da alma.»",
                    "en" to "«Sincere confession of sins (Kureurura) before God brings healing to the body and deliverance to the soul.»",
                    "sn" to "«Kureurura zvivi nemwoyo wose pamberi paMwari kunounza kuporeswa kwemuviri nekusunungurwa kwemweya.»"
                ),
                reference = "Ensinamentos de Mai Chaza (1954)"
            ),
            com.example.model.DailyWisdom(
                title = mapOf(
                    "pt" to "Fé Pura em Jeová",
                    "en" to "Pure Faith in Jehovah",
                    "sn" to "Kutenda Kwakachena muna Jehovha"
                ),
                teaching = mapOf(
                    "pt" to "«Confie inteiramente em Deus Altíssimo. Não recorra a feitiçarias, amuletos ou remédios tradicionais; a oração com retidão tudo supera.»",
                    "en" to "«Trust completely in the Almighty God. Do not turn to charms or traditional sorcery; righteous prayer overcomes all.»",
                    "sn" to "«Vimba naMwari Wemasimbaose. Usatendeuke kumazango kana mishonga yakaipa; munamato wakarurama unokunda zvese.»"
                ),
                reference = "Doutrina Sagrada GRJ"
            ),
            com.example.model.DailyWisdom(
                title = mapOf(
                    "pt" to "Amor Fraternal e Paz",
                    "en" to "Brotherly Love and Peace",
                    "sn" to "Rudo rweHama neRugare"
                ),
                teaching = mapOf(
                    "pt" to "«A veste branca que usamos exige um coração branco e puro: sem rancor, sem mentiras e com perdão sincero entre irmãos.»",
                    "en" to "«The white robe we wear demands a white and pure heart: free of malice, free of deceit, and full of sincere forgiveness among brethren.»",
                    "sn" to "«Nguo chena yatakapfeka inoda mwoyo wakachena: usina mafi, usina unyengeri, une ruregerero pakati pehama.»"
                ),
                reference = "Dzimbahwe Zvimba"
            )
        )
    }

    fun getChurchHymns(): List<com.example.model.ChurchHymn> {
        return listOf(
            com.example.model.ChurchHymn(
                id = "hymn_01",
                title = mapOf(
                    "sn" to "Ndinotenda Jehovha Mwari Wangu",
                    "pt" to "Agradeço a Jeová Meu Deus",
                    "en" to "I Thank Jehovah My God"
                ),
                context = mapOf(
                    "pt" to "Entoado em reverência para agradecer a Deus pela cura de doenças incuráveis, libertação de espíritos e paz no lar.",
                    "en" to "Sung in reverence to thank God for healing incurable illnesses, deliverance from evil spirits, and peace at home.",
                    "sn" to "Inoimbwa nenyasha dzekutenda Jehovha nekuporeswa kwezvirwere, kusunungurwa nemweya yetsvina uye rugare mumusha."
                ),
                lyricsShona = listOf(
                    "1. Ndinotenda Jehovha Mwari wangu,",
                    "   Wakanzwa kuchema kwangu,",
                    "   Wakandiponesa murima guru,",
                    "   Ndinokurumbidza Jehovha.",
                    "2. Vakanga vaora vakaporeswa,",
                    "   Vakanga vachichema vanyaradzwa,",
                    "   Guta raJehovha idendere renyasha,",
                    "   Ngaakudzwe Mwari Baba."
                ),
                lyricsPt = listOf(
                    "1. Agradeço a Jeová meu Deus,",
                    "   Que ouviu o meu clamor,",
                    "   Livrou-me da escuridão profunda,",
                    "   Eu te louvo, ó Jeová.",
                    "2. Os que definhavam foram curados,",
                    "   Os que choravam foram consolados,",
                    "   A Cidade de Jeová é refúgio de graça,",
                    "   Louvado seja Deus Pai."
                ),
                lyricsEn = listOf(
                    "1. I thank Jehovah my God,",
                    "   Who heard my weeping cry,",
                    "   Delivered me from the deep darkness,",
                    "   I give praise to Thee, Jehovah.",
                    "2. Those who were ailing were restored,",
                    "   Those weeping found sweet solace,",
                    "   The City of Jehovah is sanctuary of grace,",
                    "   Glory be to God the Father."
                ),
                soundType = com.example.util.SoundManager.SoundType.CHURCH_CHOIR
            ),
            com.example.model.ChurchHymn(
                id = "hymn_02",
                title = mapOf(
                    "sn" to "Bhero reKunamata muDzimbahwe",
                    "pt" to "O Sino Sagrado da Oração",
                    "en" to "The Sacred Prayer Bell of Dzimbahwe"
                ),
                context = mapOf(
                    "pt" to "O sino solene que toca no santuário sagrado de Zvimba convocando os fiéis a descalçar os sapatos, manter silêncio e orar de coração puro.",
                    "en" to "The solemn bell echoing through Zvimba shrine calling believers to remove shoes, observe quietude and pray with pure hearts.",
                    "sn" to "Bhero dzvene rinorira muDzimbahwe reZvimba richikoka vatendi kupfeka nguo chena, kubvisa shangu nekunamata zvakadzama."
                ),
                lyricsShona = listOf(
                    "1. Bhero dzvene rorira muGuta,",
                    "   Vana vaJehovha sunganai,",
                    "   Bvisai shangu, garai pamwe,",
                    "   Jehovha ari pakati pedu.",
                    "2. Mweya mutsvene unodururwa,",
                    "   Kune vose vanochema nezvivi,",
                    "   Namatirai vana nevese vanorwara,",
                    "   Ngaizviitwe, Ngaizviitwe."
                ),
                lyricsPt = listOf(
                    "1. O sino sagrado ressoa na Cidade,",
                    "   Filhos de Jeová, uni-vos em oração,",
                    "   Descalçai os pés, assentai-vos em paz,",
                    "   Jeová está entre nós.",
                    "2. O Espírito Santo é derramado,",
                    "   Sobre os que confessam os seus pecados,",
                    "   Orai pelos filhos e pelos doentes,",
                    "   Que assim seja (Ngaizviitwe)."
                ),
                lyricsEn = listOf(
                    "1. The holy bell sounds across the City,",
                    "   Children of Jehovah, unite in communion,",
                    "   Remove your sandals, sit in silence,",
                    "   Jehovah is present among us.",
                    "2. The Holy Spirit is poured forth,",
                    "   Upon all who repent of their misdeeds,",
                    "   Pray for the young and the infirm,",
                    "   May it be so (Ngaizviitwe)."
                ),
                soundType = com.example.util.SoundManager.SoundType.CHURCH_BELL
            ),
            com.example.model.ChurchHymn(
                id = "hymn_03",
                title = mapOf(
                    "sn" to "Kureurura Kunounza Rugare",
                    "pt" to "A Confissão Traz a Paz",
                    "en" to "Confession Brings Peace"
                ),
                context = mapOf(
                    "pt" to "Cântico que recorda o mandamento central da Guta raJehovah: expor e abandonar os pecados ocultos diante do altar para que a cura se manifeste.",
                    "en" to "Sacred hymn upholding the central commandment of Guta raJehovah: unveiling and renouncing hidden transgressions before the altar.",
                    "sn" to "Nziyo inoyeuchidza mutemo mukuru wekureurura zvivi pachena (Kureurura) kuti muviri nemweya zvisunungurwe pachena."
                ),
                lyricsShona = listOf(
                    "1. Usavanza zvivi mwoyo wako uchirema,",
                    "   Reurura zviri pachena kuna Mwari,",
                    "   Jehovha anonzwa uye anoporesa,",
                    "   Rugare runouya semupata wemvura.",
                    "2. Rasa mishonga nemazango ekare,",
                    "   Tenda muna Jehovha oga,",
                    "   Simba rake harienzaniswi,",
                    "   Kuuchirai Jehovha nemufaro."
                ),
                lyricsPt = listOf(
                    "1. Não ocultes as culpas enquanto o coração sofre,",
                    "   Confessa abertamente diante de Deus,",
                    "   Jeová escuta e sara as feridas,",
                    "   A paz desce como água fresca.",
                    "2. Lança fora amuletos e mandingas de feitiço,",
                    "   Crê somente no Senhor Jeová,",
                    "   O Seu poder não tem comparação,",
                    "   Batei palmas com júbilo a Jeová."
                ),
                lyricsEn = listOf(
                    "1. Conceal not thy guilt while the heart weighs heavy,",
                    "   Confess openly in the sight of God,",
                    "   Jehovah listens and heals every affliction,",
                    "   Peace descendeth like refreshing water.",
                    "2. Cast away talismans and medicines of sorcery,",
                    "   Have faith in Jehovah alone,",
                    "   His divine strength has no equal,",
                    "   Clap hands in joyful praise to Jehovah."
                ),
                soundType = com.example.util.SoundManager.SoundType.CHURCH_CLAPPING
            )
        )
    }

    fun getAchievements(): List<com.example.model.Achievement> {
        return listOf(
            com.example.model.Achievement(
                id = "first_quiz",
                title = mapOf("pt" to "Primeiro Passo", "en" to "First Step", "sn" to "Nhanho Yekutanga"),
                description = mapOf("pt" to "Concluiu seu primeiro quiz na Guta raJehovah", "en" to "Completed your first GRJ quiz", "sn" to "Wapedza mutambo wako wekutanga"),
                iconEmoji = "🌱"
            ),
            com.example.model.Achievement(
                id = "streak_3",
                title = mapOf("pt" to "Fogo da Sabedoria", "en" to "Fire of Wisdom", "sn" to "Moto weRuzivo"),
                description = mapOf("pt" to "Alcançou 3 respostas corretas seguidas", "en" to "Reached 3 correct answers in a row", "sn" to "Wakatevedzanisa 3 zvakanaka"),
                iconEmoji = "🔥"
            ),
            com.example.model.Achievement(
                id = "streak_5",
                title = mapOf("pt" to "Luz Celestial", "en" to "Heavenly Light", "sn" to "Chiedza cheKudenga"),
                description = mapOf("pt" to "Alcançou 5 acertos seguidos sem hesitar", "en" to "Reached 5 correct answers in a row", "sn" to "Wakatevedzanisa 5 zvakanaka"),
                iconEmoji = "⭐"
            ),
            com.example.model.Achievement(
                id = "perfect_score",
                title = mapOf("pt" to "Mestre de Zvimba", "en" to "Master of Zvimba", "sn" to "Nyanzvi yeZvimba"),
                description = mapOf("pt" to "Obteve 100% de precisão numa rodada completa", "en" to "Achieved 100% accuracy in a quiz", "sn" to "Wakapindura zvese nemazvo 100%"),
                iconEmoji = "🏆"
            ),
            com.example.model.Achievement(
                id = "duel_master",
                title = mapOf("pt" to "Campeão do Duelo", "en" to "Duel Champion", "sn" to "Gamba reDuelo"),
                description = mapOf("pt" to "Venceu uma disputa no modo 2 jogadores", "en" to "Won a head-to-head 2-player battle", "sn" to "Wakakunda muduelo revatambi 2"),
                iconEmoji = "⚔️"
            ),
            com.example.model.Achievement(
                id = "church_sounds",
                title = mapOf("pt" to "Coração Devoto", "en" to "Devout Heart", "sn" to "Mwoyo weKunamata"),
                description = mapOf("pt" to "Escutou e meditou com os sons sagrados da igreja", "en" to "Listened and meditated with church sounds", "sn" to "Wateerera mutinhimira wechechi"),
                iconEmoji = "🔔"
            )
        )
    }
