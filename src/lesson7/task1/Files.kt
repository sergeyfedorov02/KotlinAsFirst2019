@file:Suppress("UNUSED_PARAMETER", "ConvertCallChainIntoSequence")

package lesson7.task1

import ru.spbstu.wheels.toMap
import java.io.File

/**
 * Пример
 *
 * Во входном файле с именем inputName содержится некоторый текст.
 * Вывести его в выходной файл с именем outputName, выровняв по левому краю,
 * чтобы длина каждой строки не превосходила lineLength.
 * Слова в слишком длинных строках следует переносить на следующую строку.
 * Слишком короткие строки следует дополнять словами из следующей строки.
 * Пустые строки во входном файле обозначают конец абзаца,
 * их следует сохранить и в выходном файле
 */
fun alignFile(inputName: String, lineLength: Int, outputName: String) {
    val outputStream = File(outputName).bufferedWriter()
    var currentLineLength = 0
    for (line in File(inputName).readLines()) {
        if (line.isEmpty()) {
            outputStream.newLine()
            if (currentLineLength > 0) {
                outputStream.newLine()
                currentLineLength = 0
            }
            continue
        }
        for (word in line.split(" ")) {
            if (currentLineLength > 0) {
                if (word.length + currentLineLength >= lineLength) {
                    outputStream.newLine()
                    currentLineLength = 0
                } else {
                    outputStream.write(" ")
                    currentLineLength++
                }
            }
            outputStream.write(word)
            currentLineLength += word.length
        }
    }
    outputStream.close()
}

/**
 * Средняя
 *
 * Во входном файле с именем inputName содержится некоторый текст.
 * На вход подаётся список строк substrings.
 * Вернуть ассоциативный массив с числом вхождений каждой из строк в текст.
 * Регистр букв игнорировать, то есть буквы е и Е считать одинаковыми.
 *
 */
fun countSubstrings(inputName: String, substrings: List<String>): Map<String, Int> {

    fun countSubstring(s: String, subString: String): Int {
        return s.windowed(subString.length) {
            if (it == subString) 1 else 0
        }.sum()
    }

    val result = substrings.map { it to 0 }.toMap().toMutableMap()
    val lowerCase = substrings.map { it to it.toLowerCase() }.toMap()

    val file = File(inputName)
    for (line in file.readLines().filter { it.isNotEmpty() }) {

        for (word in line.split(" ").filter { it.isNotEmpty() }) lowerCase.forEach {
            result.computeIfPresent(it.key) { _, v -> v + countSubstring(word.toLowerCase(), it.value) }
        }

    }

    return result
}


/**
 * Средняя
 *
 * В русском языке, как правило, после букв Ж, Ч, Ш, Щ пишется И, А, У, а не Ы, Я, Ю.
 * Во входном файле с именем inputName содержится некоторый текст на русском языке.
 * Проверить текст во входном файле на соблюдение данного правила и вывести в выходной
 * файл outputName текст с исправленными ошибками.
 *
 * Регистр заменённых букв следует сохранять.
 *
 * Исключения (жюри, брошюра, парашют) в рамках данного задания обрабатывать не нужно
 *
 */

val mapReplace = mapOf(
    "Ы" to "И",
    "ы" to "и",
    "Я" to "А",
    "я" to "а",
    "Ю" to "У",
    "ю" to "у"
)

fun sibilants(inputName: String, outputName: String) {

    val expr = """(?<=[жЖшШчЧщЩ])[ыЫяЯюЮ]""".toRegex()
    val x = File(inputName)
    val outputStream = File(outputName).bufferedWriter()

    val s = x.readText().replace(expr) { m -> mapReplace.getValue(m.value) }

    outputStream.write(s)
    outputStream.close()
}

