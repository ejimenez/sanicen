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


package com.argenes.android.sanicen.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.argenes.android.sanicen.activities.DetailCenterActivity;
import com.argenes.android.sanicen.activities.MapActivity;
import com.argenes.android.sanicen.activities.Search2Activity;
import com.argenes.android.sanicen.activities.SearchActivity;

public class NavigationManager {

    public static void goToSearchActivity(Activity activity)
    {

        Intent intent = new Intent(activity, SearchActivity.class);
        activity.startActivity(intent);
    }

    public static void goToSearch2Activity(Activity activity,
                                           String provinceCode,
                                           String provinceName,
                                           String munCode,
                                           String munName,
                                           String centerTypeCode,
                                           String centerTypeName)

    {

        Intent itemintent = new Intent(activity, Search2Activity.class);
        Bundle b = new Bundle();
        b.putString("provinceCode", provinceCode);
        b.putString("provinceName", provinceName);
        b.putString("munCode", munCode);
        b.putString("munName", munName);
        b.putString("centerTypeCode", centerTypeCode);
        b.putString("centerTypeName", centerTypeName);
        itemintent.putExtra("android.intent.extra.INTENT", b);
        activity.startActivity(itemintent);
    }

    public static void goToMapActivity(Activity activity,
                                       String provinceCode,
                                       String provinceName,
                                       String munCode,
                                       String munName,
                                       String centerTypeCode,
                                       String centerTypeName,
                                       long [] specialities)
    {
        Intent itemintent = new Intent(activity, MapActivity.class);
        Bundle b = new Bundle();
        b.putString("provinceCode", provinceCode);
        b.putString("provinceName", provinceName);
        b.putString("munCode", munCode);
        b.putString("munName", munName);
        b.putString("centerTypeCode", centerTypeCode);
        b.putString("centerTypeName", centerTypeName);
        b.putLongArray("specialities", specialities);
        itemintent.putExtra("android.intent.extra.INTENT", b);
        activity.startActivity(itemintent);
    }

    public static void gotToDetailCenterActivity(Activity activity,
                                                 String title,
                                                 String snippet)
    {
        Intent itemintent = new Intent(activity, DetailCenterActivity.class);
        Bundle b = new Bundle();
        b.putString("title", title);
        b.putString("snippet", snippet);
        itemintent.putExtra("android.intent.extra.INTENT", b);
        activity.startActivity(itemintent);
    }


    public static String getData(Activity activity, String name)
    {
        String data="";
        Intent startingIntent = activity.getIntent();

        if (startingIntent != null)
        {
            Bundle b = startingIntent.getBundleExtra("android.intent.extra.INTENT");
            if (b != null) {
                data = b.getString(name);
            }
        }

        return data;
    }

    public static long [] getDataArray(Activity activity, String name)
    {
        long [] data= null;
        Intent startingIntent = activity.getIntent();

        if (startingIntent != null)
        {
            Bundle b = startingIntent.getBundleExtra("android.intent.extra.INTENT");
            if (b != null) {
                data = b.getLongArray(name);
            }
        }

        return data;
    }

}
