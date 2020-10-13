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

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.utsman.networkism.NetworkismResult
import com.utsman.networkism.logi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class PreLollipopNetwork(private val application: Application) {

    fun listen() = callbackFlow<NetworkismResult> {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        val broadcast = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.extras != null) {
                    val networkInfo = intent.extras?.get(ConnectivityManager.EXTRA_NETWORK_INFO) as NetworkInfo?
                    val isAvailable = networkInfo != null && networkInfo.isConnectedOrConnecting

                    if (isAvailable) logi("Connectivity available")
                    else logi("Connectivity lost")

                    offer(NetworkismResult.simple {
                        this.counter = null
                        this.isConnected = isAvailable
                    })
                }
            }
        }

        application.registerReceiver(broadcast, filter)
        awaitClose { application.unregisterReceiver(broadcast) }
    }
}