/**
 * Средняя
 *
 * Во входном файле с именем inputName содержится некоторый текст (в том числе, и на русском языке).
 * Вывести его в выходной файл с именем outputName, выровняв по центру
 * относительно самой длинной строки.
 *
 * Выравнивание следует производить путём добавления пробелов в начало строки.
 *
 *
 * Следующие правила должны быть выполнены:
 * 1) Пробелы в начале и в конце всех строк не следует сохранять.
 * 2) В случае невозможности выравнивания строго по центру, строка должна быть сдвинута в ЛЕВУЮ сторону
 * 3) Пустые строки не являются особым случаем, их тоже следует выравнивать
 * 4) Число строк в выходном файле должно быть равно числу строк во входном (в т. ч. пустых)
 *
 */
fun centerFile(inputName: String, outputName: String) {
    var length = 0
    val outputStream = File(outputName).bufferedWriter()

    for (line in File(inputName).readLines()) {
        val x = line.trim()
        if (x.length > length)
            length = x.length
    }
    for (line in File(inputName).readLines()) {
        val y = line.trim()
        val yModTwo = y.length % 2
        val lenModTwo = length % 2
        var size = length / 2 - y.length / 2
        val result = when {
            (yModTwo == 0 && lenModTwo == 0 || yModTwo != 0 && lenModTwo != 0) ->
                " ".repeat(size) + y
            else -> {
                size = length / 2 - (y.length + 1) / 2
                " ".repeat(size) + y
            }
        }
        outputStream.write(result)
        outputStream.newLine()
    }
    outputStream.close()
}

/**
 * Сложная
 *
 * Во входном файле с именем inputName содержится некоторый текст (в том числе, и на русском языке).
 * Вывести его в выходной файл с именем outputName, выровняв по левому и правому краю относительно
 * самой длинной строки.
 * Выравнивание производить, вставляя дополнительные пробелы между словами: равномерно по всей строке
 *
 * Слова внутри строки отделяются друг от друга одним или более пробелом.
 *
 * Следующие правила должны быть выполнены:
 * 1) Каждая строка входного и выходного файла не должна начинаться или заканчиваться пробелом.
 * 2) Пустые строки или строки из пробелов трансформируются в пустые строки без пробелов.
 * 3) Строки из одного слова выводятся без пробелов.
 * 4) Число строк в выходном файле должно быть равно числу строк во входном (в т. ч. пустых).
 *
 * Равномерность определяется следующими формальными правилами:
 * 5) Число пробелов между каждыми двумя парами соседних слов не должно отличаться более, чем на 1.
 * 6) Число пробелов между более левой парой соседних слов должно быть больше или равно числу пробелов
 *    между более правой парой соседних слов.
 *
 * Следует учесть, что входной файл может содержать последовательности из нескольких пробелов  между слвоами. Такие
 * последовательности следует учитывать при выравнивании и при необходимости избавляться от лишних пробелов.
 * Из этого следуют следующие правила:
 * 7) В самой длинной строке каждая пара соседних слов должна быть отделена В ТОЧНОСТИ одним пробелом
 * 8) Если входной файл удовлетворяет требованиям 1-7, то он должен быть в точности идентичен выходному файлу
 */
fun alignFileByWidth(inputName: String, outputName: String) {
    TODO()
}

/**
 * Средняя
 *
 * Во входном файле с именем inputName содержится некоторый текст (в том числе, и на русском языке).
 *
 * Вернуть ассоциативный массив, содержащий 20 наиболее часто встречающихся слов с их количеством.
 * Если в тексте менее 20 различных слов, вернуть все слова.
 *
 * Словом считается непрерывная последовательность из букв (кириллических,
 * либо латинских, без знаков препинания и цифр).
 * Цифры, пробелы, знаки препинания считаются разделителями слов:
 * Привет, привет42, привет!!! -привет?!
 * ^ В этой строчке слово привет встречается 4 раза.
 *
 * Регистр букв игнорировать, то есть буквы е и Е считать одинаковыми.
 * Ключи в ассоциативном массиве должны быть в нижнем регистре.
 *
 */
