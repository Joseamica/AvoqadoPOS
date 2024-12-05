//package com.avoqadoapp.di
//
//import android.content.Context
//import com.avoqadoapp.core.navigation.NavigationCommand
//import com.avoqadoapp.core.navigation.NavigationManager
//import com.menta.android.core.repository.SendEmailRepository
//import com.menta.android.core.repository.SendEmailRepositoryImpl
//import com.menta.android.core.repository.TerminalAPI
//import com.menta.android.core.repository.TrxRepository
//import com.menta.android.core.repository.TrxRepositoryImpl
//import com.menta.android.core.viewmodel.ExternalTokenData
//import com.menta.android.core.viewmodel.MasterKeyData
//import com.menta.android.restclient.core.Storage
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import kotlinx.coroutines.flow.MutableSharedFlow
//import kotlinx.coroutines.flow.asSharedFlow
//import javax.inject.Singleton
//
//@Module
//@InstallIn(SingletonComponent::class)
//object AppModule {
//    @Singleton
//    @Provides
//    fun provideNavigationManager(): NavigationManager {
//        return object : NavigationManager {
//
//            private val _navActions = MutableSharedFlow<NavigationCommand>(extraBufferCapacity = 10)
//            override val navActions = _navActions.asSharedFlow()
//
//            override fun navigate(command: NavigationCommand) {
//                _navActions.tryEmit(command)
//            }
//
//        }
//    }
//
////    @Singleton
////    @Provides
////    fun provideSendEmailRepository(terminalAPI: TerminalAPI): SendEmailRepository =
////        SendEmailRepositoryImpl(terminalAPI = terminalAPI)
////
////    @Singleton
////    @Provides
////    fun provideTrxRepository(terminalAPI: TerminalAPI): TrxRepository =
////        TrxRepositoryImpl(terminalAPI = terminalAPI)
//
////    @Singleton
////    @Provides
////    fun provideStorage(
////        @ApplicationContext context: Context
////    ) = Storage(context)
////
////
////    @Singleton
////    @Provides
////    fun provideExternalTokenData(@ApplicationContext context: Context) = ExternalTokenData(context)
////
////    @Singleton
////    @Provides
////    fun provideMasterKeyData(@ApplicationContext context: Context) = MasterKeyData(context)
//
//}