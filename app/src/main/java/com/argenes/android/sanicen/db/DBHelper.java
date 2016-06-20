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


package com.argenes.android.sanicen.db;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.ArrayList;

import com.argenes.android.sanicen.*;

import android.util.Log;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static String DB_PATH = "/data/data/com.argenes.android.sanicen/databases/";

    private static String DB_NAME = "sanicen_bd.sqlite";

    private static int DATABASE_VERSION = 1;

    private SQLiteDatabase db;

    private final Context myContext;

    public final String apCode = "AP";
    public final String hoCode = "HO";
    public final String auCode = "AU";

    /**
     * Constructor: Toma referencia hacia el contexto de la aplicación que lo
     * invoca para poder acceder a los 'assets' y 'resources' de la aplicación.
     * Crea un objeto DBOpenHelper que nos permitirá controlar la apertura de la
     * base de datos.
     *
     * @param context
     */
    public DBHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.myContext = context;
        try {
            this.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
    }

    /**
     * Crea una base de datos vacía en el sistema y la reescribe con nuestro
     * fichero de base de datos.
     * */
    public void createDataBase() throws IOException
    {
        boolean dbExist = checkDataBase();

        if (dbExist) {
            Log.d("DBHelper","createDataBase() DB exists");
            // la base de datos existe y no hacemos nada.
        } else {
            // Llamando a este método se crea la base de datos vacía en la ruta
            // por defecto del sistema de nuestra aplicación
            // por lo que podremos sobreescribirla con
            // nuestra base de datos.
            this.getReadableDatabase();
            try {

                copyDataBase();

            } catch (IOException e) {
                throw new Error("Error copiando Base de Datos");
            }
        }

    }

    /**
     * Comprueba si la base de datos existe para evitar copiar siempre el
     * fichero cada vez que se abra la aplicación.
     *
     * @return true si existe, false si no existe
     */
    private boolean checkDataBase()
    {
        SQLiteDatabase checkDB = null;

        try {

            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READWRITE);

        } catch (SQLiteException e) {

            // si llegamos aqui es porque la base de datos no existe todavía.

        }
        if (checkDB != null) {

            checkDB.close();

        }
        return checkDB != null ? true : false;
    }

    /**
     * Copia nuestra base de datos desde la carpeta assets a la recién creada
     * base de datos en la carpeta de sistema, desde dónde podremos acceder a
     * ella. Esto se hace con bytestream.
     * */
    private void copyDataBase() throws IOException {

        // Abrimos el fichero de base de datos como entrada
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Ruta a la base de datos vacía recién creada
        String outFileName = DB_PATH + DB_NAME;

        // Abrimos la base de datos vacía como salida
        OutputStream myOutput = new FileOutputStream(outFileName);

        // Transferimos los bytes desde el fichero de entrada al de salida
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // Liberamos los streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    /**
     * Abre la base de datos
     **/
    public void openBD() throws SQLException{
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    /**
     * Cierra la base de datos
     **/
    public synchronized void closeDB() {
        if(db != null)
            db.close();

        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DBHelper","onCreate() Creating database");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * @return Devuelve un listado con las distintas provincias
     * */
    public ArrayList<HashMap<String, String>> getProvinces()
    {
        final String TABLE_NAME = "provincias";
        final String TABLE_KEY_ROWID = "IDPROV";
        final String TABLE_KEY_PROVINCE = "NOMBRE";

        ArrayList<HashMap<String, String>> lstProvinces = new ArrayList<HashMap<String, String>>();
        try{

            Cursor result = db.query(TABLE_NAME,
                                     new String[] {TABLE_KEY_ROWID,TABLE_KEY_PROVINCE},
                                     null, null, null, null, TABLE_KEY_PROVINCE);

            if(result.moveToFirst()){
                do{
                    HashMap<String, String> province = new HashMap<String, String>();
                    province.put("id", result.getString(0));
                    province.put("name", result.getString(1));
                    lstProvinces.add(province);
                }while(result.moveToNext());

                if(result!=null && !result.isClosed()){
                    result.close();
                }
            }

            this.closeDB();
        }catch (Exception e)
        {
            Log.e("ERROR", "ERROR IN CODE:"+e);
        }

        return lstProvinces;
    }

    public ArrayList<HashMap<String, String>> getCenterTypes()
    {
        ArrayList<HashMap<String, String>> lstTypes = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> APtype = new HashMap<String, String>();
        APtype.put("_id", apCode);
        APtype.put("name", "Atención primaria (AP)");
        lstTypes.add(APtype);

        HashMap<String, String> HOtype = new HashMap<String, String>();
        HOtype.put("_id", hoCode);
        HOtype.put("name", "Hospitales (HO)");
        lstTypes.add(HOtype);

        HashMap<String, String> AUtype = new HashMap<String, String>();
        AUtype.put("_id", auCode);
        AUtype.put("name", "Atención urgente extrahospitalaria (AU)");
        lstTypes.add(AUtype);


        return lstTypes;
    }

    public Cursor getCCAACursor() {
        final String TABLE_NAME = "ccaa";
        final String TABLE_KEY_ROWID = "ID";
        final String TABLE_KEY_CCAA = "NOMBRE";
        this.openBD();
        Cursor c = db.query(TABLE_NAME,
                new String[] {TABLE_KEY_ROWID + " _id",TABLE_KEY_CCAA + " name"},
                null, null, null, null, TABLE_KEY_CCAA);
        this.close();
        return c;
    }

    public Cursor getProvincesCursor(String ccaa) {
        final String TABLE_NAME = "provincias";
        final String TABLE_KEY_ROWID = "IDPROV";
        final String TABLE_KEY_PROVINCE = "NOMBRE";
        final String TABLE_KEY_CCAA = "IDCCAA";
        this.openBD();
        Cursor c = db.query(TABLE_NAME,
                            new String[] {TABLE_KEY_ROWID + " _id",TABLE_KEY_PROVINCE + " name"},
                            TABLE_KEY_CCAA + " = '" + ccaa +"'",
                            null, null, null, TABLE_KEY_PROVINCE);
        this.close();
        return c;
    }

    public Cursor getMunicipaltiesCursor(String province) {
        final String TABLE_NAME = "municipios";
        final String TABLE_KEY_ROWID = "CODMU";
        final String TABLE_KEY_MUNICIPALTY = "MUNICIPIO";
        final String TABLE_KEY_PROVINCE = "CODPROV";
        this.openBD();
        Cursor c = db.query(TABLE_NAME,
                            new String[] {TABLE_KEY_ROWID + " _id",TABLE_KEY_MUNICIPALTY + " name"},
                            TABLE_KEY_PROVINCE + " = '" + province +"'",
                            null, null, null, TABLE_KEY_PROVINCE);
        this.close();
        return c;
    }

    public Cursor getAPCentersCursor(String muncipalty) {

        String query = "select cen.idcentro _id, cen.nombre name, cen.direccion address, cen.cp postalCode, " +
                               "cen.telefono phone, cen.lat latitude, cen.long longitude, " +
                               "area.nombre area_name, zona.nombre zone_name, tipo.tipocentro subtype, " +
                               "cen.d_gestion dependence, gest.t_nombre gestion, cen.esdocente teaching, " +
                               "cen.localidad village " +
                       "from siap_centros cen, siap_areasalud_cd area, siap_tipocentro tipo,  "+
                             "siap_zonabasica zona, siap_mod_gestiones gest " +
                       "where codmu = ? and " +
                              "cen.idarea = area.idarea and " +
                              "cen.idzonabasica = zona.idzona and " +
                              "zona.idarea = area.idarea and " +
                              "cen.tipocentro = tipo.id and " +
                              "cen.c_gestion_id = gest.c_gestion_id ";
        this.openBD();
        Cursor c = db.rawQuery(query, new String[]{muncipalty});
        this.close();

        return c;
    }

    public Cursor getHOCentersCursor(String muncipalty, long [] specialities) {

        String query = "select cen.CODID _id, cen.nombre name, cen.direccion address, cen.CODPOSTAL postalCode, " +
                            "cen.telefono phone, cen.telefono2 phone2, cen.telefax fax, cen.email email, " +
                            "cen.lat latitude, cen.long longitude, " +
                            "cen.ncamas beds, cen.concierto concert, cen.acredocent teaching, " +
                            "fin.desfi speciality, pat.desde patDependence, fun.desfu funDependence, " +
                            "dot.tac tac, dot.rm rm, dot.gam gam, dot.hem hem, dot.asd asd, dot.lit lit,  " +
                            "dot.bco bco, dot.ali ali, dot.spect spect, dot.pet pet, dot.mamos mamos, " +
                            "dot.do do, dot.dial dial  " +
                        "from ch_catalogo cen, " +
                             "ch_finalidad fin, ch_patrimonial pat, ch_funcional fun, " +
                             "ch_dotacion dot " +
                        "where codmu = ? and " +
                            "cen.codfi = fin.codfi and " +
                            "cen.codfu = fun.codfu and " +
                            "cen.codpat = pat.codpat and " +
                            "cen.codid = dot.codid";

        if (specialities != null) {
            String strSpecialities = null;
            for( int i = 0 ; i < specialities.length; i++ ) {
                if (i == 0) {
                    strSpecialities = "(";
                }
                strSpecialities += "'" + specialities[i] + "'";
                if (i != (specialities.length - 1)){
                    strSpecialities += ",";
                }
                else {
                    strSpecialities += ")";
                }
            }
            if (strSpecialities != null) {
                query += " and cen.codfi in " + strSpecialities;
            }
        }

        this.openBD();

        Cursor c = db.rawQuery(query, new String[]{String.format("%06d", Integer.parseInt(muncipalty))});
        this.close();


        return c;
    }

    public Cursor getAUCentersCursor(String muncipalty) {

        String query = "select cen.disp_extra_id _id, cen.t_ubicacion name, cen.t_direccion address, " +
                            "cen.c_codpostal postalCode, cen.t_localidad village, cen.t_nombre subtype, " +
                            "cen.c_telefono phone, cen.t_horario timeTable, " +
                            "cen.lat latitude, cen.long longitude " +
                        "from siap_disp_extras cen  "+
                        "where cen.c_codmu_id = ?";

        this.openBD();

        Cursor c = db.rawQuery(query, new String[]{muncipalty});
        this.close();

        return c;
    }

    public Cursor getCenterTypeCursor (String centerTypeCode, String munCode, long [] specialities) {
        Cursor cursor = null;
        if (centerTypeCode.equals(apCode)) {
            cursor =  getAPCentersCursor(munCode);
        } else if (centerTypeCode.equals(hoCode)) {
            cursor = getHOCentersCursor(munCode, specialities);
        } else if (centerTypeCode.equals(auCode)){
            cursor = getAUCentersCursor(munCode);
        }
        return cursor;

    }

    public Cursor getSpecialityCursor () {
        final String TABLE_NAME = "ch_finalidad";
        final String TABLE_KEY_ROWID = "CODFI";
        final String TABLE_KEY_SPECIALITY = "DESFI";

        this.openBD();
        Cursor c = db.query(TABLE_NAME,
                new String[] {TABLE_KEY_ROWID + " _id", TABLE_KEY_SPECIALITY + " name"},
                null, null, null, null, TABLE_KEY_SPECIALITY);
        this.close();
        return c;

    }

    public String getCenterInfo(Cursor cursor,
                                String provinceName,
                                String munName,
                                String centerTypeCode,
                                String centerTypeName) {
        String body = "";
        String address = cursor.getString(cursor.getColumnIndex("address"));
        String postalCode = cursor.getString(cursor.getColumnIndex("postalCode"));
        String phone = cursor.getString(cursor.getColumnIndex("phone"));

        // TODO: move html styles

        body += "<br><b>Tipo de centro: </b>" + centerTypeName;

        if(address != null){
            body += "<br><b>Dirección: </b>" + address;
        }

        if(postalCode != null){
            body += ("<br><b>Código Postal: </b>" + postalCode);
        }

        if(provinceName != null){
            body += "<br><b>Provincia: </b>" + provinceName;
        }

        if(munName != null){
            body += "<br><b>Municipio: </b>" + munName;
        }


        // Extra info
        String village = null;
        String subtype = null;

        if (centerTypeCode.equals(apCode)) {
            village = cursor.getString(cursor.getColumnIndex("village"));
            String zona_name = cursor.getString(cursor.getColumnIndex("zone_name"));
            String area_name = cursor.getString(cursor.getColumnIndex("area_name"));
            String dependence = cursor.getString(cursor.getColumnIndex("dependence"));
            String gestion = cursor.getString(cursor.getColumnIndex("gestion"));
            subtype = cursor.getString(cursor.getColumnIndex("subtype"));
            String teaching = cursor.getString(cursor.getColumnIndex("teaching"));

            if (village != null) {
                body += "<br><b>Localidad: </b>" + village;
            }

            if (zona_name != null) {
                body += "<br><b>Zona básica: </b>" + zona_name;
            }

            if (area_name != null) {
                body += "<br><b>Área de salud: </b>" + area_name;
            }

            if (dependence != null) {
                body += "<br><b>Dependencia de gestión: </b>" + dependence;
            }

            if (gestion != null) {
                body += "<br><b>Modalidad de gestión: </b>" + gestion;
            }

            if (subtype != null) {
                body += "<br><b>Subtipo de centro: </b>" + subtype;
            }

            if (teaching != null) {
                body += "<br><b>Acreditación docente: </b>" + teaching;
            }

            if(phone != null){
                body += "<br><b>Teléfono:</b> <a href=\"tel:"+phone+"\">"+phone+"</a>";
            }
        }
        else if (centerTypeCode.equals(auCode)){

            String timeTable = cursor.getString(cursor.getColumnIndex("timeTable"));
            village = cursor.getString(cursor.getColumnIndex("village"));
            subtype = cursor.getString(cursor.getColumnIndex("subtype"));


            if(village != null){
                body += "<br><b>Localidad: </b>" + village;
            }

            if(subtype != null){
                body += "<br><b>Subtipo de centro: </b>" + subtype;
            }

            if(timeTable != null){
                body += "<br><b>Horario: </b>" + timeTable;
            }

            if(phone != null){
                body += "<br><b>Teléfono:</b> <a href=\"tel:"+phone+"\">"+phone+"</a>";
            }

        }
        else if (centerTypeCode.equals(hoCode)){
            String phone2 = cursor.getString(cursor.getColumnIndex("phone2"));
            String fax = cursor.getString(cursor.getColumnIndex("fax"));
            String email = cursor.getString(cursor.getColumnIndex("email"));
            String beds = cursor.getString(cursor.getColumnIndex("beds"));
            String concert = cursor.getString(cursor.getColumnIndex("concert"));
            String teaching = cursor.getString(cursor.getColumnIndex("teaching"));
            String speciality = cursor.getString(cursor.getColumnIndex("speciality"));
            String patDependence = cursor.getString(cursor.getColumnIndex("patDependence"));
            String funDependence = cursor.getString(cursor.getColumnIndex("funDependence"));

            String tac = cursor.getString(cursor.getColumnIndex("tac"));
            String rm = cursor.getString(cursor.getColumnIndex("rm"));
            String gam = cursor.getString(cursor.getColumnIndex("gam"));
            String hem = cursor.getString(cursor.getColumnIndex("hem"));
            String asd = cursor.getString(cursor.getColumnIndex("asd"));
            String lit = cursor.getString(cursor.getColumnIndex("lit"));
            String bco = cursor.getString(cursor.getColumnIndex("bco"));
            String ali = cursor.getString(cursor.getColumnIndex("ali"));
            String spect = cursor.getString(cursor.getColumnIndex("spect"));
            String pet = cursor.getString(cursor.getColumnIndex("pet"));
            String mamos = cursor.getString(cursor.getColumnIndex("mamos"));
            String ddo = cursor.getString(cursor.getColumnIndex("do"));
            String dial = cursor.getString(cursor.getColumnIndex("dial"));




            if(phone2 != null && !phone2.equals("")){
                body += "<br><b>Teléfono2:</b> <a href=\"tel:"+phone2+"\">"+ phone2 +"</a>";
            }

            if(fax != null){
                body += "<br><b>Fax: </b>" + fax;
            }

            if(email != null && !email.equals("")){
                body += "<br><b>Email: </b>" + email;
            }
            if(beds != null && !beds.equals("0")){
                body += "<br><b>Número de camas: </b>" + beds;
            }

            if(concert != null){
                body += "<br><b>Concierto: </b>" + concert;
            }

            if(teaching != null){
                body += "<br><b>Acreditación docente: </b>" + teaching;
            }
            if(speciality != null){
                body += "<br><b>Finalidad asistencial: </b>" + speciality;
            }

            if(patDependence != null){
                body += "<br><b>Dependencia patrimonial: </b>" + patDependence;
            }

            if(funDependence != null){
                body += "<br><b>Dependencia funcional: </b>" + funDependence;
            }

            if(phone != null){
                body += "<br><b>Teléfono:</b> <a href=\"tel:"+phone+"\">"+phone+"</a>";
            }

            body += "<br><br><b>Equipos de alta tecnología: </b>";

            if(tac != null && !tac.equals("0")){
                body += "<br><b>Tomografía Axial Computerizada: </b>" + tac;
            }

            if(rm != null && !rm.equals("0")){
                body += "<br><b>Resonancia Magnética: </b>" + rm;
            }

            if(gam != null && !gam.equals("0")){
                body += "<br><b>Gammacámara: </b>" + gam;
            }

            if(hem != null && !hem.equals("0")){
                body += "<br><b>Sala de Hemodinámica: </b>" + hem;
            }

            if(asd != null && !asd.equals("0")){
                body += "<br><b>Angiografía por Sustracción Digital: </b>" + asd;
            }

            if(lit != null && !lit.equals("0")){
                body += "<br><b>Litotricia Extracorporea por Ondas de Choque: </b>" + lit;
            }

            if(bco != null && !bco.equals("0")){
                body += "<br><b>Bomba de Cobalto: </b>" + bco;
            }

            if(ali != null && !ali.equals("0")){
                body += "<br><b>Acelerador de Partículas: </b>" + ali;
            }

            if(spect != null && !spect.equals("0")){
                body += "<br><b>Tomografía por emisión de fotones: </b>" + spect;
            }

            if(pet != null && !pet.equals("0")){
                body += "<br><b>Tomografía por emisión de positrones: </b>" + pet;
            }

            if(mamos != null && !mamos.equals("0")){
                body += "<br><b>Mamógrafo: </b>" + mamos;
            }

            if(ddo != null && !ddo.equals("0")){
                body += "<br><b>Densitómetros Óseos: </b>" + ddo;
            }

            if(dial != null && !dial.equals("0")){
                body += "<br><b>Equipos de Hemodiálisis: </b>" + dial;
            }

        }


        return body;

    }





}