fun top20Words(inputName: String): Map<String, Int> {
    val result: MutableMap<String, Int> = mutableMapOf()
    val reg = """[A-zА-яёЁ]+""".toRegex()

    for (line in File(inputName).readLines()) {
        reg.findAll(line).forEach {
            val lc = it.value.toLowerCase()
            var x = result[lc] ?: 0
            x += 1
            result[lc] = x
        }
    }
    val y = result.entries.sortedByDescending { (_, v) -> v }
    if (result.size < 20)
        return y.toMap()
    return y.take(20).toMap()
}

/**
 * Средняя
 *
 * Реализовать транслитерацию текста из входного файла в выходной файл посредством динамически задаваемых правил.

 * Во входном файле с именем inputName содержится некоторый текст (в том числе, и на русском языке).
 *
 * В ассоциативном массиве dictionary содержится словарь, в котором некоторым символам
 * ставится в соответствие строчка из символов, например
 * mapOf('з' to "zz", 'р' to "r", 'д' to "d", 'й' to "y", 'М' to "m", 'и' to "yy", '!' to "!!!")
 *
 * Необходимо вывести в итоговый файл с именем outputName
 * содержимое текста с заменой всех символов из словаря на соответствующие им строки.
 *
 * При этом регистр символов в словаре должен игнорироваться,
 * но при выводе символ в верхнем регистре отображается в строку, начинающуюся с символа в верхнем регистре.
 *
 * Пример.
 * Входной текст: Здравствуй, мир!
 *
 * заменяется на
 *
 * Выходной текст: Zzdrавствуy, mир!!!
 *
 * Пример 2.
 *
 * Входной текст: Здравствуй, мир!
 * Словарь: mapOf('з' to "zZ", 'р' to "r", 'д' to "d", 'й' to "y", 'М' to "m", 'и' to "YY", '!' to "!!!")
 *
 * заменяется на
 *
 * Выходной текст: Zzdrавствуy, mир!!!
 *
 * Обратите внимание: данная функция не имеет возвращаемого значения
 */
fun transliterate(inputName: String, dictionary: Map<Char, String>, outputName: String) {
    val x = dictionary.mapKeys { (k, _) -> k.toLowerCase() }
    val outputStream = File(outputName).bufferedWriter()

    fun additionalActions(s: Char): String? {
        val hasUppercase = s.isUpperCase()
        if (hasUppercase) {
            val z = x.getValue(s.toLowerCase())
            if (z.isNotEmpty())
                return z[0].toUpperCase().toString() + z.removeRange(0, 1).toLowerCase()
            return z
        }
        return x[s]?.toLowerCase()
    }

    val result = File(inputName).readText().map { if (it.toLowerCase() in x) additionalActions(it) else it }
        .joinToString(separator = "")

    outputStream.write(result)
    outputStream.close()
}

/**
 * Средняя
 *
 * Во входном файле с именем inputName имеется словарь с одним словом в каждой строчке.
 * Выбрать из данного словаря наиболее длинное слово,
 * в котором все буквы разные, например: Неряшливость, Четырёхдюймовка.
 * Вывести его в выходной файл с именем outputName.
 * Если во входном файле имеется несколько слов с одинаковой длиной, в которых все буквы разные,
 * в выходной файл следует вывести их все через запятую.
 * Регистр букв игнорировать, то есть буквы е и Е считать одинаковыми.
 *
 * Пример входного файла:
 * Карминовый
 * Боязливый
 * Некрасивый
 * Остроумный
 * БелогЛазый
 * ФиолетОвый

 * Соответствующий выходной файл:
 * Карминовый, Некрасивый
 *
 * Обратите внимание: данная функция не имеет возвращаемого значения
 */
