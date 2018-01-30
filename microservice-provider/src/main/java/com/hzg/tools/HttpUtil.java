package com.hzg.tools;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class HttpUtil {

    public String get(String url){
        String responseStr = null;
        HttpURLConnection con = null;
        try{
            URL urlObj = new URL(url);
            con = (HttpURLConnection) urlObj.openConnection();

            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) { //success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (responseStr == null) {
                        responseStr = inputLine;
                    } else {
                        responseStr += inputLine;
                    }
                }
                in.close();

            } else {
                System.out.println(url + " GET request not worked");
            }
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            if (con != null) {
                con.disconnect();
            }
        }

        return responseStr ;
    }

    /**
     *
     * @param url
     * @param params  key1=value1&key2=value2...
     * @return
     */
    private String post(String url, String params) {
        String responseStr = null;
        HttpURLConnection con = null;

        try {
            URL urlObj = new URL(url);
            con = (HttpURLConnection) urlObj.openConnection();
            con.setRequestMethod("POST");

            // For POST only - START
            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            os.write(params.getBytes());
            os.flush();
            os.close();
            // For POST only - END

            if ( con.getResponseCode() == HttpURLConnection.HTTP_OK) { //success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (responseStr == null) {
                        responseStr = inputLine;
                    } else {
                        responseStr += inputLine;
                    }
                }
                in.close();

            } else {
                System.out.println(url + " POST request not worked");
            }
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            if (con != null) {
                con.disconnect();
            }
        }

        return responseStr;
    }
}
