/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.beantoan.smsbackup.util

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager


/**
 * This provides methods to help Activities load their UI.
 */
object ActivityUtils {

    fun addFragmentToActivity(fragmentManager: FragmentManager,
                              fragment: Fragment, frameId: Int) {
        ActivityUtils.addFragmentToActivity(fragmentManager, fragment, frameId, null)
    }

    /**
     * The `fragment` is added to the container view with id `frameId`. The operation is
     * performed by the `fragmentManager`.
     *
     */
    fun addFragmentToActivity(fragmentManager: FragmentManager,
                              fragment: Fragment, frameId: Int, tag: String?) {
        val transaction = fragmentManager.beginTransaction()
        if (tag == null) {
            transaction.add(frameId, fragment)
        } else {
            transaction.add(frameId, fragment, tag)
        }
        transaction.commit()
    }

    fun replaceFragmentToPlaceholder(fragmentManager: FragmentManager,
                                     fragment: Fragment, frameId: Int, tag: String?) {
        val transaction = fragmentManager.beginTransaction()
        if (tag == null) {
            transaction.replace(frameId, fragment)
        } else {
            transaction.replace(frameId, fragment, tag)
        }
        transaction.addToBackStack(null)

        transaction.commit()
    }

}
