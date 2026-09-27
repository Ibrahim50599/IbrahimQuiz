package com.example.model

enum class Language(val code: String, val displayName: String, val shortCode: String, val flag: String) {
    PORTUGUESE("pt", "Português", "PT", "🇵🇹"),
    ENGLISH("en", "English", "EN", "🇬🇧"),
    SHONA("sn", "ChiShona", "SN", "🇿🇼");

    companion object {
        fun fromCode(code: String): Language {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: PORTUGUESE
        }
    }
}

/**
 * UI Localization repository for strings across Portuguese, English, and Shona.
 */
object Strings {
    fun appTitle(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Guta raJehovah Quiz"
        Language.ENGLISH -> "Guta raJehovah Quiz"
        Language.SHONA -> "Guta raJehovah Mibvunzo"
    }

    fun appSubtitle(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "História, Ensinamentos e Tradições Sagradas"
        Language.ENGLISH -> "History, Teachings & Sacred Traditions"
        Language.SHONA -> "Nhoroondo, Dzidziso neMaitiro Matsvene"
    }

    fun selectLanguage(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Selecionar Idioma"
        Language.ENGLISH -> "Select Language"
        Language.SHONA -> "Sarudza Mutauro"
    }

    fun soloMode(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Modo Solo"
        Language.ENGLISH -> "Solo Mode"
        Language.SHONA -> "Kutamba Uri Woga"
    }

    fun soloModeDesc(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Teste seus conhecimentos com temporizador e pontuação por sequência."
        Language.ENGLISH -> "Test your knowledge with countdown timer and streak scoring."
        Language.SHONA -> "Edza ruzivo rwako nenguva inoverenga uye zvibodzwa."
    }

    fun duelMode(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Modo 2 Jogadores"
        Language.ENGLISH -> "2-Player Duel"
        Language.SHONA -> "Vatambi Vaviri (Duelo)"
    }

    fun duelModeDesc(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Duelo frente a frente no mesmo ecrã em tempo real!"
        Language.ENGLISH -> "Head-to-head battle on the same screen in real-time!"
        Language.SHONA -> "Kukwikwidzana pachiratidziro chimwe chete panguva imwe!"
    }

    fun aboutGrj(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Sobre a Guta raJehovah"
        Language.ENGLISH -> "About Guta raJehovah"
        Language.SHONA -> "Nezve Guta raJehovah"
    }

    fun statsTitle(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Suas Estatísticas"
        Language.ENGLISH -> "Your Statistics"
        Language.SHONA -> "Zvakaitwa Zvako"
    }

    fun bestScore(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Melhor Pontuação"
        Language.ENGLISH -> "Best Score"
        Language.SHONA -> "Chibodzwa Chepamusoro"
    }

    fun quizzesCompleted(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Jogos Concluídos"
        Language.ENGLISH -> "Quizzes Completed"
        Language.SHONA -> "Mitambo Yakapera"
    }

    fun duelWins(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Vitórias Duelo"
        Language.ENGLISH -> "Duel Wins"
        Language.SHONA -> "Kukunda muDuelo"
    }

    fun startGame(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Iniciar Jogo"
        Language.ENGLISH -> "Start Game"
        Language.SHONA -> "Tanga Mutambo"
    }

    fun selectCategory(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Escolha a Categoria"
        Language.ENGLISH -> "Choose Category"
        Language.SHONA -> "Sarudza Chikamu"
    }

    fun questionCount(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Número de Perguntas"
        Language.ENGLISH -> "Number of Questions"
        Language.SHONA -> "Huwandu hweMibvunzo"
    }

    fun timerSetting(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Tempo por Pergunta"
        Language.ENGLISH -> "Time per Question"
        Language.SHONA -> "Nguva yeMubvunzo"
    }

    fun secondsUnit(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "segundos"
        Language.ENGLISH -> "seconds"
        Language.SHONA -> "masekonzi"
    }

    fun questionProgress(current: Int, total: Int, lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Pergunta $current de $total"
        Language.ENGLISH -> "Question $current of $total"
        Language.SHONA -> "Mubvunzo $current pa$total"
    }

