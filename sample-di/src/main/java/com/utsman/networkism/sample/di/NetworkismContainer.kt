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

package com.utsman.networkism.sample.di

import android.app.Application
import android.util.Log
import androidx.lifecycle.asLiveData
import com.utsman.networkism.Networkism
import kotlinx.coroutines.CoroutineScope

class NetworkismContainer(application: Application) {

    init {
        Log.i("SAMPLE DI", "Creating container..")
    }

    val networkism = Networkism.flow(application)
    fun observer(coroutineScope: CoroutineScope) = networkism
        .asLiveData(coroutineScope.coroutineContext)
}