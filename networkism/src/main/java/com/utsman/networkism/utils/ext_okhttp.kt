/*
 * Copyright (c) 2021 Muhammad Utsman
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

package com.utsman.networkism.utils

import com.utsman.networkism.model.NetworkismResult
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.net.HttpRetryException
import java.net.UnknownHostException

internal fun OkHttpClient.request(url: String, networkismResult: NetworkismResult): Flow<NetworkismResult> = channelFlow {
    val job = GlobalScope.launch {
        val request = Request.Builder()
            .url(url)
            .build()

        val response = try {
            if (networkismResult.isConnected) {
                newCall(request).execute()
            } else {
                null
            }
        } catch (e: Throwable) {
            networkismResult.reason = "Connecting url failure"
            null
        }

        val result = NetworkismResult.simple {
            if (response?.isSuccessful == true) {
                isConnected = true
                reason = "Url connected on $url"
                counter = networkismResult.counter
            } else {
                isConnected = false
                reason = networkismResult.reason
                counter = networkismResult.counter
            }
        }

        logi("result is --> ${result.reason}")
        offer(result)
    }

    awaitClose { job.cancel() }
}