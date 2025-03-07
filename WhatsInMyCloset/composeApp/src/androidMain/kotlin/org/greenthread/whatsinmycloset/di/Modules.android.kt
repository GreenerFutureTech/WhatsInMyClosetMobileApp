package org.greenthread.whatsinmycloset.di

import io.ktor.client.engine.HttpClientEngine
import org.koin.core.module.Module
import org.koin.dsl.module
import io.ktor.client.engine.okhttp.OkHttp
import org.greenthread.whatsinmycloset.DatabaseFactory
import org.koin.android.ext.koin.androidApplication

actual val platformModule: Module
    get() = module {
        single<HttpClientEngine> {OkHttp.create()}
        single { DatabaseFactory(androidApplication()) }
    }