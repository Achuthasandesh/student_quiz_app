package com.android.student_quiz_app.di

import com.android.student_quiz_app.data.remote.TriviaApiService
import com.android.student_quiz_app.data.repository.QuizRepository
import com.android.student_quiz_app.ui.viewmodel.QuizViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    
    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    single {
        Retrofit.Builder()
            .baseUrl(TriviaApiService.BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    single<TriviaApiService> {
        get<Retrofit>().create(TriviaApiService::class.java)
    }
}

val repositoryModule = module {
    single { QuizRepository(get()) }
}

val viewModelModule = module {
    viewModel { QuizViewModel(get(), get()) }
}

val appModules = listOf(networkModule, repositoryModule, viewModelModule)
