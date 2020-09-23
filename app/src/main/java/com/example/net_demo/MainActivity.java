package com.example.net_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.net.SocketFactory;

import okhttp3.Dns;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;

import static android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET;

public class MainActivity extends AppCompatActivity {

    private TextView one;
    private TextView two;

    private SimpleDateFormat simpleDateFormat;

    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        one = findViewById(R.id.one);
        two = findViewById(R.id.two);

        client = new OkHttpClient.Builder().build();

        simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

        this.test();
        this.test2();
    }

    private void test(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        builder.addCapability(NET_CAPABILITY_INTERNET);
        //强制使用蜂窝数据网络
        builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);
        NetworkRequest build = builder.build();
        connectivityManager.requestNetwork(build,new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(@NonNull final Network network) {
                super.onAvailable(network);
                /*try {
                    URL url = new URL("https://www.baidu.com");
                    HttpURLConnection connection = (HttpURLConnection)network.openConnection(url);
                    connection.connect();
                    Log.i("connectxxxxxs:",connection.getResponseMessage()+"---"+simpleDateFormat.format(new Date(System.currentTimeMillis())));

                    one.setText("url:https://www.baidu.com\n"+connection.getResponseMessage()+"---"+simpleDateFormat.format(new Date(System.currentTimeMillis())));
                } catch (Exception e) {
                    Log.e("e",e.toString());
                }*/
                super.onAvailable(network);

                SocketFactory socketFactory = network.getSocketFactory();
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .socketFactory(socketFactory)
                        .dns(new Dns() {
                            @Override
                            public List<InetAddress> lookup(@NonNull String hostname) throws UnknownHostException {
                                return Arrays.asList(network.getAllByName(hostname));
                            }
                        })
                        .build();

                Request request = new Request.Builder()
                        .url("https://www.baidu.com")
                        .build();
//                Response response = okHttpClient.newCall(request).execute()

                try (Response response = okHttpClient.newCall(request).execute()) {
                    String rst = response.body().string();
                    Log.i("okhttp--->", rst);

                    one.setText("url:https://www.baidu.com\n"+simpleDateFormat.format(new Date(System.currentTimeMillis()))+"---"+rst);

                }catch (Exception e){
                    Log.e("a","b",e);
                }
            }
        });
    }

    public void test2() {

        new Thread(new Runnable(){
            @Override
            public void run() {

                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .build();

                Request request = new Request.Builder()
                        .url("https://xwehr.haier.net/loginNew?target=http://xwehr.haier.net/indexNew")
                        .build();
//                Response response = okHttpClient.newCall(request).execute()

                try (Response response = okHttpClient.newCall(request).execute()) {
                    String rst = response.body().string();
                    Log.i("okhttp--->", rst);

                    two.setText("url:https://xwehr.haier.net/loginNew?target=http://xwehr.haier.net/indexNew\n"+simpleDateFormat.format(new Date(System.currentTimeMillis()))+"---"+rst);

                }catch (Exception e){
                    Log.e("a","b",e);
                }
                /*URL url = null;
                try {
                    url = new URL("https://xwehr.haier.net/indexNew");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.connect();
                    Log.i("connectxxxxxs 内网:",conn.getResponseMessage()+"---"+simpleDateFormat.format(new Date(System.currentTimeMillis())));
                    two.setText("url:https://xwehr.haier.net/indexNew\n"+conn.getResponseMessage()+"---"+simpleDateFormat.format(new Date(System.currentTimeMillis())));
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
        }).start();
    }
}