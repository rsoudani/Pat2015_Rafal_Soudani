/*
 * Copyright (c) 2014. The Android Open Source Project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.rsoudani.rafalsoudani;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Pattern;

/**
 *  Created by Rafal Soudani on 12-01-2015.
 */
public class LoginActivity extends ActionBarActivity {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,})");

    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setVariables();
    }

    private void setVariables() {
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        Button okButton = (Button) findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    login();
                }
            }
        });
    }

    private boolean validate() {
        return validateEmail() & validatePassword();

    }

    private boolean validateEmail() {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()) {
            email.setError(getString(R.string.invalid_email));
            return false;
        }
        return true;
    }

    private boolean validatePassword() {
        if (!PASSWORD_PATTERN.matcher(password.getText()).matches()) {
            password.setError(getString(R.string.password_validation));
            return false;
        }
        return true;
    }

    private void login() {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
