package com.eknm.oleksiikolotylo.pocyr.translate

object PoCyrTranslator : TextTranslator {
    override fun translate(text: String): String {
        val words = text.split(Regex("(?=[,|.| ]+)"))

        return words.joinToString(transform = this::transliterateWord, separator = "")
    }

    private val simpleLetterMapping = mapOf(
        "a" to "а",
        "ą" to "о̀",
        "b" to "б",
        "c" to "ц",
        "ć" to "чь",
        "d" to "д",
        "e" to "е",
        "ę" to "ѐ",
        "f" to "ф",
        "g" to "г",
        "h" to "х",
        "i" to "і",
        "j" to "й",
        "k" to "к",
        "l" to "л",
        "ł" to "ў",
        "m" to "м",
        "n" to "н",
        "ń" to "нь",
        "o" to "о",
        "ó" to "у",
        "p" to "п",
        "q" to "ку",
        "r" to "р",
        "s" to "с",
        "ś" to "шь",
        "t" to "т",
        "u" to "у",
        "v" to "в",
        "w" to "в",
        "x" to "кс",
        "y" to "ы",
        "z" to "з",
        "ż" to "ж",
        "ź" to "жь",
    )

    private val complexL2 = mapOf(
        "sz" to "ш",
        "cz" to "ч",
        "ch" to "х",
        "si" to "ші",
        "ci" to "чі",
        "zi" to "жі",
        "kw" to "кф",
        "tw" to "тф",
        "św" to "шьф",
        "rz" to "ж",
        "ść" to "щь"// шьчь
    )

    private val complexL3 = mapOf(
        "prz" to "пш",
        "trz" to "тш",
        "krz" to "кш",
        "chrz" to "хш",
        "chw" to "хф",
    )

    private val complexL5 = mapOf(
        //before  ł и l - o e
        "ął" to "оł",
        "ęł" to "еł",
        "ąl" to "оl",
        "ęl" to "еl",
        //before b, p - m
        "ąb" to "омб",
        "ąp" to "омп",
        "ęb" to "омб",
        "ęp" to "омп",
        //before ć (ci), dź - нь
        "ąć" to "оньć",
        "ąci" to "оньci",
        "ądź" to "оньdź",
        "ęć" to "еньć",
        "ęci" to "еньci",
        "ędź" to "eньdź",

        //before t, dz, cz - n
        "ęt" to "ент",
        "ędz" to "енdz",
        "ęcz" to "енч",
        "ąt" to "онт",
        "ądz" to "онdz",
        "ącz" to "онч",

        //before g, k - ng
        "ąk" to "оньк",
        "ąg" to "eнг",
        "ęk" to "енк",
        "ęg" to "eнг",
    )

    private val complexL4 = mapOf(
        //before d, с, - n
        "ęd" to "енd",
        "ęc" to "енc",
        "ąc" to "онc",
        "ąd" to "онd",
    )

    private val doubleSoundSoftCyrillicMapping = mapOf(
        "ьа" to "я",
        "ьо" to "ё",
        "ье" to "є",
        "ьу" to "ю",
    )

    private val doubleSoundHardMapping = mapOf(
        "йа" to "я",
        "йо" to "ё",
        "йі" to "ї",
        "йу" to "ю",
        "йе" to "є",
    )

    private val vowels = listOf(
        "a",
        "ą",
        "e",
        "ę",
        "o",
        "ó",
        "i",
        "u",
    )

    private val Char.isVowel: Boolean
        get() = toString() in vowels

    private val Char.isConsonant: Boolean get() = isLetter() && !isVowel


    private fun String.removeIInCombination(): String {
        var charArray = charArrayOf()
        forEachIndexed { index, c ->
            if (
                c == 'i'
                && index > 0
                && index < lastIndex
                && get(index - 1).isConsonant && get(index + 1).isVowel
            ) {
                charArray = when (get(index - 1)) {
                    'c' -> charArray.dropLast(1).let { it + 'ч' + 'ь' }.toCharArray()
                    's' -> charArray.dropLast(1).let { it + 'ш' + 'ь' }.toCharArray()
                    'z' -> charArray.dropLast(1).let { it + 'ж' + 'ь' }.toCharArray()
                    else -> charArray + 'ь'
                }
            } else {
                charArray += c
            }

        }

        return String(charArray)
    }

    private fun String.replaceWithMap(map: Map<String, String>): String {
        var newString = this
        map.forEach { (combination, replacement) ->
            newString = newString.replace(combination, replacement)
        }
        return newString
    }

    private fun String.replaceWithDoubleSound(): String {
        var result = this
        val cyrillicVowels = listOf(
            "а", "е", "э", "ё", "і", "о", "у", "ю", "я", "ї", "ъ", "ь"
        )
        doubleSoundHardMapping.forEach { (combination, replacement) ->
            while (result.indexOf(combination) >= 0) {
                val pos = result.indexOf(combination)

                result = if (pos <= 0
                    || (!result[pos - 1].isLetter()
                            || result[pos - 1].toString() in cyrillicVowels)
                ) {
                    result.replaceFirst(combination, replacement)
                } else {
                    result.replaceFirst(combination, "ъ$replacement")
                }
            }
        }

        return result
    }

    private fun transliterateWord(word: String): String {
        var isAllUpper = true

        var firstUpperPos = -1
        word.forEachIndexed { index, c ->
            isAllUpper = c.isLetter() && c.isUpperCase() && isAllUpper
            if (c.isLetter() && firstUpperPos == -1) {
                firstUpperPos = if (c.isUpperCase()) index else -2
            }
        }

        return word
            .lowercase()
            .removeIInCombination()
            .replaceWithMap(complexL5)
            .replaceWithMap(complexL4)
            .replaceWithMap(complexL3)
            .replaceWithMap(complexL2)
            .replaceWithMap(simpleLetterMapping)
            .replaceWithMap(doubleSoundSoftCyrillicMapping)
            .replaceWithDoubleSound()
            .replaceWithMap(
                mapOf(
                    "шч" to "щ",
                    "шьч" to "щь"
                )
            )
            .let { cyrillicWord ->
                when {
                    isAllUpper -> cyrillicWord.uppercase()
                    firstUpperPos >= 0 ->
                        cyrillicWord.take(firstUpperPos) +
                                cyrillicWord[firstUpperPos].uppercase() +
                                cyrillicWord.takeLast(cyrillicWord.length - firstUpperPos - 1)

                    else -> cyrillicWord
                }
            }
    }
}