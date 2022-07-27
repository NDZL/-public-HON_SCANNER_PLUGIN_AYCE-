package com.ndzl.scannerplugin.ayce;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Set;


public class DataEditing extends BroadcastReceiver {

    public String prefix;

    public DataEditing() {
        prefix="HEX={d2500}";
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        SharedPreferences prefs = context.getSharedPreferences("SCANNER_PLUGIN_AYCE", MODE_PRIVATE );
        boolean isICDchecked = prefs.getBoolean( "ICD_ONOFF", false);
        String ICDvalue =  prefs.getString("ICD_VALUE", "100");
        boolean isGSchecked = prefs.getBoolean( "GS_ONOFF", false);
        String GSvalue= prefs.getString("GS_VALUE", "") ;
        boolean isRPL1checked = prefs.getBoolean( "RPL1_ONOFF", false) ;
        String etRPL1_A = prefs.getString("RPL1_A_VALUE", "")  ;
        String etRPL1_B = prefs.getString("RPL1_B_VALUE", "")  ;


        String scanResult = intent.getStringExtra("data");//Read the scan result from the Intent
        Bundle bundle = new Bundle();

        String CURRENT_INPUT_DATA = scanResult;
        //===========================================
        //WORKING ON GS FILTERING - IF REQUIRED
        if(isGSchecked){
         //clean GS
            final String GROUP_SEPARATOR = Character.toString( (char)29 ) ;

            String gsfilteredScannedData = CURRENT_INPUT_DATA
                    .replace(GROUP_SEPARATOR, GSvalue);
            CURRENT_INPUT_DATA = gsfilteredScannedData;
        }
        //===========================================
        //WORKING ON GENERAL STRING REPLACEMENT
        if(isRPL1checked){
            String replacedOneScannedData = CURRENT_INPUT_DATA;
            char[] chars = etRPL1_A.toCharArray();
            for (char ch : chars) {
                replacedOneScannedData = replacedOneScannedData.replace(String.valueOf(ch), etRPL1_B);
            }
            CURRENT_INPUT_DATA = replacedOneScannedData;
        }

        //=========================================
        //WORKING ON INTER CHARACTER DELAY, IF REQUIRED

        if(isICDchecked) {
            String delayedScannedData = "";
            String INTER_CHAR_DELAY = "{d"+ICDvalue+"}";
            StringBuilder sb_data = new StringBuilder();
            StringBuilder sb_delay = new StringBuilder();
            char[] chars = CURRENT_INPUT_DATA.toCharArray();
            for (char ch : chars) {
                sb_data.append(ch);
                sb_data.append(INTER_CHAR_DELAY);
                sb_delay.append(INTER_CHAR_DELAY + ";");
            }

            delayedScannedData = sb_data.toString();
            String delayString = sb_delay.toString();
            //Toast.makeText(context, "<"+delayedScannedData+">" , Toast.LENGTH_LONG).show();
            bundle.putString("delayStr", delayString);
            CURRENT_INPUT_DATA = delayedScannedData;
        } //output string for next stage is delayedScannedData
        //===========================================

        bundle.putString("data", CURRENT_INPUT_DATA);
        setResultExtras(bundle);
    }

}
