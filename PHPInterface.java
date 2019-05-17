package land.learn.hw17;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.lang.StringBuffer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;

import android.content.Context;
//TODO file_* Смотри, как в camscan сделано, там лучше, потому что пишет на SD карту
public class PHPInterface {
	
	
	private Context _ctx;
	public static final int FILE_APPEND = 1;
	/* Сюда записывается число замен функцией str_replace если передан аргумент getCount */
	public static int STR_REPLACE_COUNT = 0;


	public String file_put_contents(String file, String data)
	{
		return file_put_contents(file, data, 0);
	}

	public String file_put_contents(String file, String data, int isAppend)
	{
		try {
			int mode = 0;
			if (isAppend == FILE_APPEND) {
				mode = Context.MODE_APPEND;
			}
			OutputStream oStream = _ctx.openFileOutput(file, mode);
			OutputStreamWriter writer = new OutputStreamWriter(oStream);
			writer.write(data);
			writer.close();
			//oStream.close();
			return Integer.toString( data.length() );
		} catch (Exception e) {
			return "Could not read file " + file + " with error '" + e.getMessage() + "'";
		}
	}	
	
	public PHPInterface(Context ctx) {
		this._ctx = ctx;
	}
	
	public String file_get_contents(String file) {
		try {
			InputStream inStream = _ctx.openFileInput(file);
			InputStreamReader rd = new InputStreamReader(inStream);
			BufferedReader reader = new BufferedReader(rd);
			StringBuffer buffer = new StringBuffer();
			String str;
			while ( (str = reader.readLine()) != null ) {
				buffer.append(str + "\n");
			}
			inStream.close();
			return buffer.toString();
		} catch (Exception e) {
			return "Could not read file " + file + " with error '" + e.getMessage() + "'";
		}
	}
	public String date( String s, long timestamp ){
		Calendar c = Calendar.getInstance(  );
		long n = c.getTimeInMillis(  );
		c .setTimeInMillis( timestamp * 1000);
		s = _date( s, c  );
		c.setTimeInMillis( n);
		return s;
    }
    public String date( String s  ){
        Calendar c = Calendar.getInstance(  );
        return _date( s, c  );
    }
    public String _date( String s, Calendar c  ){
        String Y  = _zero(  c.get(Calendar.YEAR) );
        String m = _zero(  c.get(Calendar.MONTH ) + 1
        );
        String d = _zero(  c.get(Calendar.DATE  ) );
        String H = _zero( c.get(Calendar.HOUR_OF_DAY ) );
        String i = _zero( c.get(Calendar.MINUTE));
        String sec = _zero( c.get(Calendar.SECOND ));
        s  = s.replaceAll( "Y", Y );
        s = s.replaceAll( "m", m  );
        s = s.replaceAll( "d", d  );
        s = s.replaceAll( "H", H );
        s = s.replaceAll( "i", i  );
        s = s.replaceAll( "s", sec  );
        return s;
    }
    private String _zero( int n  ){
        String p = "";
        if( n < 10 ){
            p = "0";
        }
        return p + Integer.toString( n );
    }
    public String str_replace( String search, String replace, String subject){
        return subject.replaceAll(search, replace);
    }
    /**
     * @description Заменяет search в subject на replace. Если передан getCount = true записывает в 
     * STR_REPLACE_COUNT количество замен
    */
    public String str_replace( String search, String replace, String subject, boolean getCount){
		if (getCount) {
			int n = 0,
				offset = 0;
			offset = subject.indexOf(search, offset);
			while (offset != -1) {
				n++;
				offset = subject.indexOf(search, offset + 1);
			}
			PHPInterface.STR_REPLACE_COUNT = n;
		}
        return subject.replaceAll(search, replace);
    }
    public long intval( String s  ){
		return Integer.parseInt(s);
    }
    public boolean in_array( String[] a, String need ){
        int i;
        for (i = 0; i < a.length;  i++ ){
            if( a[i]  == need  ){
                return true ;
            }
        }
        return false;
     }
    public boolean in_array( int[] a, int need ){
        int i;
        for (i = 0; i < a.length;  i++ ){
            if( a[i]  == need ){
                return true ;
            }
        }
        return false;
     }
     public long time(){
       Calendar c = Calendar.getInstance();
       return Math.round(c.getTimeInMillis() / 1000);
     }
	 /**
      * @description Считает непустые (не равные null) элементы от нулевого до a.length
      * Как только найден первый null - длина массива считается найденной
     */
	 public long count(Object[] a) {
		 long sz = 0;
		 for (int i = a.length - 1; i > -1; i--) {
			 if (a[i] != null) {
				 return (i + 1);
			 }
		 }
		 return 0;
	 }
	 public long strlen(String s) {
		 return s.length();
	 }
	
}