fun chooseLongestChaoticWord(inputName: String, outputName: String) {
    var length = 0
    var result = ""
    val outputStream = File(outputName).bufferedWriter()

    for (word in File(inputName).readLines()) {
        val arrayOfLetters = word.groupBy { it.toLowerCase() }.filter { it.value.size > 1 }
        if (arrayOfLetters.isEmpty())
            when {
                word.length > length -> {
                    result = word
                    length = word.length
                }
                word.length == length -> result += ", $word"

            }
    }
    outputStream.write(result)
    outputStream.close()
}

/**
 * Сложная
 *
 * Реализовать транслитерацию текста в заданном формате разметки в формат разметки HTML.
 *
 * Во входном файле с именем inputName содержится текст, содержащий в себе элементы текстовой разметки следующих типов:
 * - *текст в курсивном начертании* -- курсив
 * - **текст в полужирном начертании** -- полужирный
 * - ~~зачёркнутый текст~~ -- зачёркивание
 *
 * Следует вывести в выходной файл этот же текст в формате HTML:
 * - <i>текст в курсивном начертании</i>
 * - <b>текст в полужирном начертании</b>
 * - <s>зачёркнутый текст</s>
 *
 * Кроме того, все абзацы исходного текста, отделённые друг от друга пустыми строками, следует обернуть в теги <p>...</p>,
 * а весь текст целиком в теги <html><body>...</body></html>.
 *
 * Все остальные части исходного текста должны остаться неизменными с точностью до наборов пробелов и переносов строк.
 * Отдельно следует заметить, что открывающая последовательность из трёх звёздочек (***) должна трактоваться как "<b><i>"
 * и никак иначе.
 *
 * При решении этой и двух следующих задач полезно прочитать статью Википедии "Стек".
 *
 * Пример входного файла:
Lorem ipsum *dolor sit amet*, consectetur **adipiscing** elit.
Vestibulum lobortis, ~~Est vehicula rutrum *suscipit*~~, ipsum ~~lib~~ero *placerat **tortor***,

Suspendisse ~~et elit in enim tempus iaculis~~.
 *
 * Соответствующий выходной файл:
<html>
    <body>
        <p>
            Lorem ipsum <i>dolor sit amet</i>, consectetur <b>adipiscing</b> elit.
            Vestibulum lobortis. <s>Est vehicula rutrum <i>suscipit</i></s>, ipsum <s>lib</s>ero <i>placerat <b>tortor</b></i>.
        </p>
        <p>
            Suspendisse <s>et elit in enim tempus iaculis</s>.
        </p>
    </body>
</html>
 *
 * (Отступы и переносы строк в примере добавлены для наглядности, при решении задачи их реализовывать не обязательно)
 */
fun markdownToHtmlSimple(inputName: String, outputName: String) {
    TODO()
}

