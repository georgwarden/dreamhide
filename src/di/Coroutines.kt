package net.rocketparty.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module.module

val CoroutinesModule = module {
    single { CoroutineScope(Dispatchers.Default + SupervisorJob()) }
}