    fun scoreLabel(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Pontos"
        Language.ENGLISH -> "Score"
        Language.SHONA -> "Zvibodzwa"
    }

    fun streakLabel(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Sequência"
        Language.ENGLISH -> "Streak"
        Language.SHONA -> "Kutevedzana"
    }

    fun nextQuestion(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Próxima Pergunta"
        Language.ENGLISH -> "Next Question"
        Language.SHONA -> "Mubvunzo Unotevera"
    }

    fun seeResults(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Ver Resultados"
        Language.ENGLISH -> "See Results"
        Language.SHONA -> "Ona Zvakabuda"
    }

    fun correctExclamation(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Correto! Muito bem!"
        Language.ENGLISH -> "Correct! Well done!"
        Language.SHONA -> "Zvakanaka kwazvo! Wazvigona!"
    }

    fun incorrectExclamation(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Incorreto!"
        Language.ENGLISH -> "Incorrect!"
        Language.SHONA -> "Hazvina kururama!"
    }

    fun timeOutExclamation(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Tempo esgotado!"
        Language.ENGLISH -> "Time's up!"
        Language.SHONA -> "Nguva yapera!"
    }

    fun explanationLabel(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Ensinamento / Explicação:"
        Language.ENGLISH -> "Teaching / Explanation:"
        Language.SHONA -> "Dzidziso / Tsanangudzo:"
    }

    fun quizComplete(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Quiz Concluído!"
        Language.ENGLISH -> "Quiz Complete!"
        Language.SHONA -> "Mutambo Wapera!"
    }

    fun playAgain(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Jogar Novamente"
        Language.ENGLISH -> "Play Again"
        Language.SHONA -> "Tamba Zvakare"
    }

    fun mainMenu(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Menu Principal"
        Language.ENGLISH -> "Main Menu"
        Language.SHONA -> "Menyu Huru"
    }

    fun reviewAnswers(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Revisão das Perguntas"
        Language.ENGLISH -> "Review Questions"
        Language.SHONA -> "Ongorora Mibvunzo"
    }

    fun accuracy(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Precisão"
        Language.ENGLISH -> "Accuracy"
        Language.SHONA -> "Kururama"
    }

    // Duel Strings
    fun player1(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Jogador 1"
        Language.ENGLISH -> "Player 1"
        Language.SHONA -> "Mutambi 1"
    }

    fun player2(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Jogador 2"
        Language.ENGLISH -> "Player 2"
        Language.SHONA -> "Mutambi 2"
    }

    fun duelRound(current: Int, total: Int, lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Ronda $current de $total"
        Language.ENGLISH -> "Round $current of $total"
        Language.SHONA -> "Danho $current pa$total"
    }

    fun duelWinner(winnerName: String, lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Vitória de $winnerName!"
        Language.ENGLISH -> "$winnerName Wins!"
        Language.SHONA -> "$winnerName Akunda!"
    }

    fun duelDraw(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Empate Espetacular!"
        Language.ENGLISH -> "Thrilling Draw!"
        Language.SHONA -> "Mabuda Magadzana!"
    }

    fun duelStartInstruction(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Coloque o telefone no meio. Quem responder corretamente primeiro marca pontos!"
        Language.ENGLISH -> "Place the phone in between. First to answer correctly scores points!"
        Language.SHONA -> "Isai foni pakati penyu. Anotanga kupindura zvakanaka anowana zvibodzwa!"
    }

    fun tapToAnswer(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Toque para responder"
        Language.ENGLISH -> "Tap to answer"
        Language.SHONA -> "Bata kuti upindure"
    }

    fun orientationInverted(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Frente a Frente (Inverter P1)"
        Language.ENGLISH -> "Face-to-Face (Invert P1)"
        Language.SHONA -> "Kutarisana (Shandura P1)"
    }

    fun orientationSame(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Lado a Lado (Mesmo Sentido)"
        Language.ENGLISH -> "Side-by-Side (Same View)"
        Language.SHONA -> "Padivi nePadivi"
    }

