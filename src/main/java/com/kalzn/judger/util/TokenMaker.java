package com.kalzn.judger.util;

import java.util.Random;

public class TokenMaker {
    public static String makeToken(int length) {
        StringBuffer token = new StringBuffer();
        Random rand = new Random();

        for (int i = 0; i < length; i++) {
            int tp = rand.nextInt(26+26+10);
            char nextCh;
            if (tp < 26) nextCh = (char)('A'+tp);
            else if (tp < 26 + 26) nextCh = (char)('a' +tp -26);
            else nextCh =(char)('0'+tp-26-26);
            token.append(nextCh);
        }
        return token.toString();
    }
}
