package com.example.model

data class QuizQuestion(
    val id: String,
    val category: String,
    val categoryName: Map<String, String>,
    val question: Map<String, String>,
    val options: Map<String, List<String>>,
    val correctIndex: Int,
    val explanation: Map<String, String>,
    val hint: Map<String, String> = emptyMap()
) {
    fun getCategoryTitle(language: Language): String {
        return categoryName[language.code] ?: categoryName["pt"] ?: category
    }

    fun getQuestionText(language: Language): String {
        return question[language.code] ?: question["pt"] ?: ""
    }

    fun getOptionList(language: Language): List<String> {
        return options[language.code] ?: options["pt"] ?: emptyList()
    }

    fun getExplanationText(language: Language): String {
        return explanation[language.code] ?: explanation["pt"] ?: ""
    }

    fun getHintText(language: Language): String {
        val specific = hint[language.code] ?: hint["pt"]
        if (!specific.isNullOrBlank()) return specific

        // Intelligent fallback hint based on category
        return when (category) {
            "history" -> when (language) {
                Language.PORTUGUESE -> "💡 Dica: Lembre-se do ministério de Mai Chaza (Matenga) e a descida sagrada de 1954 em Zvimba."
                Language.ENGLISH -> "💡 Clue: Recall Mai Chaza's (Matenga) ministry and the holy descent of 1954 in Zvimba."
                Language.SHONA -> "💡 Zano: Yeuka ushumiri hwaMai Chaza (Matenga) nekuvamba kutsvene kwa1954 kuZvimba."
            }
            "teachings" -> when (language) {
                Language.PORTUGUESE -> "💡 Dica: A Guta raJehovah baseia-se na confissão sincera de pecados (Kureurura) e obediência total aos Mandamentos de Deus."
                Language.ENGLISH -> "💡 Clue: Guta raJehovah is grounded in sincere confession of sins (Kureurura) and obedience to God's Commandments."
                Language.SHONA -> "💡 Zano: Guta raJehovah rakavakirwa pakureurura zvivi nemwoyo wose nekuteerera mirau yaJehovha."
            }
            "sacred_places" -> when (language) {
                Language.PORTUGUESE -> "💡 Dica: O quartel-general sagrado principal (Dzimbahwe) fica no distrito de Zvimba, no Zimbábue."
                Language.ENGLISH -> "💡 Clue: The principal holy headquarters (Dzimbahwe) is located in Zvimba district, Zimbabwe."
                Language.SHONA -> "💡 Zano: Dzimbahwe guru reGuta raJehovah riri muDunhu reZvimba, muZimbabwe."
            }
            "practices" -> when (language) {
                Language.PORTUGUESE -> "💡 Dica: A pureza espiritual reflete-se na veste branca sagrada sem sapatos no santuário e na rejeição total de feitiçarias."
                Language.ENGLISH -> "💡 Clue: Spiritual purity is reflected in holy white attire without shoes in the shrine and total rejection of witchcraft."
                Language.SHONA -> "💡 Zano: Kuchena kwemweya kunoratidzwa nenguvo chena pasina shangu mualtari nekurasa mashiripiti ese."
            }
            else -> when (language) {
                Language.PORTUGUESE -> "💡 Dica: Pense na fé inabalável em Jeová Deus Altíssimo e na cura divina sem medicamentos."
                Language.ENGLISH -> "💡 Clue: Think of unwavering faith in Jehovah the Almighty God and divine healing without medications."
                Language.SHONA -> "💡 Zano: Funga nezvekutenda muna Mwari Jehovha nekuporeswa pasina mishonga."
            }
        }
    }

    fun withShuffledOptions(): QuizQuestion {
        val sampleList = options.values.firstOrNull() ?: return this
        if (sampleList.size <= 1) return this
        val permutation = sampleList.indices.toList().shuffled()
        val newCorrectIndex = permutation.indexOf(correctIndex)
        val newOptions = options.mapValues { (_, list) ->
            if (list.size == permutation.size) {
                permutation.map { list[it] }
            } else {
                list
            }
        }
        return this.copy(
            options = newOptions,
            correctIndex = if (newCorrectIndex != -1) newCorrectIndex else correctIndex
        )
    }
}

data class QuizCategory(
    val id: String,
    val name: Map<String, String>
) {
    fun getName(language: Language): String {
        return name[language.code] ?: name["pt"] ?: id
    }
}

data class QuestionReview(
    val question: QuizQuestion,
    val selectedIndex: Int, // -1 if timed out
    val isCorrect: Boolean,
    val timeSpentSeconds: Int
)

data class CategoryPerformance(
    val categoryId: String,
    val categoryTitle: String,
    val total: Int,
    val correct: Int
) {
    val accuracyPercent: Int
        get() = if (total > 0) ((correct * 100f) / total).toInt() else 0
}

