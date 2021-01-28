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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.utsman.networkism.utils.ConstantValue
import com.utsman.networkism.model.NetworkismResult
import com.utsman.networkism.utils.logi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class PreLollipopNetworkism(private val context: Context) : NetworkismApi {

    override fun listen() = callbackFlow<NetworkismResult> {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        val broadcast = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.extras != null) {
                    val networkInfo = intent.extras?.get(ConnectivityManager.EXTRA_NETWORK_INFO) as NetworkInfo?
                    val isAvailable = networkInfo != null && networkInfo.isConnectedOrConnecting

                    if (isAvailable) logi("Connectivity available")
                    else logi("Connectivity lost")

                    val reason = if (isAvailable) {
                        ConstantValue.CONNECTIVITY_AVAILABLE
                    } else {
                        ConstantValue.CONNECTIVITY_UNAVAILABLE
                    }

                    offer(NetworkismResult.simple {
                        this.counter = null
                        this.isConnected = isAvailable
                        this.reason = reason
                    })
                }
            }
        }

        context.registerReceiver(broadcast, filter)
        awaitClose { context.unregisterReceiver(broadcast) }
    }
}