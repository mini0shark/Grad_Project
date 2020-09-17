package com.capston.recipe.Utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.capston.recipe.R;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ServerAddress {
    public static String serverAddress="127.0.0.1";
    Context context;
    public ServerAddress(Context context){
        this.context = context;
        try{
            InputStream in = context.getResources().openRawResource(R.raw.server_address);
            if(in != null){
                InputStreamReader stream = new InputStreamReader(in, "utf-8");
                BufferedReader buffer = new BufferedReader(stream);
                StringBuilder sb =new StringBuilder("");
                String read;
                while( (read=buffer.readLine())!=null){
                    sb.append(read);
                }
                in.close();
                serverAddress = new String(sb);
                serverAddress = "http://"+ serverAddress;
            }
        }catch(IOException e){
            e.getStackTrace();
        }finally {
            Log.e("url test", serverAddress);
        }
    }
    public String getServerAddress(){
        return serverAddress;
    }

    @NonNull
    @Override
    public String toString() {
        return serverAddress;
    }
}