data class SoloQuizSummary(
    val totalQuestions: Int,
    val correctAnswers: Int,
    val score: Int,
    val highestStreak: Int,
    val timeTakenSeconds: Int,
    val categoryName: String,
    val reviews: List<QuestionReview>
) {
    val accuracyPercent: Int
        get() = if (totalQuestions > 0) ((correctAnswers * 100f) / totalQuestions).toInt() else 0

    fun getRankBadge(language: Language): String {
        return when {
            accuracyPercent == 100 -> when (language) {
                Language.PORTUGUESE -> "🏆 Campeão da Fé GRJ"
                Language.ENGLISH -> "🏆 GRJ Faith Champion"
                Language.SHONA -> "🏆 Gamba reKutenda kweGuta"
            }
            accuracyPercent >= 80 -> when (language) {
                Language.PORTUGUESE -> "⭐ Mutungamiri (Líder da Fé)"
                Language.ENGLISH -> "⭐ Mutungamiri (Faith Leader)"
                Language.SHONA -> "⭐ Mutungamiri weKutenda"
            }
            accuracyPercent >= 50 -> when (language) {
                Language.PORTUGUESE -> "📜 Mupupuri (Testemunha Fiel)"
                Language.ENGLISH -> "📜 Mupupuri (Faithful Witness)"
                Language.SHONA -> "📜 Mupupuri weChokwadi"
            }
            else -> when (language) {
                Language.PORTUGUESE -> "🌱 Mudzidzi (Aprendiz Sincero)"
                Language.ENGLISH -> "🌱 Mudzidzi (Sincere Learner)"
                Language.SHONA -> "🌱 Mudzidzi weGuta"
            }
        }
    }

    fun getCategoryBreakdown(language: Language): List<CategoryPerformance> {
        val groups = reviews.groupBy { it.question.category }
        return groups.map { (catId, revList) ->
            val title = revList.firstOrNull()?.question?.getCategoryTitle(language) ?: catId
            val correct = revList.count { it.isCorrect }
            CategoryPerformance(
                categoryId = catId,
                categoryTitle = title,
                total = revList.size,
                correct = correct
            )
        }.sortedByDescending { it.total }
    }

    fun getSmartDiagnosis(language: Language): String {
        return when {
            accuracyPercent >= 90 -> when (language) {
                Language.PORTUGUESE -> "Inteligência Espiritual Excepcional! Você domina com clareza a história sagrada de 1954, os mandamentos de Deus e as práticas sagradas da Guta raJehovah."
                Language.ENGLISH -> "Exceptional Spiritual Insight! You have mastered the 1954 sacred history, God's commandments, and the practices of Guta raJehovah."
                Language.SHONA -> "Hungwaru HweKutenda Hunoshamisa! Unonyatsoziva nhoroondo tsvene ya1954, mirau yaMwari nemaitiro matsvene eGuta raJehovah."
            }
            accuracyPercent >= 70 -> when (language) {
                Language.PORTUGUESE -> "Conhecimento Sólido e Louvável! Você compreende os pilares centrais da fé, a confissão dos pecados (Kureurura) e o papel de Mai Chaza."
                Language.ENGLISH -> "Solid & Commendable Knowledge! You understand the pillars of faith, confession of sins (Kureurura), and Mai Chaza's legacy."
                Language.SHONA -> "Ruzivo Rwakasimba! Unonzwisisa mbiru dzekutenda, kureurura zvivi (Kureurura) nebasa dzvene raMai Chaza."
            }
            accuracyPercent >= 40 -> when (language) {
                Language.PORTUGUESE -> "Bom Aprendizado em Andamento! O seu caminho de sabedoria na palavra de Jeová está a florescer. Recomendamos rever os ensinamentos fundamentais."
                Language.ENGLISH -> "Good Learning in Progress! Your journey of wisdom in Jehovah's word is growing. We recommend reviewing core doctrines."
                Language.SHONA -> "Kudzidza Kwakanaka! Nzira yako yeruzivo muna Jehovha iri kukura. Zvinokurudzirwa kudzokorora nhoroondo nemirau."
            }
            else -> when (language) {
                Language.PORTUGUESE -> "Primeiros Passos no Conhecimento! Toda grande jornada na fé começa com a humildade de aprender a história de Zvimba e a pureza moral."
                Language.ENGLISH -> "First Steps in Divine Wisdom! Every great journey begins with the humility to learn the sacred history of Zvimba and moral purity."
                Language.SHONA -> "Nhanho dzekutanga muruzivo! Rwendo rwese runotanga nekuzvininipisa pakudzidza nhoroondo yeZvimba nekuchena kwemweya."
            }
        }
    }

    fun getSmartStudyAdvice(language: Language): String {
        val wrongReviews = reviews.filter { !it.isCorrect }
        if (wrongReviews.isEmpty()) {
            return when (language) {
                Language.PORTUGUESE -> "Parabéns! Continue meditando na oração dos 40 dias e na união fraternal de todos os filhos de Jeová."
                Language.ENGLISH -> "Congratulations! Continue meditating on the 40-day prayer and fraternal unity among all God's children."
                Language.SHONA -> "Makorokoto! Ramba uchifungisisa pamunamato wemazuva makumi mana nerudo pakati pevana vaJehovha."
            }
        }
        val mostMissedCategory = wrongReviews.groupBy { it.question.category }.maxByOrNull { it.value.size }?.key
        return when (mostMissedCategory) {
            "history" -> when (language) {
                Language.PORTUGUESE -> "Recomendação: Aprofunde o estudo sobre a descida de Deus em Zvimba em 1954 e o início da obra de cura de Mai Chaza."
                Language.ENGLISH -> "Recommendation: Deepen your study on God's descent in Zvimba in 1954 and the origin of Mai Chaza's healing mission."
                Language.SHONA -> "Kurudziro: Dzidza zvakadzama nezvekuvamba kweGuta kuZvimba muna 1954 nebasa rekuporesa raMai Chaza."
            }
            "teachings" -> when (language) {
                Language.PORTUGUESE -> "Recomendação: Releia os 10 Mandamentos de Deus e o princípio da confissão de pecados (Kureurura) para a purificação da alma."
                Language.ENGLISH -> "Recommendation: Revisit the 10 Commandments and the sacred act of confession (Kureurura) for soul purification."
                Language.SHONA -> "Kurudziro: Dzokorora mirau gumi yaMwari nekureurura zvivi pachena (Kureurura) kuti uwane kuporeswa."
            }
            "sacred_places" -> when (language) {
                Language.PORTUGUESE -> "Recomendação: Conheça mais sobre Dzimbahwe (Santuário Central em Zvimba) e a comunhão dos ramos em Moçambique e no mundo."
                Language.ENGLISH -> "Recommendation: Learn more about Dzimbahwe (Head Shrine in Zvimba) and the fellowship of branches worldwide."
                Language.SHONA -> "Kurudziro: Ziva zvakawanda nezveDzimbahwe guru rekuZvimba nemizinda iri muMozambique nepasirese."
            }
            "practices" -> when (language) {
                Language.PORTUGUESE -> "Recomendação: Observe o significado da veste branca (símbolo de santidade e retidão) e dos hinos sagrados sem instrumentos artificiais."
                Language.ENGLISH -> "Recommendation: Study the sanctity of the white robe (symbol of purity) and sacred hymns sung without artificial instruments."
                Language.SHONA -> "Kurudziro: Chengetedza zvirevo zvenguvo chena tsvene nenziyo dzekurumbidza Jehovha pasina zviridzwa."
            }
            else -> when (language) {
                Language.PORTUGUESE -> "Recomendação: Pratique o amor fraternal e a oração com perseverança e fé."
                Language.ENGLISH -> "Recommendation: Practice fraternal love and steadfast prayer with deep faith."
                Language.SHONA -> "Kurudziro: Ramba uchiratidza rudo nekunamata usinganeti."
            }
        }
    }
}

