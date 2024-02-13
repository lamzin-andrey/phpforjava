package land.learn.hw19;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.lang.StringBuffer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import android.os.Environment;

// Exec imports
import java.lang.Process;
import java.lang.Runtime;
import java.io.DataOutputStream;
import java.util.List;

import android.content.Context;
//TODO file_* Смотри, как в camscan сделано, там лучше, потому что пишет на SD карту
public class PHPInterface {
    
    private Context _ctx;
    public static final int FILE_APPEND = 1;
    public static final int FILE_APPEND_PHP = 8;
    public static final String LOCAL_FILES_DIR_A2 = "/data/data/land.learn.hw242/files";
    
    /* Сюда записывается число замен функцией str_replace если передан аргумент getCount */
    public static int STR_REPLACE_COUNT = 0;


    public String file_put_contents(String file, String data)
    {
        return file_put_contents(file, data, 0);
    }

    public String file_put_contents(String file, String data, int isAppend)
    {
        try {
            /*if (file.indexOf("/") != -1) {
                return writeSDFile(file, data, isAppend);
            }*/
            int mode = 0;
            if (isAppend == FILE_APPEND || isAppend == FILE_APPEND_PHP) {
                mode = Context.MODE_APPEND;
            }
            OutputStream oStream = _ctx.openFileOutput(file, mode);
            OutputStreamWriter writer = new OutputStreamWriter(oStream);
            writer.write(data);
            writer.close();
            //oStream.close();
            return Integer.toString( data.length() );
        } catch (Exception e) {
            return "Could not write file " + file + " with error '" + e.getMessage() + "'";
        }
    }
    
    /*public String writeSDFile(String file, String data, int isAppend) {
        Sting 
    }*/
    
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
    
    public boolean file_exists(String file) {
        try {
            InputStream inStream = _ctx.openFileInput(file);
            InputStreamReader rd = new InputStreamReader(inStream);
            BufferedReader reader = new BufferedReader(rd);
            try {
                inStream.close();
            } catch (Exception alle) {;}
            return true;
        } catch (FileNotFoundException e) {
            return false;
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
        int i;
        String q = "";
        for (i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '0'
             || s.charAt(i) == '1'
             || s.charAt(i) == '2'
             || s.charAt(i) == '3'
             || s.charAt(i) == '4'
             || s.charAt(i) == '5'
             || s.charAt(i) == '6'
             || s.charAt(i) == '7'
             || s.charAt(i) == '8'
             || s.charAt(i) == '9'
             || s.charAt(i) == '-'
            ) {
                q += s.charAt(i);
            }
        }
        if (q.length() == 0) {
            return 0;
        }
        //return Integer.parseInt(q);
        return Long.parseLong(q);
    }
    public boolean in_array(String need,  String[] a){
        int i;
        for (i = 0; i < a.length;  i++ ){
            if( a[i]  == need  ){
                return true ;
            }
        }
        return false;
     }
    public boolean in_array(int need, int[] a ){
        int i;
        for (i = 0; i < a.length;  i++ ){
            if( a[i]  == need ){
                return true ;
            }
        }
        return false;
     }
     public boolean in_array(long need, long[] a ){
        int i;
        for (i = 0; i < a.length;  i++ ){
            if( a[i]  == need ){
                return true;
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
     
     public String strval(int n) {
         return Integer.toString(n);
     }
     
    public String strval(long n) {
        return Long.toString(n);
    }
     
    public long hexdec(String s) {
        return Long.parseLong(s, 16);
    }
     
    public String dechex(int n) {
        return Integer.toHexString(n);
    }
     
    /**
     * @description Run process as root (for root devices)
    */
    public String suexec(String cmd) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd);
            os.flush();
            os.writeBytes("exit\n");
            os.flush();
            // InputStream in = process.getInputStream();
            // readStream(in);
            process.waitFor();
        } catch (InterruptedException e) {
            return e.getMessage();
        } catch (IOException e) {
            return e.getMessage();
        }
        
        return "";
    }
    
    public void writeSocket(String command) {
        file_put_contents("sock.sh", command);
    }
    
    public String exec(String command) {
        StringBuffer output = new StringBuffer();
        StringBuffer errs = new StringBuffer();
        Process p;
        try {
            File dir = Environment.getExternalStorageDirectory();
            this.writeSocket(command);
            
            String[] arr = new String[]{"PATH=/sbin:/system/bin"};
            p = Runtime.getRuntime().exec("sh " + LOCAL_FILES_DIR_A2 + "/sock.sh", null, dir);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader eReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            String line = "";
            String eLine = "";
            while ((line = reader.readLine())!= null || (eLine = eReader.readLine())!= null) {
                if (line != null) {
                    output.append(line + "\n");
                }
                if (eLine != null) {
                    errs.append(eLine + "\n");
                }
            }
            // "__LAND_TERMINAL_DVD__"

        } catch (Exception e) {
            return "__LAND_TERMINAL_DVD__" + "__LAND_TERMINAL_DVD__" + e.getMessage();
        }
        String response = command + "__LAND_TERMINAL_DVD__" + output.toString() + "__LAND_TERMINAL_DVD__" + errs.toString();
        return response;
    }
    
    
    
    public int shortval( String s  ){
        int i;
        String q = "";
        for (i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '0'
             || s.charAt(i) == '1'
             || s.charAt(i) == '2'
             || s.charAt(i) == '3'
             || s.charAt(i) == '4'
             || s.charAt(i) == '5'
             || s.charAt(i) == '6'
             || s.charAt(i) == '7'
             || s.charAt(i) == '8'
             || s.charAt(i) == '9'
             || s.charAt(i) == '-'
            ) {
                q += s.charAt(i);
            }
        }
        if (q.length() == 0) {
            return 0;
        }
        return Integer.parseInt(q);
    }
    
}
