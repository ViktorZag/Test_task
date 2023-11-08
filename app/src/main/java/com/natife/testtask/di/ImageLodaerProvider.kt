package com.natife.testtask.di

import android.content.Context
import android.os.Build
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.memory.MemoryCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ImageLoaderProvider {

    @Provides
    @Singleton
    fun provideImageLoader(@ApplicationContext context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) add(ImageDecoderDecoder.Factory())
                else add(GifDecoder.Factory())
            }
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.10)
                    .build()
            }
            .build()
    }

}