data class DuelRoundRecap(
    val roundNumber: Int,
    val winnerPlayer: Int?, // 1 for P1, 2 for P2, null for neither
    val p1AnsweredCorrect: Boolean?,
    val p2AnsweredCorrect: Boolean?,
    val p1PointsGained: Int,
    val p2PointsGained: Int
)

data class DailyWisdom(
    val title: Map<String, String>,
    val teaching: Map<String, String>,
    val reference: String
) {
    fun getTitle(language: Language): String = title[language.code] ?: title["pt"] ?: ""
    fun getTeaching(language: Language): String = teaching[language.code] ?: teaching["pt"] ?: ""
}

data class ChurchHymn(
    val id: String,
    val title: Map<String, String>,
    val context: Map<String, String>,
    val lyricsShona: List<String>,
    val lyricsPt: List<String>,
    val lyricsEn: List<String>,
    val soundType: com.example.util.SoundManager.SoundType
) {
    fun getTitle(language: Language): String = title[language.code] ?: title["pt"] ?: ""
    fun getContext(language: Language): String = context[language.code] ?: context["pt"] ?: ""
    fun getLyrics(language: Language): List<String> = when (language) {
        Language.SHONA -> lyricsShona
        Language.ENGLISH -> lyricsEn
        Language.PORTUGUESE -> lyricsPt
    }
}

data class Achievement(
    val id: String,
    val title: Map<String, String>,
    val description: Map<String, String>,
    val iconEmoji: String
) {
    fun getTitle(language: Language): String = title[language.code] ?: title["pt"] ?: ""
    fun getDescription(language: Language): String = description[language.code] ?: description["pt"] ?: ""
}

enum class Screen {
    REGISTRATION,
    HOME,
    SOLO_CONFIG,
    SOLO_QUIZ,
    SOLO_RESULT,
    DUEL_CONFIG,
    DUEL_QUIZ,
    DUEL_RESULT,
    HYMNS_AND_SOUNDS,
    ACHIEVEMENTS
}
