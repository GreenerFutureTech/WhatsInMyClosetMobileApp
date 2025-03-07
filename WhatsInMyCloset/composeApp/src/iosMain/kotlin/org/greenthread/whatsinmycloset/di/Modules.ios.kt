package org.greenthread.whatsinmycloset.di

import io.ktor.client.engine.HttpClientEngine
import org.koin.core.module.Module
import org.koin.dsl.module
import io.ktor.client.engine.darwin.Darwin
import org.greenthread.whatsinmycloset.DatabaseFactory

actual val platformModule: Module
    get() = module {
        single<HttpClientEngine> {Darwin.create()}
        single { DatabaseFactory() }
    }