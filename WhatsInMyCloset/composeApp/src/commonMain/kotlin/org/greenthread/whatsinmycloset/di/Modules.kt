package org.greenthread.whatsinmycloset.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import org.greenthread.whatsinmycloset.DatabaseFactory
import org.greenthread.whatsinmycloset.core.data.HttpClientFactory
import org.greenthread.whatsinmycloset.core.data.MyClosetDatabase
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.core.domain.models.User
import org.greenthread.whatsinmycloset.core.managers.OutfitManager
import org.greenthread.whatsinmycloset.core.managers.WardrobeManager
import org.greenthread.whatsinmycloset.core.network.KtorRemoteDataSource
import org.greenthread.whatsinmycloset.core.network.RemoteClosetDataSource
import org.greenthread.whatsinmycloset.core.repositories.OutfitRepository
import org.greenthread.whatsinmycloset.core.repositories.OutfitTags
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
import org.greenthread.whatsinmycloset.features.tabs.profile.ProfileTabViewModel
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
    singleOf(::UserManager).bind<UserManager>()


    single {
        get<DatabaseFactory>().create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    single{ get<MyClosetDatabase>().wardrobeDao()}
    single{ get<MyClosetDatabase>().itemDao()}
    single{ get<MyClosetDatabase>().outfitDao()}

    viewModelOf(::HomeTabViewModel)
    viewModelOf(::AddItemScreenViewModel)
    viewModelOf(::ProfileTabViewModel)

    viewModelOf(::SelectedSwapViewModel)
    viewModelOf(::SwapViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::MessageViewModel)

    viewModelOf(::ClothingItemViewModel)

    single {
        User(99999123, "TestName",
        email = "testmail",
        firebaseUuid = "",
        lastLogin = "01-01-2025",
        name = "testName",
        registeredAt = "01-01-2025",
        updatedAt = "01-01-2025")
    } // Replace with actual user info

    // Define OutfitRepository
    single<OutfitRepository> { OutfitRepository(get(), get()) } // Inject OutfitDao and ItemDao

    // Define OutfitManager (depends on OutfitRepository, OutfitTags, and UserManager)
    single<OutfitManager> { OutfitManager(get(), get(), get()) }

    // Define OutfitTags
    single { OutfitTags(get()) } // Inject User into OutfitTags

    // Define the OutfitViewModel with explicit parameters
    viewModel {
        OutfitViewModel(
            outfitManager = get(), // Inject the OutfitManager instance
            outfitTags = get(), // Inject the OutfitTags instance
            itemDao = get(), // Inject the ItemDao instance
            savedStateHandle = null // Optional parameter, can be null
        )
    }
}


