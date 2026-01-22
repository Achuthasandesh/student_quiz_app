package com.android.student_quiz_app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.student_quiz_app.ui.state.QuizState
import com.android.student_quiz_app.ui.theme.BackgroundBlue

@Composable
fun QuizScreen(
    quizState: QuizState.Quiz,
    onAnswerSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentQuestion = quizState.currentQuestion
    val progress by animateFloatAsState(
        targetValue = quizState.progress,
        animationSpec = tween(300),
        label = "progress"
    )
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundBlue)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            QuizHeader(
                currentQuestion = quizState.currentQuestionIndex + 1,
                totalQuestions = quizState.questions.size,
                remainingTime = quizState.remainingTimeSeconds,
                progress = progress,
                score = quizState.score
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            CategoryBadge(category = currentQuestion.category)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            QuestionCard(questionText = currentQuestion.questionText)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            AnswerOptions(
                options = currentQuestion.options,
                selectedAnswer = currentQuestion.selectedAnswer,
                correctAnswer = currentQuestion.correctAnswer,
                showFeedback = quizState.showFeedback,
                onAnswerSelected = onAnswerSelected
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun QuizHeader(
    currentQuestion: Int,
    totalQuestions: Int,
    remainingTime: Int,
    progress: Float,
    score: Int
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Question $currentQuestion/$totalQuestions",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.8f)
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Score: ",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Text(
                    text = "$score",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Green
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = Color.Red,
            trackColor = Color.White.copy(alpha = 0.2f),
            strokeCap = StrokeCap.Round
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TimerDisplay(remainingTime = remainingTime)
    }
}

@Composable
private fun TimerDisplay(remainingTime: Int) {
    val timerColor by animateColorAsState(
        targetValue = when {
            remainingTime <= 3 -> Color.Red
            remainingTime <= 5 -> Color.Yellow
            else -> Color.Green
        },
        animationSpec = tween(300),
        label = "timerColor"
    )
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(timerColor.copy(alpha = 0.2f))
                .border(3.dp, timerColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$remainingTime",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = timerColor
            )
        }
    }
}

@Composable
private fun CategoryBadge(category: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Cyan.copy(alpha = 0.2f))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = category,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}

@Composable
private fun QuestionCard(questionText: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        )
    ) {
        Text(
            text = questionText,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            lineHeight = 28.sp,
            modifier = Modifier.padding(24.dp),
            textAlign = TextAlign.Start
        )
    }
}

@Composable
private fun AnswerOptions(
    options: List<String>,
    selectedAnswer: String?,
    correctAnswer: String,
    showFeedback: Boolean,
    onAnswerSelected: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        options.forEachIndexed { index, option ->
            AnswerOption(
                option = option,
                index = index,
                isSelected = selectedAnswer == option,
                isCorrect = option == correctAnswer,
                showFeedback = showFeedback,
                onClick = { if (!showFeedback) onAnswerSelected(option) }
            )
        }
    }
}

@Composable
private fun AnswerOption(
    option: String,
    index: Int,
    isSelected: Boolean,
    isCorrect: Boolean,
    showFeedback: Boolean,
    onClick: () -> Unit
) {
    val optionLabels = listOf("A", "B", "C", "D")
    
    val backgroundColor by animateColorAsState(
        targetValue = when {
            showFeedback && isCorrect -> Color.Green.copy(alpha = 0.3f)
            showFeedback && isSelected && !isCorrect -> Color(0xFFFF5252).copy(alpha = 0.3f)
            isSelected -> Color.Red.copy(alpha = 0.3f)
            else -> Color.White.copy(alpha = 0.1f)
        },
        animationSpec = tween(300),
        label = "bgColor"
    )
    
    val borderColor by animateColorAsState(
        targetValue = when {
            showFeedback && isCorrect -> Color.Green
            showFeedback && isSelected && !isCorrect -> Color(0xFFFF5252)
            isSelected -> Color.Red
            else -> Color.Transparent
        },
        animationSpec = tween(300),
        label = "borderColor"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isSelected || (showFeedback && isCorrect)) 2.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(enabled = !showFeedback, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            showFeedback && isCorrect -> Color.Green
                            showFeedback && isSelected && !isCorrect -> Color(0xFFFF5252)
                            isSelected -> Color.Red
                            else -> Color.White.copy(alpha = 0.2f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (index < optionLabels.size) optionLabels[index] else "${index + 1}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected || (showFeedback && isCorrect)) Color.White else Color.White.copy(alpha = 0.8f)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = option,
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            
            if (showFeedback) {
                Text(
                    text = when {
                        isCorrect -> "✓"
                        isSelected && !isCorrect -> "✗"
                        else -> ""
                    },
                    fontSize = 20.sp,
                    color = if (isCorrect) Color.Green else Color(0xFFFF5252)
                )
            }
        }
    }
}