package com.hzg.tools;

import org.springframework.stereotype.Component;

@Component
public class StrUtil {
    public final static String openBraces = "{";
    public final static String closeBraces = "}";
    public final static String doubleQuotes = "\"";

    public String getJsonValue(String key, String json) {
        int startPos = json.indexOf(openBraces, json.indexOf(doubleQuotes + key + doubleQuotes));

        int openBracesNum = 1, closeBracesNum = 0;
        int openBracesPos = 0,  closeBracesPos = 0, tempStartPos = startPos+1;

        while (openBracesNum != closeBracesNum) {
            openBracesPos = json.indexOf(openBraces, tempStartPos);
            closeBracesPos = json.indexOf(closeBraces, tempStartPos);

            if (openBracesPos < closeBracesPos && openBracesPos != -1) {
                tempStartPos = openBracesPos+1;
                openBracesNum++;
            }

            if(openBracesPos > closeBracesPos || openBracesPos == -1) {
                tempStartPos = closeBracesPos+1;
                closeBracesNum++;
            }
        }

        return json.substring(startPos, closeBracesPos+1);
    }
}
