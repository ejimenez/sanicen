/**
 *   Sanicen
 *
 *   @author Emilio Jiménez del Moral
 *   Copyright (C) 2016 Emilio Jiménez del Moral
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License version 2,
 *   as published by the Free Software Foundation.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */


package com.argenes.android.sanicen.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.argenes.android.sanicen.R;
import com.argenes.android.sanicen.utils.NavigationManager;

public class DetailCenterActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_center);

        String title = NavigationManager.getData(this, "title");
        String snippet = NavigationManager.getData(this, "snippet");

        getLayoutInflater().inflate(R.layout.activity_detail_center, null);
        TextView tvTitle = ((TextView)findViewById(R.id.title));
        tvTitle.setText(title);
        TextView tvSnippet= ((TextView)findViewById(R.id.snippet));
        tvSnippet.setMovementMethod(LinkMovementMethod.getInstance());
        tvSnippet.setText(Html.fromHtml(snippet));

    }


}
