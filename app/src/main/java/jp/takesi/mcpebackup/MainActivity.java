package jp.takesi.mcpebackup;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.nispok.snackbar.SnackbarManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private List<String> songList = new ArrayList<String>();
    private ListView lv;
    private File[] files;
    CustomTabsIntent customTabsIntent;
    CustomTabsIntent.Builder intentBuilder;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences pref = getSharedPreferences("version", MODE_PRIVATE);
        if (pref.contains("3.2")) {
        } else {
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("3.2", "version");
            editor.apply();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                // permissionが許可されていません
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                    // 許可ダイアログで今後表示しないにチェックされていない場合
                }
                // permissionを許可してほしい理由の表示など
                // 許可ダイアログの表示
                // MY_PERMISSIONS_REQUEST_READ_CONTACTSはアプリ内で独自定義したrequestCodeの値
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                //return;
            }
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.terms);//set title
        alertDialog.setMessage(R.string.termsof);//set content
        alertDialog.setIcon(R.mipmap.ic_launcher);//set icon
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                setState(PREFERENCE_BOOTED);
            }
        });

        if (PREFERENCE_INIT == getState()) {
            //初回起動時のみ表示する
            alertDialog.create();
            alertDialog.show();
        }
        AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(this);
        alertDialog1.setTitle(R.string.update_new);//set title
        alertDialog1.setMessage(R.string.termsof);//set content
        alertDialog1.setIcon(R.mipmap.ic_launcher);//set icon
        alertDialog1.setCancelable(false);
        alertDialog1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                setState(PREFERENCE_BOOTED);
            }
        });
        if (PREFERENCE_INIT == getState1()) {
            //初回起動時のみ表示する
            alertDialog1.create();
            alertDialog1.show();
        }

        new Thread(new Runnable(){
            @Override
            public void run() {
        //check_update();
            }
        }).start();


        String sdPath = Environment.getExternalStorageDirectory() + "/MCPEBackups/";
        files = new File(sdPath).listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile() && files[i].getName().endsWith(".apk")) {
                    songList.add(files[i].getName());
                }
            }

            lv = (ListView) findViewById(R.id.listview);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, songList);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ListView listView = (ListView) parent;
                    String item = (String) listView.getItemAtPosition(position);
                    try {
                        Toast.makeText(getApplicationContext(), R.string.show, Toast.LENGTH_LONG).show();
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Uri uri = Uri.fromParts("package", "com.mojang.minecraftpe", null);
                    Intent intent = new Intent(Intent.ACTION_DELETE, uri);
                    startActivity(intent);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String fileName = Environment.getExternalStorageDirectory() + "/MCPEBackups/" + item;
                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                    intent1.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
                    startActivity(intent1);
                }
            });

            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String type = bundle.getString("type");
                String app = bundle.getString("package");
                String app_name = bundle.getString("name");
                if (Objects.equals(type, "backup")) {
                    Log.d("[MCPEBackup]", "Starts MCPE APK Backup");
                    PackageManager pm = this.getPackageManager();
                    String versionName = "";
                    try {
                        SnackbarManager.show(
                                com.nispok.snackbar.Snackbar.with(this)
                                        .text(R.string.starts_backup));
                        PackageInfo packageInfo = pm.getPackageInfo(app, 0);
                        versionName = packageInfo.versionName;
                        ApplicationInfo appInfo = pm.getApplicationInfo(app, 0);
                        String appFile = appInfo.sourceDir;
                        File src = new File(appFile);

                        String PATH = Environment.getExternalStorageDirectory() + "/MCPEBackups/";
                        File file = new File(PATH);
                        file.mkdirs();

                        File outputFile = new File(file, app_name + versionName + ".apk");
                        FileChannel srcChannel = null;
                        FileChannel destChannel = null;
                        try {
                            srcChannel = new FileInputStream(src).getChannel();
                            destChannel = new FileOutputStream(outputFile).getChannel();
                            srcChannel.transferTo(0, srcChannel.size(), destChannel);
                        } catch (IOException e) {
                            e.printStackTrace();

                        } finally {
                            if (srcChannel != null) {
                                try {
                                    srcChannel.close();
                                } catch (IOException e) {
                                }
                            }
                            if (destChannel != null) {
                                try {
                                    destChannel.close();
                                } catch (IOException e) {
                                }
                            }
                        }

                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    SnackbarManager.show(
                            com.nispok.snackbar.Snackbar.with(this)
                                    .text(R.string.done_backup));
                    Log.d("[MCPEBackup]", "Done!");
                }
            }

            // Initialize intentBuilder
            intentBuilder = new CustomTabsIntent.Builder();

            // Set toolbar(tab) color of your chrome browser
            intentBuilder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));

            // Define entry and exit animation
            intentBuilder.setExitAnimations(this, R.anim.right_to_left_end, R.anim.left_to_right_end);
            intentBuilder.setStartAnimations(this, R.anim.left_to_right_start, R.anim.right_to_left_start);
            intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));

            // build it by setting up all
            customTabsIntent = intentBuilder.build();

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setAction("android.intent.category.LAUNCHER");
                    intent.setClassName("com.mojang.minecraftpe", "com.mojang.minecraftpe.MainActivity");
                    startActivity(intent);
                }
            });
        }
    }

    public static final int PREFERENCE_INIT = 0;
    public static final int PREFERENCE_BOOTED = 1;

    //データ保存
    private void setState(int state) {
        // SharedPreferences設定を保存
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putInt("InitState", state).commit();

        //ログ表示
        output( String.valueOf(state) );
    }

    //データ読み出し
    private int getState() {
        // 読み込み
        int state;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        state = sp.getInt("InitState", PREFERENCE_INIT);

        //ログ表示
        output( String.valueOf(state) );
        return state;
    }

    //データ保存
    private void setState1(int state) {
        // SharedPreferences設定を保存
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putInt("version", 11).commit();

        //ログ表示
        output( String.valueOf(state) );
    }

    //データ読み出し
    private int getState1() {
        // 読み込み
        int state;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        state = sp.getInt("version", 11);

        //ログ表示
        output( String.valueOf(state) );
        return state;
    }

    public String readInputStream(InputStream in) throws IOException, UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();
        String st = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        while((st = br.readLine()) != null) {
            sb.append(st);
        }
        try {
            in.close();
        }catch(Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private void output(String string){
        Log.d("[MCPEBackup]",string.toString());
    }

    public void check_update(){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                .permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        // URL設定
                        URL urlSt = new URL("https://drive.google.com/uc?id=0Bxp5wIuQibuSZkptOGl3NFA4RUk");
                        // HTTP接続開始
                        HttpsURLConnection c = (HttpsURLConnection) urlSt.openConnection();
                        c.setRequestMethod("GET");
                        c.connect();
                        InputStream in = c.getInputStream();
                        JSONObject jsonData = new JSONObject(readInputStream(in));
                        String latest_version = jsonData.getString("latest_version");
                        Log.d("[Important]", latest_version);
                        PackageManager pm = getApplicationContext().getPackageManager();
                        String versionName = "unknown";
                        try{
                            PackageInfo packageInfo = pm.getPackageInfo(getApplicationContext().getPackageName(), 0);
                            versionName = packageInfo.versionName;
                        }catch(PackageManager.NameNotFoundException e){
                            e.printStackTrace();
                        }

                        if (latest_version.equals(versionName)) {
                            String whats_new = jsonData.getString("whats_new");
                            Log.d("[Important]", whats_new);
                            //Thanks! http://slumbers99.blogspot.jp/2012/01/android.html
                            AlertDialog.Builder UpdateDialog = new AlertDialog.Builder(MainActivity.this);
                            //set title
                            UpdateDialog.setTitle("Your version is latest version.");
                            String latest = getResources().getString(R.string.latest);
                            String ur_version = getResources().getString(R.string.your_version);
                            String latest_ver = getResources().getString(R.string.latest_version);
                            UpdateDialog.setMessage( latest + "\n\n" + ur_version + " : " + versionName + "\n" + latest_ver + " : " + latest_version + "\n\nWHAT'S NEW? \n" + whats_new);//set content
                            UpdateDialog.setIcon(R.mipmap.ic_launcher);//set icon
                            UpdateDialog.setCancelable(true);
                            UpdateDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            UpdateDialog.create();
                            UpdateDialog.show();
                        }else{
                            String whats_new = jsonData.getString("whats_new");
                            Log.d("[Important]", whats_new);
                            //Thanks! http://slumbers99.blogspot.jp/2012/01/android.html
                            AlertDialog.Builder UpdateDialog = new AlertDialog.Builder(MainActivity.this);
                            //set title
                            UpdateDialog.setTitle("UPDATE!");
                            String update = getResources().getString(R.string.update);
                            String ur_version = getResources().getString(R.string.your_version);
                            String latest_ver = getResources().getString(R.string.latest_version);
                            UpdateDialog.setMessage(update + "\n\nWHAT'S NEW? \n" + whats_new + "\n\n" + ur_version + " : " + versionName + "\n" + latest_ver + " : " + latest_version);//set content
                            UpdateDialog.setIcon(R.mipmap.ic_launcher);//set icon
                            UpdateDialog.setCancelable(true);
                            UpdateDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            download();
                                        }
                                    });
                                }
                            });
                            UpdateDialog.create();
                            UpdateDialog.show();
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
    }

    public void download() {
        try {
            // URL設定
            URL url = new URL("https://drive.google.com/uc?id=0Bxp5wIuQibuSdU1BOWl0QUFVR0U");
            // HTTP接続開始
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.connect();

            // SDカードの設定
            String PATH = Environment.getExternalStorageDirectory() + "/MCPEBackups/tmp/";
            File file = new File(PATH);
            file.mkdirs();

            // テンポラリファイルの設定
            File outputFile = new File(file, "MCPEBackup.apk");
            FileOutputStream fos = new FileOutputStream(outputFile);
            // ダウンロード開始
            InputStream is = c.getInputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            is.close();
            //https://medium.com/@ali.muzaffar/what-is-android-os-fileuriexposedexception-and-what-you-can-do-about-it-70b9eb17c6d0
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            // Old Approach
            install.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/MCPEBackups/tmp/" + "MCPEBackup.apk")), "application/vnd.android.package-archive");
            // End Old approach
            // New Approach
            Uri apkURI = FileProvider.getUriForFile(getApplicationContext(),getApplicationContext().getPackageName() + ".provider", (new File(Environment.getExternalStorageDirectory() + "/MCPEBackups/tmp/" + "MCPEBackup.apk")));
            install.setDataAndType(apkURI, "application/vnd.android.package-archive");
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            // End New Approach
            startActivity(install);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_download) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            check_update();
                        }
                    });
                }
            }).start();
        }
        if (id == R.id.about) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(getString(R.string.app_name) + BuildConfig.VERSION_NAME );
            alertDialog.setIcon(R.mipmap.ic_launcher);
            alertDialog.setMessage(getString(R.string.author) + "\n\n" +
                    getString(R.string.email) + "\n\n" +
                    getString(R.string.github_link) +  "\n\n" +
                    getString(R.string.date) + "\n");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            return true;
        }
        if (id == R.id.developer) {
            startActivity(new Intent(this, DeveloperActivity.class));
            return true;
        }
        if (id == R.id.action_help) {
            customTabsIntent.launchUrl(this, Uri.parse("https://sites.google.com/view/mcpebackup/%E3%83%98%E3%83%AB%E3%83%97"));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
