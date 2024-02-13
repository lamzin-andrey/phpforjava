package land.learn.hw19;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.widget.LinearLayout;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.util.Log;
import android.os.PowerManager;
import android.webkit.WebView;
import android.widget.Toast;
import android.content.res.Configuration;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import java.util.Locale;
import android.provider.Settings;
import android.os.Environment;

//for launch service
import android.content.Intent;

//for exit on run
import android.os.Message;
import android.os.Handler;

public class SimpleActivity extends Activity{
    private WebView mWebView;
    private PHPInterface _mPhp;
    static private final String TAG = "WhistleApp";
    WakeLock wakeLock;
    PowerManager pm;
    boolean wake_lock_on = false;;
    static public TextView _label; //будем показывать, сколько секунд прошло
    private int _limit; //через сколько секунд воспроизводить звук
    private int _current_limit; //
    private CountDownTimer _timer = null;
    private CountDownTimer _animate_timer = null;
    private int _animate_frame = 1;
    private int animate_pause = -1;
    private boolean play_sound = false;
    private String _lastErr = "";
    
    //for hide load scrn
    private static final int HIDE_LDR_MSG = 2;


    /** Вызывается  когда активность вперые создана. 
     * Здесь и далее под "Спящим режимом" понимается ситуация, когда у смартфона в течении нескольких часов отключен экран.
    */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mWebView = (WebView) findViewById(R.id.webView);
        // включаем поддержку JavaScript
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setScrollbarFadingEnabled(true);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setBackgroundColor(Color.parseColor("#000000"));
        
        //Может, поможет в итоге
        //initLocker();
        
        
        mWebView.loadUrl("file:///android_asset/www/index.html"); 
        //mWebView.loadUrl("javascript://alert(101);"); 
        AndroidUlib.APP = this;
        mWebView.addJavascriptInterface (this, "Droid");
        
