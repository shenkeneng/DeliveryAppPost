package com.ewu.core.utils;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ewu on 2016/3/23.
 */
public class InputUtils {

    public static boolean isNumeric(String str) {
        Pattern pattern = java.util.regex.Pattern.compile("[0-9]*");
        Matcher match = pattern.matcher(str);
        return match.matches();
    }

    public static boolean isNumericOrLetter(String str) {
        Pattern patten = Pattern.compile("[0-9a-zA-Z]*");
        Matcher matcher = patten.matcher(str);
        return matcher.matches();
    }

    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }

    public static boolean isMobileNO(String mobiles) {
        String expression = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(mobiles);
        Log.d("wxl", matcher.matches() + "");
        return matcher.matches();
    }


}
