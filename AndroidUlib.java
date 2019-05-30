package land.learn.hw19;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

public class AndroidUlib {
	static public Activity APP = null;
	static public final String UNINITALIZE_APP_STR = "Need Initalize AndroidUlib.APP";
	/**
	 * @desc Читает строку из ресурса
	 * @param Activity act - экземпляр "активности" приложения
	 * @param int id       - идентификатор ресурса
	 * **/
	static public String readRawResource(Activity act, int id) {
    	AndroidUlib.APP = act;
    	return AndroidUlib.readRawResource(id);
    }
	/**
	 * @desc Читает строку из ресурса
	 * @param int id       - идентификатор ресурса
	 * **/
	static public String readRawResource(int id) {
		if ( AndroidUlib.APP == null ) {
			return AndroidUlib.UNINITALIZE_APP_STR;
		}
		InputStream is = AndroidUlib.APP.getResources().openRawResource(id);
    	try {
	    	byte abyte0[] = new byte[(int)is.available()];
	        is.read(abyte0);
	        is.close();
	        String s = new String(abyte0, "UTF-8");
	        return s;
    	} catch (IOException ioe) {
    		return ioe.getMessage();
    	}
    }
	static public String APP_ID = "DEFAULT";
	/**
	 * @desc Запись настройки
	 * @param Activity act - экземпляр "активности" приложения
	 * @param String key    - ключ
	 * @param String val    - значение
	 * @param String app_id - идентификатор приложения
	 * **/
	static public boolean saveStr(Activity act, String key, String val, String app_id) {
		AndroidUlib.APP_ID = app_id;
		AndroidUlib.APP = act;
		return AndroidUlib.saveStr(key, val);
	}
	/**
	 * @desc Запись настройки
	 * @param String key    - ключ
	 * @param String val    - значение
	 * **/
	static public boolean saveStr(String key, String val) {
		if (AndroidUlib.APP == null) {
			return false;
		}
		SharedPreferences settings = AndroidUlib.APP.getSharedPreferences(AndroidUlib.APP_ID, AndroidUlib.APP.MODE_PRIVATE);
		SharedPreferences.Editor prefEditor = settings.edit();
		prefEditor.putString(key, val);
		prefEditor.commit();
		return true;
	}
	
	/**
	 * @desc Чтение настройки
	 * @param Activity act - экземпляр "активности" приложения
	 * @param String key    - ключ
	 * @param String default_val    - значение по умолчанию
	 * @param String app_id - идентификатор приложения
	 * **/
	static public String loadStr(Activity act, String key, String default_val, String app_id) {
		AndroidUlib.APP_ID = app_id;
		AndroidUlib.APP = act;
		return AndroidUlib.loadStr(key, default_val);
	}
	/**
	 * @desc Чтение настройки
	 * @param String key         - ключ
	 * @param String default_val - значение
	 * **/
	static public String loadStr(String key, String default_val) {
		if (AndroidUlib.APP == null) {
			return AndroidUlib.UNINITALIZE_APP_STR;
		}
		SharedPreferences settings = AndroidUlib.APP.getSharedPreferences(AndroidUlib.APP_ID, AndroidUlib.APP.MODE_PRIVATE);
		return settings.getString(key, default_val);
	}
	/**
	 * @desc Чтение настройки
	 * @param Activity act - экземпляр "активности" приложения
	 * @param String key    - ключ
	 * @param String app_id - идентификатор приложения
	 * **/
	static public String loadStr(Activity act, String key, String app_id) {
		AndroidUlib.APP_ID = app_id;
		AndroidUlib.APP = act;
		return AndroidUlib.loadStr(key);
	}
	/**
	 * @desc Чтение настройки
	 * @param String key         - ключ
	 * **/
	static public String loadStr(String key) {
		if (AndroidUlib.APP == null) {
			return AndroidUlib.UNINITALIZE_APP_STR;
		}
		SharedPreferences settings = AndroidUlib.APP.getSharedPreferences(AndroidUlib.APP_ID, AndroidUlib.APP.MODE_PRIVATE);
		return settings.getString(key, null);
	}
	static public boolean MP3_PLAYING = false;
	/**
	 * @desc Воспроизвести звук из ресурса, использует флаг MP3_PLAYING для определения факта воспроизведения звука
	 * @param Activity act - экземпляр "активности" приложения
	 * @param int      id  - идентификатор ресурса
	 * @param boolean  force - проиграть, даже если текущее воспроизведение не але 
	 * **/
	static public void playMp3FromResource(Activity act, int id, boolean force) {
		AndroidUlib.APP = act;
		AndroidUlib.playMp3FromResource(id, force);
	}
	/**
	 * @desc Воспроизвести звук из ресурса, использует флаг MP3_PLAYING для определения факта воспроизведения звука
	 * @param Activity act - экземпляр "активности" приложения
	 * @param int      id  - идентификатор ресурса 
	 * **/
	static public void playMp3FromResource(Activity act, int id) {
		AndroidUlib.APP = act;
		AndroidUlib.playMp3FromResource(act, id, false);
	}
	/**
	 * @desc Воспроизвести звук из ресурса, использует флаг MP3_PLAYING для определения факта воспроизведения звука
	 * @param int      id  - идентификатор ресурса
	 * @param boolean  force - проиграть, даже если текущее воспроизведение не але
	 * **/
	static public void playMp3FromResource(int id, boolean force) {
		if (AndroidUlib.APP == null) {
			return;
		}
		if (force || AndroidUlib.MP3_PLAYING == false) {
			AndroidUlib.MP3_PLAYING = true;
			MediaPlayer mp = MediaPlayer.create(AndroidUlib.APP, id);
			mp.setLooping(false);
			mp.setOnPreparedListener(
				new MediaPlayer.OnPreparedListener() {
					public void onPrepared(MediaPlayer mp) {
						mp.start();	
					}
				}
			);
			mp.setOnCompletionListener(
				new MediaPlayer.OnCompletionListener() {
					public void onCompletion(MediaPlayer mp) {
						mp.release();
						AndroidUlib.MP3_PLAYING = false;
					}
				}
			);
		}
	}
	/**
	 * @desc Воспроизвести звук из ресурса, использует флаг MP3_PLAYING для определения факта воспроизведения звука
	 * @param int      id  - идентификатор ресурса
	 * **/
	static public void playMp3FromResource(int id) {
		AndroidUlib.playMp3FromResource(id, false);
	}
}
