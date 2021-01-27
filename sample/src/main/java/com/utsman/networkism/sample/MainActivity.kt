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

package com.utsman.networkism.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.utsman.networkism.Networkism
import com.utsman.networkism.NetworkismListener
import com.utsman.networkism.NetworkismResult
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.FlowPreview

@FlowPreview
class MainActivity : AppCompatActivity(), NetworkismListener {
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val api = Networkism.provideNetworkismApi(this)
        val networkism = Networkism.instance(api)
        networkism
            .asLiveData(lifecycleScope)
            .observe(this, Observer { networkResult ->
                if (networkResult.isConnected) {
                    // connected
                } else {
                    // disconnected
                }
            })

        viewModel.users.observe(this, {
            txt_log.text = "$it"
        })

        btn_test.setOnClickListener {
            txt_log.text = "..."
            viewModel.getUsers(2, api)
        }
    }

    override fun connectivityAvailable(counter: NetworkismResult.Counter?) {
        txt_status.text = "connect"
    }

    override fun connectivityLost() {
        txt_status.text = "disconnect"
    }
}