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
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.utsman.networkism.Networkism
import com.utsman.networkism.NetworkismListener
import com.utsman.networkism.NetworkismResult
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NetworkismListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Networkism(this).init(lifecycleScope)

        // or
        Networkism.observer(application, lifecycleScope).observe(this, {
            it.counter
            if (it.isConnected) {
                txt_log.text = "uhuy"
            } else {
                txt_log.text = "error"
            }
        })
    }

    override fun connectivityAvailable(counter: NetworkismResult.Counter?) {
        txt_log.text = "connect"
    }

    override fun connectivityLost() {
        txt_log.text = "disconnect"
    }
}