/**
 * Сложная
 *
 * Реализовать транслитерацию текста в заданном формате разметки в формат разметки HTML.
 *
 * Во входном файле с именем inputName содержится текст, содержащий в себе набор вложенных друг в друга списков.
 * Списки бывают двух типов: нумерованные и ненумерованные.
 *
 * Каждый элемент ненумерованного списка начинается с новой строки и символа '*', каждый элемент нумерованного списка --
 * с новой строки, числа и точки. Каждый элемент вложенного списка начинается с отступа из пробелов, на 4 пробела большего,
 * чем список-родитель. Максимально глубина вложенности списков может достигать 6. "Верхние" списки файла начинются
 * прямо с начала строки.
 *
 * Следует вывести этот же текст в выходной файл в формате HTML:
 * Нумерованный список:
 * <ol>
 *     <li>Раз</li>
 *     <li>Два</li>
 *     <li>Три</li>
 * </ol>
 *
 * Ненумерованный список:
 * <ul>
 *     <li>Раз</li>
 *     <li>Два</li>
 *     <li>Три</li>
 * </ul>
 *
 * Кроме того, весь текст целиком следует обернуть в теги <html><body>...</body></html>
 *
 * Все остальные части исходного текста должны остаться неизменными с точностью до наборов пробелов и переносов строк.
 *
 * Пример входного файла:
///////////////////////////////начало файла/////////////////////////////////////////////////////////////////////////////
* Утка по-пекински
    * Утка
    * Соус
* Салат Оливье
    1. Мясо
        * Или колбаса
    2. Майонез
    3. Картофель
    4. Что-то там ещё
* Помидоры
* Фрукты
    1. Бананы
    23. Яблоки
        1. Красные
        2. Зелёные
///////////////////////////////конец файла//////////////////////////////////////////////////////////////////////////////
 *
 *
 * Соответствующий выходной файл:
///////////////////////////////начало файла/////////////////////////////////////////////////////////////////////////////
<html>
  <body>
    <ul>
      <li>
        Утка по-пекински
        <ul>
          <li>Утка</li>
          <li>Соус</li>
        </ul>
      </li>
      <li>
        Салат Оливье
        <ol>
          <li>Мясо
            <ul>
              <li>
                  Или колбаса
              </li>
            </ul>
          </li>
          <li>Майонез</li>
          <li>Картофель</li>
          <li>Что-то там ещё</li>
        </ol>
      </li>
      <li>Помидоры</li>
      <li>
        Фрукты
        <ol>
          <li>Бананы</li>
          <li>
            Яблоки
            <ol>
              <li>Красные</li>
              <li>Зелёные</li>
            </ol>
          </li>
        </ol>
      </li>
    </ul>
  </body>
</html>
///////////////////////////////конец файла//////////////////////////////////////////////////////////////////////////////
 * (Отступы и переносы строк в примере добавлены для наглядности, при решении задачи их реализовывать не обязательно)
 */
fun markdownToHtmlLists(inputName: String, outputName: String) {
    TODO()
}

/**
 * Очень сложная
 *
 * Реализовать преобразования из двух предыдущих задач одновременно над одним и тем же файлом.
 * Следует помнить, что:
 * - Списки, отделённые друг от друга пустой строкой, являются разными и должны оказаться в разных параграфах выходного файла.
 *
 */
fun markdownToHtml(inputName: String, outputName: String) {
    TODO()
}

/**
 * Средняя
 *
 * Вывести в выходной файл процесс умножения столбиком числа lhv (> 0) на число rhv (> 0).
 *
 * Пример (для lhv == 19935, rhv == 111):
   19935
*    111
--------
   19935
+ 19935
+19935
--------
 2212785
 * Используемые пробелы, отступы и дефисы должны в точности соответствовать примеру.
 * Нули в множителе обрабатывать так же, как и остальные цифры:
  235
*  10
-----
    0
+235
-----
 2350
 *
 */
fun printMultiplicationProcess(lhv: Int, rhv: Int, outputName: String) {
    val outputStream = File(outputName).bufferedWriter()
    val result = (lhv * rhv).toString()
    val resultLength = result.length
    val x = lhv.toString()
    var y = rhv.toString()
    val rhvLength = y.length

    fun str(x: String, y: String, pos: Int): String {
        val z = (x.toInt() * y.toInt()).toString()
        val length = z.length

        return if (pos == 0)
            " ".repeat(resultLength + 1 - length) + z
        else "+" + " ".repeat(resultLength - pos - length) + z
    }

    with(outputStream) {
        write(" ".repeat(resultLength + 1 - x.length) + x)
        newLine()
        write("*" + " ".repeat(resultLength - rhvLength) + y)
        newLine()
        write("-".repeat(resultLength + 1))
        newLine()
    }


    for (i in 0 until rhvLength) {
        val number = y.toInt() % 10
        outputStream.write(str(x, number.toString(), i))
        outputStream.newLine()
        y = (y.toInt() / 10).toString()
    }

    with(outputStream) {
        write("-".repeat(resultLength + 1))
        newLine()
        write(" $result")
        close()
    }
}