    fun roundPointsAwarded(winner: String, points: Int, lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "+$points pts para $winner!"
        Language.ENGLISH -> "+$points pts for $winner!"
        Language.SHONA -> "+$points zvibodzwa kuna $winner!"
    }

    fun bothWrong(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Nenhum jogador pontuou nesta ronda."
        Language.ENGLISH -> "No player scored this round."
        Language.SHONA -> "Hapana akawana chibodzwa mudanho rino."
    }

    fun lifeline5050(lang: Language): String = "50:50"
    fun lifelineTime(lang: Language): String = "+10s"

    // Registration strings
    fun registrationTitle(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Cadastro do Participante"
        Language.ENGLISH -> "Participant Registration"
        Language.SHONA -> "Kunyoresa Mutambi"
    }

    fun registrationSubtitle(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Identifique-se para começar a jogar o Quiz da Guta raJehovah"
        Language.ENGLISH -> "Enter your details to start playing the Guta raJehovah Quiz"
        Language.SHONA -> "Zivikanwe kuti utange kutamba Mibvunzo yeGuta raJehovah"
    }

    fun nameLabel(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Seu Nome Completo ou Apelido"
        Language.ENGLISH -> "Your Full Name or Nickname"
        Language.SHONA -> "Zita Rako Kana reMadunhurirwa"
    }

    fun namePlaceholder(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Ex: Irmão Tendai / Maria"
        Language.ENGLISH -> "E.g.: Brother Tendai / Mary"
        Language.SHONA -> "Semuenzaniso: Hama Tendai / Maria"
    }

    fun locationLabel(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Onde Você Vive? (Cidade / País / Muzinda)"
        Language.ENGLISH -> "Where Do You Live? (City / Country / Muzinda)"
        Language.SHONA -> "Unogara Kupi? (Guta / Nyika / Muzinda)"
    }

    fun locationPlaceholder(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Ex: Maputo, Moçambique / Harare / Zvimba"
        Language.ENGLISH -> "E.g.: Maputo, Mozambique / Harare / Zvimba"
        Language.SHONA -> "Semuenzaniso: Maputo, Mozambique / Harare / Zvimba"
    }

    fun registerButton(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Cadastrar e Começar a Jogar"
        Language.ENGLISH -> "Register & Start Playing"
        Language.SHONA -> "Nyoresa Uye Tanga Kutamba"
    }

    fun updateProfileButton(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Salvar Alterações"
        Language.ENGLISH -> "Save Changes"
        Language.SHONA -> "Chengetedza Zvachinjwa"
    }

    fun editProfile(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Editar Perfil"
        Language.ENGLISH -> "Edit Profile"
        Language.SHONA -> "Chinja Zvinyorwa"
    }

    fun playerProfileTitle(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Perfil do Jogador"
        Language.ENGLISH -> "Player Profile"
        Language.SHONA -> "Zvinyorwa zveMutambi"
    }

    fun welcomeGreeting(name: String, lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Bem-vindo(a), $name!"
        Language.ENGLISH -> "Welcome, $name!"
        Language.SHONA -> "Mauya, $name!"
    }

    fun locationHint(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Sugestões de locais frequentes:"
        Language.ENGLISH -> "Suggested common locations:"
        Language.SHONA -> "Nzvimbo dzinowanzozivikanwa:"
    }

    fun nameRequiredError(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Por favor, digite seu nome para continuar."
        Language.ENGLISH -> "Please enter your name to continue."
        Language.SHONA -> "Ndapota nyora zita rako kuti uenderere mberi."
    }

    fun locationRequiredError(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Por favor, informe onde você vive."
        Language.ENGLISH -> "Please indicate where you live."
        Language.SHONA -> "Ndapota taura kwaunogara."
    }

