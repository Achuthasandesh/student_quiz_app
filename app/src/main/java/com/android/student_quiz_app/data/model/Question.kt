package com.android.student_quiz_app.data.model

import com.google.gson.annotations.SerializedName

data class QuestionResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("correctAnswer")
    val correctAnswer: String,
    @SerializedName("incorrectAnswers")
    val incorrectAnswers: List<String>,
    @SerializedName("question")
    val question: QuestionText,
    @SerializedName("difficulty")
    val difficulty: String
)

data class QuestionText(
    @SerializedName("text")
    val text: String
)

data class Question(
    val id: String,
    val questionText: String,
    val category: String,
    val difficulty: String,
    val correctAnswer: String,
    val options: List<String>,
    val selectedAnswer: String? = null,
    val isAnswered: Boolean = false
) {
    val isCorrect: Boolean
        get() = selectedAnswer == correctAnswer
}

fun QuestionResponse.toDomainModel(): Question {
    val allOptions = (incorrectAnswers + correctAnswer).shuffled()
    return Question(
        id = id,
        questionText = question.text,
        category = category,
        difficulty = difficulty,
        correctAnswer = correctAnswer,
        options = allOptions
    )
}
