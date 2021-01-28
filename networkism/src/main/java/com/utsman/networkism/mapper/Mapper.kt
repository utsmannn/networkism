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

package com.utsman.networkism.mapper

import com.utsman.networkism.model.NetworkismResult
import com.utsman.networkism.model.UrlConnectionBuilder
import com.utsman.networkism.utils.loge
import com.utsman.networkism.utils.request
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map

internal object Mapper {
    fun listenOn(
        builder: UrlConnectionBuilder,
        listenFlow: Flow<NetworkismResult>
    ): Flow<NetworkismResult> {

        return if (builder.okHttpClient != null && builder.url != null) {
            builder.okHttpClient!!.request(builder.url!!)
                .flatMapConcat { a ->
                    listenFlow.map {
                        NetworkismResult.simple {
                            this.isConnected = a.isConnected
                            this.reason = a.reason
                        }
                    }
                }
        } else {
            loge("Connection Builder NOT WORK!, Okhttp client or url must be fill")
            listenFlow
        }
    }
}