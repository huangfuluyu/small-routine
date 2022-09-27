package com.wechat.routine.common;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author HuangFuLuYu
 * @date 2022/06/12/18:12
 * @Description
 */

@Component
@Slf4j
public class WxQrCode {
    //获取AccessToken路径
    private static final String AccessToken_URL
            = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";//小程序id
    /**
     * 生成不限个数的小程序码（建议使用）
     * @param accessToken
     * @param filePath
     * @return
     */
    public static void getminiqrQr(String accessToken, String filePath) {
        String WxCode_URL = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=ACCESS_TOKEN";//小程序密钥
        String fileName = "code1.png";
        try {
            String wxCodeURL = WxCode_URL.replace("ACCESS_TOKEN", accessToken);
            URL url = new URL(wxCodeURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");// 提交模式
            // conn.setConnectTimeout(10000);//连接超时 单位毫秒
            // conn.setReadTimeout(2000);//读取超时 单位毫秒
            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
            // 发送请求参数
            JSONObject paramJson = new JSONObject();
            // 注意小程序页面如果不存在，则注释掉page参数
            //paramJson.put("page", "pages/index/index");
            paramJson.set("scene", "1234567890");
            paramJson.set("width", 430);
            paramJson.set("is_hyaline", true);
            paramJson.set("auto_color", true);
            printWriter.write(paramJson.toString());
            // flush输出流的缓冲
            printWriter.flush();
            //开始获取数据
            BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());
            OutputStream os = new FileOutputStream(new File(filePath + fileName));
            int len;
            byte[] arr = new byte[1024];
            while ((len = bis.read(arr)) != -1) {
                os.write(arr, 0, len);
                os.flush();
            }
            os.close();
        } catch (Exception e) {
            log.error("===> error : ", e);
        }
    }


    /**
     * 生成限制个数的小程序码（不建议使用）
     * @param accessToken
     * @param filePath
     * @return
     */
    public static void getminiqrQr2(String accessToken, String filePath) {
        // bad
        String WxCode_URL = "https://api.weixin.qq.com/wxa/getwxacode?access_token=ACCESS_TOKEN" ;
        String fileName = "code2.png";
        try {
            String wxCodeURL = WxCode_URL.replace("ACCESS_TOKEN", accessToken);
            URL url = new URL(wxCodeURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");// 提交模式
            // conn.setConnectTimeout(10000);//连接超时 单位毫秒
            // conn.setReadTimeout(2000);//读取超时 单位毫秒
            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
            // 发送请求参数
            JSONObject paramJson = new JSONObject();
            paramJson.put("path", "pages/index/index");
            paramJson.put("width", 430);
            paramJson.put("is_hyaline", true);
            paramJson.put("auto_color", true);
            printWriter.write(paramJson.toString());
            // flush输出流的缓冲
            printWriter.flush();
            //开始获取数据
            BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());
            OutputStream os = new FileOutputStream(new File(filePath + fileName));
            int len;
            byte[] arr = new byte[1024];
            while ((len = bis.read(arr)) != -1) {
                os.write(arr, 0, len);
                os.flush();
            }
            os.close();
        } catch (Exception e) {
            log.error("===> error : ", e);
        }
    }

}
