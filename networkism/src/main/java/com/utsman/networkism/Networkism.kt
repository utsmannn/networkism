/*
 * Copyright (c) 2020 Muhammad Utsman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.utsman.networkism

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.asLiveData
import com.utsman.networkism.api.LollipopNetworkism
import com.utsman.networkism.api.NetworkismApi
import com.utsman.networkism.api.PreLollipopNetworkism
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class Networkism(private val networkismApi: NetworkismApi) {

    companion object {
        fun provideNetworkismApi(context: Context): NetworkismApi = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> LollipopNetworkism(context)
            else -> PreLollipopNetworkism(context)
        }

        private var instance: Networkism? = null
        fun instance(networkismApi: NetworkismApi): Networkism {
            if (instance == null) {
                instance = Networkism(networkismApi)
            }
            return instance!!
        }
    }

    fun listenConnection() = networkismApi.listen().map { it.isConnected }

    fun checkConnection() = flow { emit(listenConnection().first()) }

    fun asLiveData(coroutineScope: CoroutineScope) = networkismApi.listen()
        .asLiveData(coroutineScope.coroutineContext)

    fun setNetworkismListener(
        coroutineScope: CoroutineScope,
        listener: NetworkismListener
    ) = run {
        val liveData = networkismApi.listen().asLiveData(coroutineScope.coroutineContext)
        when (listener) {
            is AppCompatActivity -> {
                liveData.observe(listener, {
                    if (it.isConnected) {
                        listener.connectivityAvailable(it.counter)
                    } else {
                        listener.connectivityLost()
                    }
                })
            }
            is FragmentActivity -> {
                liveData.observe(listener, {
                    if (it.isConnected) {
                        listener.connectivityAvailable(it.counter)
                    } else {
                        listener.connectivityLost()
                    }
                })
            }
        }
    }
}

