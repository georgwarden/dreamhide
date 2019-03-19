package net.rocketparty.di

import com.google.gson.GsonBuilder
import org.koin.dsl.module.module

val SerializationModule = module {
    single { GsonBuilder().create() }
}