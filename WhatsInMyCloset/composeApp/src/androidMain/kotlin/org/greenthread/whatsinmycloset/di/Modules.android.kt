package org.greenthread.whatsinmycloset.di

import io.ktor.client.engine.HttpClientEngine
import org.koin.core.module.Module
import org.koin.dsl.module
import io.ktor.client.engine.okhttp.OkHttp

actual val platformModule: Module
    get() = module {
        single<HttpClientEngine> {OkHttp.create()}
    }