    fun aboutGrjText(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "A Guta raJehovah (Cidade de Jeová Deus) é uma instituição religiosa sagrada fundada em 1954 no Zimbábue por Mai Chaza (Matenga). É reconhecida pela profunda fé no Deus Altíssimo (Jehovah), pela oração fervorosa, confissão de pecados (Kureurura), cura divina sem recurso a práticas tradicionais ou amuletos, e estrita observância moral e harmonia fraternal. A sua sede principal está em Zvimba, com comunidades em Moçambique, África do Sul e no mundo inteiro."
        Language.ENGLISH -> "Guta raJehovah (City of Jehovah God) is a holy religious ministry founded in 1954 in Zimbabwe by Mai Chaza (Matenga). It is distinguished by absolute faith in God the Almighty (Jehovah), deep prayer, open confession of sins (Kureurura), divine healing without traditional medicines or charms, and strict adherence to moral uprightness and brotherhood. Its main headquarters is in Zvimba, with active congregations across Mozambique, South Africa, and globally."
        Language.SHONA -> "Guta raJehovah inzvimbo tsvene yeushumiri yakavambwa muna 1954 muZimbabwe naMai Chaza (vanoremekedzwa saMatenga). Rinonyanya kuzivikanwa nekutenda kwakasimba muna Mwari Jehovha, minamato yakadzika, kureurura zvivi pachena, kuporeswa kwemweya nemuviri pasina mishonga yen'anga kana mazango, nekuchengeta mirau yaMwari. Dzimbahwe guru riri kuZvimba, uye rine mizinda yakawanda muZimbabwe, Mozambique, South Africa nepasirese."
    }

    // Sound Strings
    fun soundOn(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Som Ativado"
        Language.ENGLISH -> "Sound On"
        Language.SHONA -> "Mutinhimira Wakabatidzwa"
    }

    fun soundOff(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Som Desativado"
        Language.ENGLISH -> "Sound Off"
        Language.SHONA -> "Mutinhimira Wadzimwa"
    }

    fun soundToggleDesc(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Ativar ou desativar efeitos sonoros do jogo"
        Language.ENGLISH -> "Toggle game sound effects"
        Language.SHONA -> "Batidza kana kudzima mutinhimira wemutambo"
    }

    // Smart Features Strings
    fun smartHintButton(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "💡 Dica Inteligente"
        Language.ENGLISH -> "💡 Smart Clue"
        Language.SHONA -> "💡 Zano reHungwaru"
    }

    fun smartHintTitle(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Dica de Sabedoria Espiritual"
        Language.ENGLISH -> "Spiritual Wisdom Clue"
        Language.SHONA -> "Zano reRuzivo rweKutenda"
    }

    fun smartStreakBanner(streak: Int, lang: Language): String = when (lang) {
        Language.PORTUGUESE -> when {
            streak >= 5 -> "⭐ Mestre do Conhecimento! (Bônus x2.0)"
            streak >= 3 -> "🔥 Em Chamas! Sabedoria Brilhante (Bônus x1.6)"
            else -> "⚡ Grande Foco! Sequência Ativa (Bônus x1.2)"
        }
        Language.ENGLISH -> when {
            streak >= 5 -> "⭐ Master of Wisdom! (x2.0 Bonus)"
            streak >= 3 -> "🔥 On Fire! Brilliant Insight (x1.6 Bonus)"
            else -> "⚡ Great Focus! Active Streak (x1.2 Bonus)"
        }
        Language.SHONA -> when {
            streak >= 5 -> "⭐ Nyanzvi yeRuzivo! (x2.0 Zvibodzwa)"
            streak >= 3 -> "🔥 Uri Kupisa! Ruzivo Rwakajeka (x1.6 Zvibodzwa)"
            else -> "⚡ Ramba Wakadaro! Kutevedzana Kwakanaka"
        }
    }

    fun dailyWisdomTitle(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Sabedoria Diária da Guta raJehovah"
        Language.ENGLISH -> "Daily Wisdom of Guta raJehovah"
        Language.SHONA -> "Chidzidzo cheZuva cheGuta raJehovah"
    }

    fun smartDiagnosisTitle(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Diagnóstico Inteligente de Sabedoria"
        Language.ENGLISH -> "Intelligent Wisdom Diagnosis"
        Language.SHONA -> "Kuongororwa kweHungwaru neRuzivo"
    }

    fun categoryBreakdownTitle(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Desempenho por Área de Conhecimento"
        Language.ENGLISH -> "Performance by Knowledge Area"
        Language.SHONA -> "Zvakabuda Muzvikamu Zveruzivo"
    }

