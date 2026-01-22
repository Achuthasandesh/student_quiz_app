package com.android.student_quiz_app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.student_quiz_app.data.model.Question
import com.android.student_quiz_app.ui.state.QuizState
import com.android.student_quiz_app.ui.theme.BackgroundBlue

@Composable
fun ResultScreen(
    resultState: QuizState.Result,
    onRestartQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = tween(durationMillis = 500),
        label = "scale"
    )
    
    val safeDrawingPadding = WindowInsets.safeDrawing.asPaddingValues()
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundBlue)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = safeDrawingPadding.calculateTopPadding() + 20.dp,
                bottom = safeDrawingPadding.calculateBottomPadding() + 20.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                ScoreSection(
                    correctAnswers = resultState.correctAnswers,
                    totalQuestions = resultState.totalQuestions,
                    scorePercentage = resultState.scorePercentage,
                    scale = scale
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                PerformanceMessage(scorePercentage = resultState.scorePercentage)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = onRestartQuiz,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE94560)
                    )
                ) {
                    Text(
                        text = "Play Again",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = "Question Summary",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            itemsIndexed(resultState.questions) { index, question ->
                QuestionSummaryCard(
                    questionNumber = index + 1,
                    question = question
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun ScoreSection(
    correctAnswers: Int,
    totalQuestions: Int,
    scorePercentage: Int,
    scale: Float
) {
    Column(
        modifier = Modifier.scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = when {
                scorePercentage >= 80 -> "🏆"
                scorePercentage >= 60 -> "🌟"
                scorePercentage >= 40 -> "👍"
                else -> "💪"
            },
            fontSize = 72.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Quiz Complete!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(
                    when {
                        scorePercentage >= 80 -> Color(0xFF4CAF50)
                        scorePercentage >= 60 -> Color(0xFFFFB74D)
                        scorePercentage >= 40 -> Color(0xFFFF9800)
                        else -> Color(0xFFE94560)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$correctAnswers/$totalQuestions",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "$scorePercentage%",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
private fun PerformanceMessage(scorePercentage: Int) {
    val (title, message) = when {
        scorePercentage >= 80 -> "Excellent!" to "You're a trivia master! Outstanding performance!"
        scorePercentage >= 60 -> "Great Job!" to "You have impressive knowledge. Keep it up!"
        scorePercentage >= 40 -> "Good Effort!" to "Not bad! Practice makes perfect."
        else -> "Keep Learning!" to "Every quiz is a chance to learn something new!"
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = when {
                scorePercentage >= 80 -> Color(0xFF4CAF50)
                scorePercentage >= 60 -> Color(0xFFFFB74D)
                scorePercentage >= 40 -> Color(0xFFFF9800)
                else -> Color(0xFFE94560)
            }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun QuestionSummaryCard(
    questionNumber: Int,
    question: Question
) {
    val isCorrect = question.isCorrect
    val wasAnswered = question.selectedAnswer != null
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCorrect -> Color(0xFF4CAF50)
                                wasAnswered -> Color(0xFFFF5252)
                                else -> Color(0xFFFFB74D)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when {
                            isCorrect -> "✓"
                            wasAnswered -> "✗"
                            else -> "–"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "Question $questionNumber",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.8f)
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = when {
                        isCorrect -> "Correct"
                        wasAnswered -> "Incorrect"
                        else -> "Time's up"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = when {
                        isCorrect -> Color(0xFF4CAF50)
                        wasAnswered -> Color(0xFFFF5252)
                        else -> Color(0xFFFFB74D)
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = question.questionText,
                fontSize = 14.sp,
                color = Color.White,
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (wasAnswered) {
                Row {
                    Text(
                        text = "Your answer: ",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = question.selectedAnswer ?: "",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFFF5252)
                    )
                }
            }
            
            if (!isCorrect) {
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = "Correct answer: ",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = question.correctAnswer,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }
    }
}