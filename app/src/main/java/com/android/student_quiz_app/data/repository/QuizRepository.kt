package com.android.student_quiz_app.data.repository

import com.android.student_quiz_app.data.model.Question
import com.android.student_quiz_app.data.model.toDomainModel
import com.android.student_quiz_app.data.remote.TriviaApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuizRepository(
    private val apiService: TriviaApiService
) {
    suspend fun fetchQuestions(): Result<List<Question>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getQuestions(limit = 10)
            val questions = response.map { it.toDomainModel() }
            Result.success(questions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