/**
 * Сложная
 *
 * Вывести в выходной файл процесс деления столбиком числа lhv (> 0) на число rhv (> 0).
 *
 * Пример (для lhv == 19935, rhv == 22):
  19935 | 22
 -198     906
 ----
    13
    -0
    --
    135
   -132
   ----
      3

 * Используемые пробелы, отступы и дефисы должны в точности соответствовать примеру.
 *
 */
fun printDivisionProcess(lhv: Int, rhv: Int, outputName: String) {
    val outputStream = File(outputName).bufferedWriter()
    val lhvToString = lhv.toString()
    val lhvLength = lhvToString.length

    fun getArrayNumber(): List<String> {
        var number = 0
        val result = mutableListOf<String>()
        for (i in 0 until lhvLength) {
            val numeral = "${lhvToString[i]}".toInt()
            number = number * 10 + numeral
            val subtraction = number - number % rhv

            if (0 == number) {
                result.add("0")
                if (i == lhvLength - 1) {
                    result.add("0")
                } else {
                    result.add("0${lhvToString[i + 1]}")
                }
            } else {
                if (number / rhv != 0) {
                    if (number % rhv == 0) {
                        result.add("$number")
                        if (i != lhvLength - 1)
                            result.add("0${lhvToString[i + 1]}")
                        else result.add("0")
                        number = 0
                    } else {
                        if (result.isNotEmpty()) {
                            if (result.size % 2 == 0) {
                                result.add("$subtraction")
                                result.add("${number - subtraction}")
                            } else {
                                result.add("$number")
                                result.add("$subtraction")
                            }
                        } else result.add("$subtraction")
                        number -= subtraction
                    }
                } else {
                    if (i == lhvLength - 1) {
                        result.add("0")
                        result.add("$number")
                    } else if (result.isNotEmpty()) {
                        result.add("$number")
                        result.add("0")
                    }
                }
            }
        }

        if (result.size % 2 != 0)
            result.add("$number")

        return result
    }

    fun start(result: List<String>) {
        val k = result[0]
        val kLength = k.length
        val answer = lhv / rhv

        with(outputStream) {
            if (k == "0" && kLength != lhvLength) {
                write("$lhv | $rhv")
                newLine()
                write(" ".repeat(lhvLength - 2) + "-0" + " ".repeat(3) + "$answer")
                newLine()
                write("-".repeat(kLength + 1))
                newLine()
            } else {
                write(" $lhv | $rhv")
                newLine()
                write("-$k" + " ".repeat(lhvLength - kLength + 3) + "$answer")
                newLine()
                write("-".repeat(kLength + 1))
                newLine()
            }
        }
    }

    fun theMiddle(result: List<String>) {

        fun output(range: Int, rangeTwo: Int, now: String, next: String) {
            with(outputStream) {
                write(" ".repeat(range) + now)
                newLine()
                write(" ".repeat(rangeTwo) + "-" + next)
                newLine()
                write(" ".repeat(rangeTwo) + "-".repeat(next.length + 1))
                newLine()
            }
        }

        var range = result[0].length
        for (i in 1..result.size - 2 step 2) {
            val now = result[i]
            val next = result[i + 1]
            val rangeTwo = range - 1 + (now.length - next.length)

            output(range, rangeTwo, now, next)
            if (next != "0" || now == "00") {
                range = rangeTwo + next.length
            } else range += rangeTwo - now.length - 1
        }
    }

    fun end(result: List<String>) {
        val m = result.last()
        val first = result[0]
        if (first != "0" && (lhvLength == first.length || result.size > 2) || first == "0" && result.size == 2 && lhvLength == first.length)
            outputStream.write(" ".repeat(lhvLength - m.length) + " $m")
        else outputStream.write(" ".repeat(lhvLength - m.length) + m)
    }

    val result = getArrayNumber()
    start(result)
    theMiddle(result)
    end(result)

    outputStream.close()
}