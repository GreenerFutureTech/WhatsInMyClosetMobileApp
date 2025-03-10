package org.greenthread.whatsinmycloset.di

import androidx.lifecycle.SavedStateHandle
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import org.greenthread.whatsinmycloset.DatabaseFactory
import org.greenthread.whatsinmycloset.core.data.HttpClientFactory
import org.greenthread.whatsinmycloset.core.data.MyClosetDatabase
import org.greenthread.whatsinmycloset.core.domain.models.Account
import org.greenthread.whatsinmycloset.core.managers.WardrobeManager
import org.greenthread.whatsinmycloset.core.network.KtorRemoteDataSource
import org.greenthread.whatsinmycloset.core.network.RemoteClosetDataSource
import org.greenthread.whatsinmycloset.core.repositories.WardrobeRepository
import org.greenthread.whatsinmycloset.core.repository.ClosetRepository
import org.greenthread.whatsinmycloset.core.repository.DefaultClosetRepository
import org.greenthread.whatsinmycloset.features.screens.login.presentation.LoginViewModel
import org.greenthread.whatsinmycloset.features.tabs.swap.presentation.SelectedSwapViewModel
import org.greenthread.whatsinmycloset.features.tabs.swap.viewmodel.SwapViewModel
import org.greenthread.whatsinmycloset.core.viewmodels.ClothingItemViewModel
import org.greenthread.whatsinmycloset.features.tabs.swap.presentation.Message.MessageViewModel
import org.greenthread.whatsinmycloset.core.viewmodels.OutfitViewModel
import org.greenthread.whatsinmycloset.features.tabs.home.presentation.HomeTabViewModel
import org.greenthread.whatsinmycloset.features.screens.addItem.presentation.AddItemScreenViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule : Module


val sharedModule = module {
    single { HttpClientFactory.create(get())}

    singleOf(::KtorRemoteDataSource).bind<RemoteClosetDataSource>()
    singleOf(::DefaultClosetRepository).bind<ClosetRepository>()
    singleOf(::WardrobeRepository).bind<WardrobeRepository>()
    singleOf(::WardrobeManager).bind<WardrobeManager>()


    single {
        get<DatabaseFactory>().create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    single{ get<MyClosetDatabase>().wardrobeDao()}
    viewModelOf(::HomeTabViewModel)
    viewModelOf(::AddItemScreenViewModel)

    viewModelOf(::SelectedSwapViewModel)
    viewModelOf(::SwapViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::MessageViewModel)

    viewModelOf(::ClothingItemViewModel)

    single { Account(userId = "user123", name = "Test User") } // Replace with actual user info

    viewModel { OutfitViewModel(get(), get()) } // Pass Account and SavedStateHandle
}


