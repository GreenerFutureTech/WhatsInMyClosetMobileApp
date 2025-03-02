package org.greenthread.whatsinmycloset.di

import org.greenthread.whatsinmycloset.core.data.HttpClientFactory
import org.greenthread.whatsinmycloset.core.network.KtorRemoteDataSource
import org.greenthread.whatsinmycloset.core.network.RemoteClosetDataSource
import org.greenthread.whatsinmycloset.core.repository.ClosetRepository
import org.greenthread.whatsinmycloset.core.repository.DefaultClosetRepository
import org.greenthread.whatsinmycloset.features.screens.login.presentation.LoginViewModel
import org.greenthread.whatsinmycloset.features.tabs.swap.presentation.SelectedSwapViewModel
import org.greenthread.whatsinmycloset.features.tabs.swap.viewmodel.SwapViewModel
import org.greenthread.whatsinmycloset.core.viewmodels.ClothingItemViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind

import org.koin.dsl.module

expect val platformModule : Module


val sharedModule = module {
    single { HttpClientFactory.create(get())}

    singleOf(::KtorRemoteDataSource).bind<RemoteClosetDataSource>()
    singleOf(::DefaultClosetRepository).bind<ClosetRepository>()

    viewModelOf(::SelectedSwapViewModel)
    viewModelOf(::SwapViewModel)
    viewModelOf(::LoginViewModel)

    viewModelOf(::OutfitViewModel)
    viewModelOf(::ClothingItemViewModel)

}


