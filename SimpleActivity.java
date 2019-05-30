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
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.os.PowerManager;
import android.webkit.WebView;
import android.widget.Toast;
import android.content.res.Configuration;
import java.util.Locale;

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
	
	//for exit on run
	private static final int DELAYED_MESSAGE = 1;
	private Handler handler;
	
	//for hide load scrn
	private static final int HIDE_LDR_MSG = 2;


	/** Вызывается  когда активность вперые создана. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mWebView = (WebView) findViewById(R.id.webView);
		// включаем поддержку JavaScript
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setScrollbarFadingEnabled(true);
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.loadUrl("file:///android_asset/www/index.html"); 
		//mWebView.loadUrl("javascript://alert(101);"); 
		AndroidUlib.APP = this;
		mWebView.addJavascriptInterface (this, "Qt");
		
		_mPhp = new PHPInterface(getApplicationContext());
		mWebView.addJavascriptInterface (_mPhp, "PHP");
		
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == DELAYED_MESSAGE) {
					SimpleActivity.this.finish();
				}
				if (msg.what == HIDE_LDR_MSG) {
					SimpleActivity.this.mWebView.loadUrl("javascript:window.hideLoadScrn();"); 
				}
				super.handleMessage(msg);
			}
		};
		Intent it = this.getIntent();
		int brigntness = it.getIntExtra("needChangeBrigntness", -1);
		if (brigntness != -1) {
			DisplayManager.setWindowBrightness(brigntness, getWindow());
			Message msg = handler.obtainMessage(DELAYED_MESSAGE);
			handler.sendMessageDelayed(msg, 1000);
		} else {
			DisplayManager.setWindowBrightness(brigntness, getWindow());
			Message msg = handler.obtainMessage(HIDE_LDR_MSG);
			handler.sendMessageDelayed(msg, 5*1000);
			stopDMService();
			startDMService();
		}
    }
	
	public void quit()
	{
		//Intent intent = new Intent(getBaseContext(), SimpleActivity.class);
		SimpleActivity.this.finish();
	}
	
	public boolean startDMService()
    {
		try {
			startService(new Intent(this, ServiceDM.class));
		} catch(Exception e) {
			this._lastErr = e.getMessage();
			return false;
		}
		return true;
	}
	
	public String getSystemLocale() {
		Configuration cnf = this.getResources().getConfiguration();
		//return cnf.locale.getDisplayLanguage(); "русский"! прикольно, но не нужно
		return cnf.locale.getLanguage();
	}
	
	public boolean stopDMService()
    {
		try {
			stopService(new Intent(this, ServiceDM.class));
		} catch(Exception e) {
			this._lastErr = e.getMessage();
			return false;
		}
		return true;
	}
	
	public void toast(String s)
	{
		try {
			Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
		} catch(Exception e) {
			this._lastErr = e.getMessage();
		}
	}
	
	/**
	 * Методы управления яркостью экрана
	*/
    public int getDisplayBrightness()
    {
		int n = DisplayManager.getDisplayBrightness(this);
		this._lastErr = DisplayManager._lastErr;
		return n;
	}
	public boolean setDisplayBrightness(int brightness)
    {
		try {
			boolean b = DisplayManager.setDisplayBrightness(brightness, this);
			this._lastErr = DisplayManager._lastErr;
			return b;
		} catch (Exception e) {
			this._lastErr = e.getMessage();
		}
		return false;
	}
	
	public int getDisplayMaxBrightness()
    { 
		return DisplayManager.getDisplayMaxBrightness();
	}
	
	public int getDisplayMinBrightness()
    { 
		return DisplayManager.getDisplayMinBrightness();
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
	
}
