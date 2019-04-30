package com.example.mendez.trackerservice.config;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.List;

public class Constantes {

    private static final String IP ="http://taxi.xorxio.com/phptracker/";
    public static final String IN_US=IP+"insertar_usuario.php";
    public static final String OBT_US =IP+"obtener_usuario.php";
    public static final String UP_US =IP+"actualizar_usuario.php";

    public static final String UP_TF=IP+"login.php";

    public static final String UP_CU=IP+"update_cue.php";

    public static final String UP_VI_RE=IP+"actualizar_viaje.php";

    public static final String ENV_NOT=IP+"enviarnot.php";

    public static final String VI_GET=IP+"obtener_viajes_usu.php";

    public static  final String IMG_US=IP+"users";
    public static  final String IMG_AU=IP+"autos";
    public static final String DIR=IP+"get_direc.php";


    public static final String PREFS_NAME="MyPrefsFile";

    public static List<Intent> POWERMANAGER_INTENTS = Arrays.asList(
            new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.entry.FunctionActivity")).setData(android.net.Uri.parse("mobilemanager://function/entry/AutoStart"))
    );


    public static void termi(Context context){
        SharedPreferences settings = context.getSharedPreferences(Constantes.PREFS_NAME, 0);
        SharedPreferences.Editor editor;
        editor=settings.edit();
        editor.putString("lato","error");
        editor.putString("lngo","error");
        editor.putString("latd","error");
        editor.putString("lngd","error");
        editor.putString("idivi","error");
        editor.putString("tkfcm","error");
        editor.putString("tus","error");
        editor.putString("pre","error");
        editor.putString("tus","error");
        editor.putString("nomcli","error");
        editor.putString("numcli","error");
        editor.apply();
    }
}
