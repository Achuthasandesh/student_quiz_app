package com.android.student_quiz_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.android.student_quiz_app.ui.screens.ErrorScreen
import com.android.student_quiz_app.ui.screens.LoadingScreen
import com.android.student_quiz_app.ui.screens.QuizScreen
import com.android.student_quiz_app.ui.screens.ResultScreen
import com.android.student_quiz_app.ui.screens.StartQuizScreen
import com.android.student_quiz_app.ui.state.QuizState
import com.android.student_quiz_app.ui.theme.Student_quiz_appTheme
import com.android.student_quiz_app.ui.viewmodel.QuizViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Student_quiz_appTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    QuizApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun QuizApp(
    modifier: Modifier = Modifier,
    viewModel: QuizViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    when (val state = uiState) {
        is QuizState.Start -> {
            StartQuizScreen(
                onStartQuiz = viewModel::startQuiz,
                modifier = modifier
            )
        }
        
        is QuizState.Loading -> {
            LoadingScreen(modifier = modifier)
        }
        
        is QuizState.Quiz -> {
            QuizScreen(
                quizState = state,
                onAnswerSelected = { answer -> viewModel.selectAnswer(answer) },
                modifier = modifier
            )
        }
        
        is QuizState.Result -> {
            ResultScreen(
                resultState = state,
                onRestartQuiz = viewModel::restartQuiz,
                modifier = modifier
            )
        }
        
        is QuizState.Error -> {
            ErrorScreen(
                message = state.message,
                onRetry =  viewModel::retry,
                modifier = modifier
            )
        }
    }
}