package com.eknm.oleksiikolotylo.pocyr.translate

object PoCyrTranslator : TextTranslator {
    override fun translate(text: String): String {
        val words = text.split(Regex("(?=[,|.| ]+)"))

        return words.joinToString(transform = this::transliterateWord, separator = "")
    }

    private val simpleLetterMapping = mapOf(
        "a" to "а",
        "b" to "б",
        "c" to "ц",
        "d" to "д",
        "e" to "е",
        "f" to "ф",
        "k" to "к",
        "m" to "м",
        "n" to "н",
        "o" to "о",
        "p" to "п",
        "r" to "р",
        "s" to "с",
        "t" to "т",
        "u" to "у",
        "z" to "з",
        "y" to "ы",
        "g" to "г",
        "w" to "в",
        "ó" to "у",
        "l" to "л",
        "ł" to "ў",
        "j" to "й",
        "h" to "х",
        "i" to "і",
        "ż" to "ж",
        "ń" to "нь",
        "ą" to "о̀",
        "ę" to "ѐ",
        "ż" to "ж",
        "ś" to "шь",
        "ć" to "чь",
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
        "szcz" to "щ",// шч
        "prz" to "пш",
        "trz" to "тш",
        "krz" to "кш",
        "chrz" to "хш",
        "chw" to "хф",
    )

    private val complexL4 = mapOf(
        "ął" to "оł",
        "ęł" to "еł",

        "ęk" to "еньk",
        "ęć" to "еньć",
        "ędź" to "eньdź",

        "ąk" to "оньk",
        "ąć" to "оньć",
        "ądź" to "оньdź",

        "ęd" to "енd",
        "ęt" to "енt",
        "ęg" to "eнg",
        "ęm" to "eнm",

        "ąd" to "енd",
        "ąt" to "енt",
        "ąg" to "eнg",
        "ąm" to "eнm",
    )

    private val doubleSoundSoftCyrillicMapping = mapOf(
        "ьа" to "я",
        "ьо" to "ё",
        "ье" to "є",
        "ьі" to "ї",
        "ьу" to "ю",

        )

    private val doubleSoundHardMapping = mapOf(
        "йа" to "я",
        "йо" to "ё",
        "йі" to "ї",
        "йу" to "ю",
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
            .replaceWithMap(complexL4)
            .replaceWithMap(complexL3)
            .replaceWithMap(complexL2)
            .replaceWithMap(simpleLetterMapping)
            .replaceWithMap(doubleSoundSoftCyrillicMapping)
            .replaceWithDoubleSound()
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