package se.roland.util;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import static se.roland.util.Checker.checkdigit;

/**
 * Provide general purpose methods for handling OpenCV-JavaFX data conversion.
 * Moreover, expose some "low level" methods for matching few JavaFX behavior.
 *
 * @author <a href="mailto:luigi.derussis@polito.it">Luigi De Russis</a>
 * @author <a href="http://max-z.de">Maximilian Zuleger</a>
 * @version 1.0 (2016-09-17)
 * @since 1.0
 */
public final class Utils {
    public static ArrayList processPassportField(String input, int seriesLength, boolean ignoreDigits){
        var res = new ArrayList<>();
        StringBuilder sb_series = new StringBuilder();
        StringBuilder sb_number = new StringBuilder();
        int seriescounter = 0;
        for (int i=1; i<=input.length(); i++){
            if ((checkdigit(input.charAt(i-1))||ignoreDigits) && (seriescounter <seriesLength)) {
                seriescounter++;
                sb_series.append(input.charAt(i - 1));
            }
            else
                sb_number.append(input.charAt(i-1));
        }
        res.add("%"+sb_series.toString()+"%");
        res.add("%"+sb_number.toString()+"%");
        System.out.println("SERIES::>"+sb_series.toString());
        System.out.println("number::>"+sb_number.toString());
        return res;
    };


    public static String trimApply(String input){
        BigDecimal bd = new BigDecimal(input);
        BigDecimal result  =  bd.setScale(2, RoundingMode.HALF_UP);
        return String.valueOf(result);
    };

    public  static void safeDelete(String filename){
        if (new File(filename).exists())
            new File(filename).delete();
    };

    public int ret(int a){
        return 3;
    }

    public static int callret(Object obj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = obj.getClass().getMethod("ret", int.class);
        System.out.println("000");
        method.setAccessible(true);
        return (int) method.invoke(obj,0);
    };

}
