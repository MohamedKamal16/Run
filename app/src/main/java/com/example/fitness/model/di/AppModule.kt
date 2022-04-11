package com.example.fitness.model.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.example.fitness.model.db.RunDataBase
import com.example.fitness.util.Constant.KEY_FIRST_TIME_TOGGLE
import com.example.fitness.util.Constant.KEY_NAME
import com.example.fitness.util.Constant.KEY_WEIGHT
import com.example.fitness.util.Constant.ROOM_DATABASE_NAME
import com.example.fitness.util.Constant.SHARED_PREFERENCES_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
1. Make BasicApplication and pass it in Manifest
2.Make App Module
to determine life time of inject the dependency
1.ApplicationComponent: life time of the application deprecated use SingletonComponent
2.FragmentComponent: life time of Fragment
3.ActivityComponent: life time of Activity
4.ServiceComponent: life time of Service
3.some annotation and keyword and its use
1. @Singleton:To have single instance from this function
2. @Provides:To make hilt understand that the return of this function i need to inject it
3.@HiltViewModel on viewModel instead of @ViewModelInject deprecated
4.@HiltAndroidApp on BasicApplication
5.private val viewModel: StatisticViewModel by viewModels() to inject viewModel Factory in Fragment
6.@AndroidEntryPoint : in any activity or fragment we use hilt on it
7. @Inject: to inject variable in constructor or out of it
8.@InstallIn:on function on app module that we write to inject what its inside after that
9.@Module:TO define my appModule


 */

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideRunDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        RunDataBase::class.java,
        ROOM_DATABASE_NAME
    ).build()


    /**
    The db in constructor gonna be get from the return of first fun(provideRunDatabase)
     */
    @Singleton
    @Provides
    fun provideRunDao(db: RunDataBase) = db.runDao()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app:Context)=app.getSharedPreferences(SHARED_PREFERENCES_NAME,MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideName(sharedPreferences: SharedPreferences)=sharedPreferences.getString(KEY_NAME," ")?:""
    @Singleton
    @Provides
    fun provideWeight(sharedPreferences: SharedPreferences)=sharedPreferences.getFloat(KEY_WEIGHT,80f)
    @Singleton
    @Provides
    fun provideFirstTimeToggle(sharedPreferences: SharedPreferences)=sharedPreferences.getBoolean(KEY_FIRST_TIME_TOGGLE,true)
/*
//Note i write function in two ways one withe = and one with {}and return keyword to understand the return of each function
/*
    @Singleton
   @Provides
   fun provideDatabase( @ApplicationContext context: Context):RunDataBase {
   return  Room.databaseBuilder(context, RunDataBase::class.java, ROOM_DATABASE_NAME).build() }
      * */
/* @Singleton
 @Provides
 fun provideRunDao(db:RunDataBase):RunDao{return db.runDao() }
 **/
*/

}