    fun studyRecommendationTitle(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Orientação e Recomendação de Estudo"
        Language.ENGLISH -> "Guidance & Study Recommendation"
        Language.SHONA -> "Mazano neKurudziro Yekudzidza"
    }

    fun duelDynamicStatus(p1Leading: Boolean, p2Leading: Boolean, tied: Boolean, lang: Language): String = when (lang) {
        Language.PORTUGUESE -> when {
            tied -> "⚡ Disputa Equilibrada! Quem acertar mais rápido lidera!"
            p1Leading -> "🔥 Jogador 1 lidera a pontuação!"
            p2Leading -> "🔥 Jogador 2 lidera a pontuação!"
            else -> "⚡ Duelo em andamento!"
        }
        Language.ENGLISH -> when {
            tied -> "⚡ Evenly Matched! Speed and precision will decide!"
            p1Leading -> "🔥 Player 1 is leading the score!"
            p2Leading -> "🔥 Player 2 is leading the score!"
            else -> "⚡ Duel underway!"
        }
        Language.SHONA -> when {
            tied -> "⚡ Makabatana zvibodzwa! Kurumidza kupindura!"
            p1Leading -> "🔥 Mutambi 1 ari pamberi!"
            p2Leading -> "🔥 Mutambi 2 ari pamberi!"
            else -> "⚡ Mutambo uri kuenderera mberi!"
        }
    }

    // Church Sounds & Hymns
    fun hymnsAndSounds(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Sons e Hinos Sagrados"
        Language.ENGLISH -> "Sacred Sounds & Hymns"
        Language.SHONA -> "Mutinhimira neNziyo dzeGuta"
    }

    fun hymnsAndSoundsDesc(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Escute o sino do santuário, o coro a cappella, as palmas de reverência (Kuuchira) e leia os hinos sagrados."
        Language.ENGLISH -> "Listen to the sanctuary bell, a cappella choir, holy clapping (Kuuchira) and read sacred hymns."
        Language.SHONA -> "Teerera bhero rekunamata, kwaya yeGuta, kuuchira kwenyasha nekuverenga nziyo tsvene."
    }

    fun sacredSoundsTitle(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Sons Sagrados do Santuário"
        Language.ENGLISH -> "Sacred Sanctuary Sounds"
        Language.SHONA -> "Mutinhimira Dzvene weDzimbahwe"
    }

    fun playSoundPrompt(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Toque para reproduzir o som"
        Language.ENGLISH -> "Tap to play sound"
        Language.SHONA -> "Bata kuti uridze mutinhimira"
    }

    fun achievementsTitle(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Quadro de Conquistas da Fé"
        Language.ENGLISH -> "Faith Achievements Hall"
        Language.SHONA -> "Zvibodzwa zveKutenda"
    }

    fun churchBellName(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Sino de Oração (Bhero reKunamata)"
        Language.ENGLISH -> "Sanctuary Bell (Bhero reKunamata)"
        Language.SHONA -> "Bhero reKunamata muDzimbahwe"
    }

    fun churchChoirName(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Coro A Cappella (Nziyo dzeGuta)"
        Language.ENGLISH -> "A Cappella Choir (Nziyo dzeGuta)"
        Language.SHONA -> "Kwaya yeGuta (Nziyo Tsvene)"
    }

    fun churchClappingName(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Palmas de Reverência (Kuuchira)"
        Language.ENGLISH -> "Reverent Clapping (Kuuchira)"
        Language.SHONA -> "Kuuchira kwaMwari (Kupururudza)"
    }

    fun churchBlessingName(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Bênção de Paz (Ngaizviitwe)"
        Language.ENGLISH -> "Blessing of Peace (Ngaizviitwe)"
        Language.SHONA -> "Chikomborero cheRugare (Ngaizviitwe)"
    }

    fun soundThemeChurch(lang: Language): String = when (lang) {
        Language.PORTUGUESE -> "Tema de Sons: Igreja GRJ"
        Language.ENGLISH -> "Sound Theme: GRJ Church"
        Language.SHONA -> "Mutinhimira: Chechi yeGuta"
    }
}
