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

import android.app.Application
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.asLiveData
import com.utsman.networkism.api.LollipopNetwork
import com.utsman.networkism.api.PreLollipopNetwork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class Networkism(private val listener: NetworkismListener) {

    companion object {
        fun flow(application: Application) = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> LollipopNetwork(application).listen()
            else -> PreLollipopNetwork(application).listen()
        }

        fun observer(application: Application, coroutineScope: CoroutineScope) = flow(application)
            .asLiveData(coroutineScope.coroutineContext)


        fun initLifecycle(flow: Flow<NetworkismResult>, coroutineScope: CoroutineScope, listener: NetworkismListener) = run {
            val liveData = flow.asLiveData(coroutineScope.coroutineContext)
            val activity = listener as AppCompatActivity?
            if (activity != null) {
                liveData.observe(activity, {
                    if (it.isConnected) {
                        listener.connectivityAvailable(it.counter)
                    } else {
                        listener.connectivityLost()
                    }
                })
            } else {
                val fragment = listener as FragmentActivity?
                if (fragment != null) {
                    liveData.observe(fragment, {
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

    fun init(coroutineScope: CoroutineScope) = run {
        val activity = listener as AppCompatActivity?
        if (activity != null) {
            observer(activity.application, coroutineScope).observe(activity, {
                if (it.isConnected) {
                    listener.connectivityAvailable(it.counter)
                } else {
                    listener.connectivityLost()
                }
            })
        } else {
            val fragment = listener as FragmentActivity?
            if (fragment != null) {
                observer(fragment.application, coroutineScope).observe(fragment, {
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

