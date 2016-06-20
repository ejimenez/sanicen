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

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.argenes.android.sanicen.R;
import com.argenes.android.sanicen.db.DBHelper;
import com.argenes.android.sanicen.utils.NavigationManager;

public class Search2Activity extends AppCompatActivity {
    private DBHelper db;
    private String provinceCode;
    private String munCode;
    private String provinceName;
    private String munName;
    private String centerTypeName;
    private String centerTypeCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search2);

        provinceCode = NavigationManager.getData(this, "provinceCode");
        provinceName = NavigationManager.getData(this, "provinceName");
        munCode = NavigationManager.getData(this, "munCode");
        munName = NavigationManager.getData(this, "munName");
        centerTypeName = NavigationManager.getData(this, "centerTypeName");
        centerTypeCode = NavigationManager.getData(this, "centerTypeCode");

        db = new DBHelper(this);

        Cursor cursor = db.getSpecialityCursor();
        String[] from = new String[] {"_id", "name"};
        int[] to = new int[] {R.id.item_id, android.R.id.text1};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_multiple_choice, cursor, from, to, 0);
        final ListView LstOptions = (ListView)findViewById(R.id.LstOptions);

        LstOptions.setAdapter(adapter);
        LstOptions.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        Button btnNext = (Button) findViewById(R.id.btnNext);

        btnNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){


            long[] selectedSpecialities = LstOptions.getCheckedItemIds();

            NavigationManager.goToMapActivity(Search2Activity.this,
                    provinceCode, provinceName,
                    munCode, munName,
                    centerTypeCode, centerTypeName,
                    selectedSpecialities);

            };
        });

    }
}
