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

package com.utsman.networkism.sample

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utsman.networkism.Networkism
import com.utsman.networkism.api.NetworkismApi
import com.utsman.networkism.sample.retrofit.Instance
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val network = Instance.builder()
    private val _users: MutableLiveData<List<String>> = MutableLiveData()
    val users: LiveData<List<String>> = _users

    @FlowPreview
    fun getUsers(page: Int = 1, networkismApi: NetworkismApi) = viewModelScope.launch {
        val networkism = Networkism.instance(networkismApi)

        networkism.checkConnection()
            .map { network.getUser(page) }
            .map { it.data.map { d -> d.lastName } }
            .collect {
                _users.postValue(it)
            }
    }
}