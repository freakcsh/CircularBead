package com.freak.circularbead.utils;



/**
 * 暂时用来保存临时数据
 *
 * @author freak
 * @date 2019/9/11.
 */

public class DataUtils {

    public static String transNum(String cardNum, int num) {
        String result;
        if (cardNum.length() <= 8) {
            result = cardNum;
        } else {
            StringBuilder tmp = new StringBuilder(cardNum.substring(0, 4));
            for (int index = 0; index < num; index++) {
                tmp.append("*");
            }
            tmp.append(cardNum.substring(cardNum.length() - 4));
            result = tmp.toString();
        }
        return result;
    }


}
