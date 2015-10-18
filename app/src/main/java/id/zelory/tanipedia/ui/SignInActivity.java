/*
 * Copyright (c) 2015 Zetra.
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

package id.zelory.tanipedia.ui;

import android.os.Bundle;

import id.zelory.benih.BenihActivity;
import id.zelory.tanipedia.R;
import id.zelory.tanipedia.ui.fragment.LoginFragment;

public class SignInActivity extends BenihActivity
{
    @Override
    protected int getActivityView()
    {
        return R.layout.activity_signin;
    }

    @Override
    protected void onViewReady(Bundle bundle)
    {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.sign_in_container, new LoginFragment())
                .commit();
    }
}