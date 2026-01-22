package com.android.student_quiz_app.data.remote

import com.android.student_quiz_app.data.model.QuestionResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TriviaApiService {
    
    @GET("v2/questions")
    suspend fun getQuestions(
        @Query("limit") limit: Int = 10
    ): List<QuestionResponse>
    
    companion object {
        const val BASE_URL = "https://the-trivia-api.com/"
    }
}
