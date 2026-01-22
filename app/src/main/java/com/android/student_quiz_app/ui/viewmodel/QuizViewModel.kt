package com.android.student_quiz_app.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.student_quiz_app.data.model.Question
import com.android.student_quiz_app.data.repository.QuizRepository
import com.android.student_quiz_app.ui.state.QuizState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuizViewModel(
    private val repository: QuizRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    companion object {
        private const val QUESTION_DURATION_SECONDS = 10
        private const val KEY_QUESTIONS = "questions"
        private const val KEY_CURRENT_INDEX = "current_index"
        private const val KEY_QUESTION_START_TIME = "question_start_time"
        private const val KEY_SCORE = "score"
        private const val KEY_QUIZ_STARTED = "quiz_started"
        private const val KEY_QUIZ_COMPLETED = "quiz_completed"
    }
    
    private val _uiState = MutableStateFlow<QuizState>(QuizState.Start)
    val uiState: StateFlow<QuizState> = _uiState.asStateFlow()
    
    private var timerJob: Job? = null
    
    private var questions: List<Question>
        get() = savedStateHandle.get<ArrayList<QuestionData>>(KEY_QUESTIONS)
            ?.map { it.toQuestion() } ?: emptyList()
        set(value) {
            savedStateHandle[KEY_QUESTIONS] = ArrayList(value.map { QuestionData.fromQuestion(it) })
        }
    
    private var currentQuestionIndex: Int
        get() = savedStateHandle.get<Int>(KEY_CURRENT_INDEX) ?: 0
        set(value) {
            savedStateHandle[KEY_CURRENT_INDEX] = value
        }
    
    private var questionStartTime: Long
        get() = savedStateHandle.get<Long>(KEY_QUESTION_START_TIME) ?: 0L
        set(value) {
            savedStateHandle[KEY_QUESTION_START_TIME] = value
        }
    
    private var score: Int
        get() = savedStateHandle.get<Int>(KEY_SCORE) ?: 0
        set(value) {
            savedStateHandle[KEY_SCORE] = value
        }
    
    private var quizStarted: Boolean
        get() = savedStateHandle.get<Boolean>(KEY_QUIZ_STARTED) ?: false
        set(value) {
            savedStateHandle[KEY_QUIZ_STARTED] = value
        }
    
    private var quizCompleted: Boolean
        get() = savedStateHandle.get<Boolean>(KEY_QUIZ_COMPLETED) ?: false
        set(value) {
            savedStateHandle[KEY_QUIZ_COMPLETED] = value
        }
    
    init {
        restoreStateIfNeeded()
    }
    
    private fun restoreStateIfNeeded() {
        if (quizCompleted && questions.isNotEmpty()) {
            showResults()
        } else if (quizStarted && questions.isNotEmpty()) {
            resumeQuiz()
        }
    }
    
    private fun resumeQuiz() {
        val currentQuestions = questions
        if (currentQuestions.isEmpty()) return
        
        val elapsedSeconds = ((System.currentTimeMillis() - questionStartTime) / 1000).toInt()
        val remainingTime = (QUESTION_DURATION_SECONDS - elapsedSeconds).coerceAtLeast(0)
        
        val currentQuestion = currentQuestions[currentQuestionIndex]
        
        if (currentQuestion.isAnswered) {
            _uiState.value = QuizState.Quiz(
                questions = currentQuestions,
                currentQuestionIndex = currentQuestionIndex,
                remainingTimeSeconds = 0,
                showFeedback = true,
                score = score
            )
            viewModelScope.launch {
                delay(500)
                moveToNextQuestion()
            }
        } else if (remainingTime <= 0) {
            handleTimeExpired()
        } else {
            _uiState.value = QuizState.Quiz(
                questions = currentQuestions,
                currentQuestionIndex = currentQuestionIndex,
                remainingTimeSeconds = remainingTime,
                showFeedback = false,
                score = score
            )
            startTimer(remainingTime)
        }
    }
    
    fun startQuiz() {
        _uiState.value = QuizState.Loading
        
        viewModelScope.launch {
            repository.fetchQuestions()
                .onSuccess { fetchedQuestions ->
                    questions = fetchedQuestions
                    currentQuestionIndex = 0
                    questionStartTime = System.currentTimeMillis()
                    score = 0
                    quizStarted = true
                    quizCompleted = false
                    
                    _uiState.value = QuizState.Quiz(
                        questions = fetchedQuestions,
                        currentQuestionIndex = 0,
                        remainingTimeSeconds = QUESTION_DURATION_SECONDS,
                        score = 0
                    )
                    
                    startTimer(QUESTION_DURATION_SECONDS)
                }
                .onFailure { exception ->
                    _uiState.value = QuizState.Error(
                        exception.message ?: "Failed to load questions"
                    )
                }
        }
    }
    
    fun selectAnswer(answer: String) {
        val currentState = _uiState.value as? QuizState.Quiz ?: return
        if (currentState.showFeedback) return
        
        timerJob?.cancel()
        
        val currentQuestion = currentState.currentQuestion
        val updatedQuestion = currentQuestion.copy(
            selectedAnswer = answer,
            isAnswered = true
        )
        
        val updatedQuestions = currentState.questions.toMutableList()
        updatedQuestions[currentState.currentQuestionIndex] = updatedQuestion
        questions = updatedQuestions
        
        val newScore = if (updatedQuestion.isCorrect) currentState.score + 1 else currentState.score
        score = newScore
        
        _uiState.value = currentState.copy(
            questions = updatedQuestions,
            showFeedback = true,
            remainingTimeSeconds = 0,
            score = newScore
        )
        
        viewModelScope.launch {
            delay(1500)
            moveToNextQuestion()
        }
    }
    
    private fun startTimer(durationSeconds: Int) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var remaining = durationSeconds
            while (remaining > 0) {
                delay(1000)
                remaining--
                
                val currentState = _uiState.value as? QuizState.Quiz ?: return@launch
                if (!currentState.showFeedback) {
                    _uiState.value = currentState.copy(remainingTimeSeconds = remaining)
                }
            }
            
            handleTimeExpired()
        }
    }
    
    private fun handleTimeExpired() {
        val currentState = _uiState.value as? QuizState.Quiz ?: return
        if (currentState.showFeedback) return
        
        val currentQuestion = currentState.currentQuestion
        val updatedQuestion = currentQuestion.copy(
            isAnswered = true
        )
        
        val updatedQuestions = currentState.questions.toMutableList()
        updatedQuestions[currentState.currentQuestionIndex] = updatedQuestion
        questions = updatedQuestions
        
        _uiState.value = currentState.copy(
            questions = updatedQuestions,
            showFeedback = true,
            remainingTimeSeconds = 0
        )
        
        viewModelScope.launch {
            delay(1500)
            moveToNextQuestion()
        }
    }
    
    private fun moveToNextQuestion() {
        val currentState = _uiState.value as? QuizState.Quiz ?: return
        
        if (currentState.isLastQuestion) {
            quizCompleted = true
            showResults()
        } else {
            val nextIndex = currentState.currentQuestionIndex + 1
            currentQuestionIndex = nextIndex
            questionStartTime = System.currentTimeMillis()
            
            _uiState.value = currentState.copy(
                currentQuestionIndex = nextIndex,
                remainingTimeSeconds = QUESTION_DURATION_SECONDS,
                showFeedback = false
            )
            
            startTimer(QUESTION_DURATION_SECONDS)
        }
    }
    
    private fun showResults() {
        val allQuestions = questions
        val correctCount = allQuestions.count { it.isCorrect }
        
        _uiState.value = QuizState.Result(
            totalQuestions = allQuestions.size,
            correctAnswers = correctCount,
            questions = allQuestions
        )
    }
    
    fun restartQuiz() {
        timerJob?.cancel()
        quizStarted = false
        quizCompleted = false
        questions = emptyList()
        currentQuestionIndex = 0
        score = 0
        _uiState.value = QuizState.Start
    }
    
    fun retry() {
        startQuiz()
    }
    
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

@kotlinx.parcelize.Parcelize
data class QuestionData(
    val id: String,
    val questionText: String,
    val category: String,
    val difficulty: String,
    val correctAnswer: String,
    val options: List<String>,
    val selectedAnswer: String?,
    val isAnswered: Boolean
) : android.os.Parcelable {
    
    fun toQuestion(): Question = Question(
        id = id,
        questionText = questionText,
        category = category,
        difficulty = difficulty,
        correctAnswer = correctAnswer,
        options = options,
        selectedAnswer = selectedAnswer,
        isAnswered = isAnswered
    )
    
    companion object {
        fun fromQuestion(question: Question): QuestionData = QuestionData(
            id = question.id,
            questionText = question.questionText,
            category = question.category,
            difficulty = question.difficulty,
            correctAnswer = question.correctAnswer,
            options = question.options,
            selectedAnswer = question.selectedAnswer,
            isAnswered = question.isAnswered
        )
    }
}
