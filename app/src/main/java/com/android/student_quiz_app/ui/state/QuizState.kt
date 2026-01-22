package com.android.student_quiz_app.ui.state

import com.android.student_quiz_app.data.model.Question

sealed class QuizState {
    data object Start : QuizState()
    
    data object Loading : QuizState()
    
    data class Quiz(
        val questions: List<Question>,
        val currentQuestionIndex: Int,
        val remainingTimeSeconds: Int,
        val showFeedback: Boolean = false,
        val score: Int = 0
    ) : QuizState() {
        val currentQuestion: Question
            get() = questions[currentQuestionIndex]
        
        val isLastQuestion: Boolean
            get() = currentQuestionIndex == questions.size - 1
        
        val progress: Float
            get() = (currentQuestionIndex + 1).toFloat() / questions.size
    }
    
    data class Result(
        val totalQuestions: Int,
        val correctAnswers: Int,
        val questions: List<Question>
    ) : QuizState() {
        val scorePercentage: Int
            get() = ((correctAnswers.toFloat() / totalQuestions) * 100).toInt()
    }
    
    data class Error(val message: String) : QuizState()
}
