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

package com.utsman.networkism.api

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import com.utsman.networkism.utils.ConstantValue
import com.utsman.networkism.model.NetworkismResult
import com.utsman.networkism.utils.logi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class LollipopNetworkism(context: Context) : NetworkismApi {
    private val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val requestBuilder = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    override fun listen() = callbackFlow<NetworkismResult> {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                val capabilities = manager.getNetworkCapabilities(network)
                val down = capabilities?.linkDownstreamBandwidthKbps ?: 0
                val up = capabilities?.linkUpstreamBandwidthKbps ?: 0
                logi("Connectivity available")
                logi("down is -> $down -> up is -> $up")

                offer(NetworkismResult.simple {
                    this.counter = NetworkismResult.Counter(down, up)
                    this.isConnected = true
                    this.reason = ConstantValue.CONNECTIVITY_AVAILABLE
                })
            }

            override fun onUnavailable() {
                super.onUnavailable()
                logi("Connectivity unavailable")
                offer(NetworkismResult.simple {
                    this.counter = null
                    this.isConnected = false
                    this.reason = ConstantValue.CONNECTIVITY_UNAVAILABLE
                })
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                logi("Connectivity lost")
                val capabilities = manager.getNetworkCapabilities(network)
                val down = capabilities?.linkDownstreamBandwidthKbps ?: 0
                val up = capabilities?.linkUpstreamBandwidthKbps ?: 0

                offer(NetworkismResult.simple {
                    this.counter = NetworkismResult.Counter(down, up)
                    this.isConnected = false
                    this.reason = ConstantValue.CONNECTIVITY_LOST
                })
            }
        }

        manager.registerNetworkCallback(requestBuilder, callback)
        awaitClose { manager.unregisterNetworkCallback(callback) }
    }
}