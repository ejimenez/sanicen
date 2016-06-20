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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.argenes.android.sanicen.R;
import com.argenes.android.sanicen.db.DBHelper;
import com.argenes.android.sanicen.utils.NavigationManager;

import java.util.HashMap;

public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private DBHelper db;
    private Spinner sprCCAA;
    private Spinner sprProvinces;
    private Spinner sprMunicipalties;
    private Spinner sprCenterTypes;
    final String[] from = new String[] {"_id", "name"};
    final int[] to = new int[] {R.id.item_id, R.id.item_name };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        db = new DBHelper(this);
        sprCCAA = (Spinner) findViewById(R.id.ccaa);
        sprCCAA.setOnItemSelectedListener(this);

        sprProvinces = (Spinner) findViewById(R.id.provinces);
        sprProvinces.setOnItemSelectedListener(this);

        sprMunicipalties = (Spinner) findViewById(R.id.municipalties);
        sprMunicipalties.setOnItemSelectedListener(this);

        sprCenterTypes = (Spinner) findViewById(R.id.centers_types);

        addCCAA();
        addCenterTypes();

        Button btnNext = (Button) findViewById(R.id.btnNext);

        btnNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Cursor selectedPronvince = (Cursor) sprProvinces.getSelectedItem();
                String provinceCode = selectedPronvince.getString(selectedPronvince.getColumnIndex("_id"));
                String provinceName = selectedPronvince.getString(selectedPronvince.getColumnIndex("name"));

                Cursor selectedMuncipalty = (Cursor) sprMunicipalties.getSelectedItem();
                String munCode = selectedMuncipalty.getString(selectedMuncipalty.getColumnIndex("_id"));
                String munName = selectedMuncipalty.getString(selectedMuncipalty.getColumnIndex("name"));

                HashMap<String, String> selectedCenterType = (HashMap<String, String>) sprCenterTypes.getSelectedItem();
                String centerTypeCode = selectedCenterType.get("_id");
                String centerTypeName = selectedCenterType.get("name");

                if (centerTypeCode.equals(db.hoCode)) {
                    NavigationManager.goToSearch2Activity(SearchActivity.this,
                                                          provinceCode, provinceName,
                                                          munCode, munName,
                                                          centerTypeCode, centerTypeName);
                }
                else {
                    NavigationManager.goToMapActivity(SearchActivity.this,
                            provinceCode, provinceName,
                            munCode, munName,
                            centerTypeCode, centerTypeName, null);
                }
            };
        });

    }

    public void addCCAA(){
        Cursor cursor = db.getCCAACursor();
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.list_item, cursor, from, to, 0);
        sprCCAA.setAdapter(adapter);

    }

    public void addProvinces(String ccaaCode){
        Cursor cursor = db.getProvincesCursor(ccaaCode);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.list_item, cursor, from, to, 0);
        sprProvinces.setAdapter(adapter);

    }

    public void addMunicipalties(String provinceCode){

        Cursor cursor = db.getMunicipaltiesCursor(provinceCode);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.list_item, cursor, from, to, 0);
        sprMunicipalties.setAdapter(adapter);
    }

    public void addCenterTypes(){
        SimpleAdapter adapter = new SimpleAdapter(this, db.getCenterTypes(), R.layout.list_item, from, to);
        sprCenterTypes.setAdapter(adapter);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View vier, int pos, long id) {

        if (parent.getId() == R.id.ccaa) {
            addProvinces(String.valueOf(id));
        }
        else if (parent.getId() == R.id.provinces) {
            addMunicipalties(String.valueOf(id));

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub

    }


}
