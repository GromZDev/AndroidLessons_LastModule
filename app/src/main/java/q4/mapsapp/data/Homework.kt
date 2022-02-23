package q4.mapsapp.data


data class Homework(
    val id: Int,
    val subject: String = "",
    val task: String = "",
    val time: Int = 0,
    val image: Int? = null
)

fun getAllHomeworks() = mutableListOf(
    Homework(1,"Литература", "Прочитать Война и Мир", 3),
    Homework(2,"Математика", "Сделать задание 433-440", 2),
    Homework(3,"Физика", "Подтвердить теорию относительности Эйнштейна", 6),
    Homework(4,"Русский язык", "Выучить все падежи", 1),
    Homework(5,"География", "Назвать страны на букву Г", 3),
    Homework(6,"Английский", "Перевести первую главу Войны и Мир", 5),
)