        _mPhp = new PHPInterface(getApplicationContext());
        mWebView.addJavascriptInterface (_mPhp, "PHP");
        
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == HIDE_LDR_MSG) {
                    SimpleActivity.this.mWebView.loadUrl("javascript:window.hideLoadScrn();"); 
                }
                super.handleMessage(msg);
            }
        };
        _srvlog("SA: start from user");
        //DisplayManager.setWindowBrightness(brightness, getWindow()); //Это зачем? 
        Message msg = handler.obtainMessage(HIDE_LDR_MSG);
        handler.sendMessageDelayed(msg, 1*1000);
    }
    
    public boolean isGPRSConnected()
    {
        return GPRSManager.isGPRSConnected(this);
    }
    
    public void setMobileDataEnabled(boolean v)
    {
        GPRSManager.setMobileDataEnabled(this, v);
    }
    
    public String showBrightnessSettingsScreen()
    {
        Intent intent = new Intent(Settings.ACTION_DISPLAY_SETTINGS); 
        if (
                intent.resolveActivity(
                    getPackageManager()
                ) != null
        ) { 
            startActivity(intent);
            return "ok";
        }
        
        return "fail resolve activity";
    }
    
    public String showWirelessSettingsScreen()
    {
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS); 
        if (
                intent.resolveActivity(
                    getPackageManager()
                ) != null
        ) { 
            startActivity(intent);
            return "ok";
        }
        
        return "fail resolve activity";
    }
     
    public String showQuickLaunchSettingsScreen()
    {
        Intent intent = new Intent(Settings.ACTION_QUICK_LAUNCH_SETTINGS);
        if (
                intent.resolveActivity(
                    getPackageManager()
                ) != null
        ) { 
            startActivity(intent);
            return "ok";
        }
        
        return "fail resolve activity";
    }
    
    public void quit()
    {
        //Intent intent = new Intent(getBaseContext(), SimpleActivity.class);
        SimpleActivity.this.finish();
    }
    
    public String getSystemLocale() {
        Configuration cnf = this.getResources().getConfiguration();
        //return cnf.locale.getDisplayLanguage(); "русский"! прикольно, но не нужно
        return cnf.locale.getLanguage();
    }
    
    public String getVersion() {
        try {
            PackageInfo info = getApplicationContext().getPackageManager().getPackageInfo( getPackageName(), 0 );
            return "{\"versionName\":\"" + info.versionName + "\",\"versionCode\":\"" + info.versionCode + "\"}";
        } catch (Exception exc) {
            _lastErr = exc.getMessage();
        }
        return "{\"versionName\":\"Fail get version\", \"versionCode\":\"Fail get version\"}";
    }
    
    
    public String getExternalStorage() {
        return Environment.getExternalStorageDirectory().getPath();
    }
    
    
    public void toast(String s)
    {
        try {
            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        } catch(Exception e) {
            this._lastErr = e.getMessage();
        }
    }
    
    
    public void setStr(String s) {
        AndroidUlib.saveStr("testn1", s);
    }
    
    public String getStr() {
        return AndroidUlib.loadStr("testn1");
    }
    public String getLastErr() {
        return this._lastErr;
    }
   
    /**
     * @desc Перезапуск таймера
     * **/
    @Override
    public void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
        
    }
    /**
     * @desc Перезапуск таймера
     * **/
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        screenRelease();
    }
    /**
     * @desc Остановка приложения
     * **/
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "on Stop");
        screenRelease();        
    }
    /**
     * @desc Десткрутор?
     * **/
    @Override
    public void onDestroy() {
        super.onDestroy();
        screenRelease();
    }
    /**
     * @desc Сделать так, чтобы экран не гас пока запущенно приложение
     * **/
    private void setScreenBrige() {
        screenRelease();
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE, TAG);
        wake_lock_on = true;
        Log.i(TAG, "try acquire");
        wakeLock.acquire();
    }
    /**
     * @desc Сделать так, чтобы экран не гас пока запущенно приложение
     * **/
    private void screenRelease() {
        if (wake_lock_on == false) {
            return;
        }
        if (wakeLock != null) {
            Log.i(TAG, "try release");
            wakeLock.release();
            wake_lock_on = false;
        }
    }
    /**
     * @desc Инициализую powerManager
     * **/
    private void initLocker()  {
        pm = (PowerManager) getSystemService(POWER_SERVICE);
        Log.i(TAG, "was init");
    }
    
    public void setLastErr(String s) {
        this._lastErr = s;
    }
    /**
     * @description Записывает сообщения в файл devlog при включенной настройке
    **/
    private  void _devlog(String s) {
        if (_mPhp == null) {
            _mPhp = new PHPInterface(this);
        }
        long dbgDevlogOn = _mPhp.intval( _mPhp.file_get_contents("dbgDevlogOn") );
        if (dbgDevlogOn == 1) {
            _mPhp.file_put_contents("devlog", s + "\n", PHPInterface.FILE_APPEND);
        }
    }
    /**
     * @description Записывает сообщения в файл srvlog
    **/
    private  void _srvlog(String s) {
        if (_mPhp == null) {
            _mPhp = new PHPInterface(this);
        }
        long dbgSrvlogOn = _mPhp.intval( _mPhp.file_get_contents("dbgSrvlogOn") );
        
        if (dbgSrvlogOn == 1) {
            String wrResult = _mPhp.file_put_contents("srvlog", _mPhp.date("Y-m-d H:i:s") + " " + s + "\n===\n", PHPInterface.FILE_APPEND);
            if (wrResult.indexOf("Could not") == 0) {
                _errlog(wrResult);
            }
        }
    }
    /**
     * @description Записывает сообщения в файл errlog
    **/
    private  void _errlog(String s) {
        if (_mPhp == null) {
            _mPhp = new PHPInterface(this);
        }
        long dbgErrlogOn = _mPhp.intval( _mPhp.file_get_contents("dbgErrlogOn") );
        if (dbgErrlogOn == 1) {
            _mPhp.file_put_contents("errlog", _mPhp.date("Y-m-d H:i:s") + " " + s + "\n===\n", PHPInterface.FILE_APPEND);
        }
    }
}
