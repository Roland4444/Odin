package se.roland.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HTTPClient {
    public static void sendPOST(HashMap<String, String> params, String url) throws IOException {
       System.out.println("\n\n\nSENDING POST TO::"+url);
       CloseableHttpClient httpclient = HttpClients.createDefault();
       HttpPost httppost = new HttpPost(url);
       List<NameValuePair> params__ = new ArrayList<NameValuePair>(params.size());  //map(a->a.getKey().replace("::","").replace(" ","_")).
       params.entrySet().stream().forEach(b->params__.add(new BasicNameValuePair(b.getKey(), String.valueOf(b.getValue()))));
       params.entrySet().stream().forEach(b -> {
           System.out.println("KEY::"+b.getKey()+" VALUE::"+String.valueOf( b.getValue()));
       });
       httppost.setEntity(new UrlEncodedFormEntity(params__, "UTF-8"));
       HttpResponse response = httpclient.execute(httppost);
            System.out.println("EXECUTED REQUEST TO draft url:"+httppost);
            HttpEntity entity = response.getEntity();
    }

    public static String sendPostwithAutorisation(String url, String AutorisationString) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
    //    HttpGet httpreq = new HttpGet(url);
        HttpPost httpreq = new HttpPost(url);
        httpreq.setHeader("Authorization", AutorisationString);
        HttpResponse response = httpclient.execute(httpreq);
        int code = response.getStatusLine().getStatusCode();
        System.out.println("CODE RESPONCE::"+code);

        System.out.println("EXECUTED REQUEST TO::"+httpreq);
        HttpEntity entity = response.getEntity();
        String resp = "";
        resp = EntityUtils.toString(response.getEntity());
        return resp;

    }

    public static String checkpayment(String url, String id,  String AutorisationString) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
      //  HttpGet httpreq = new HttpGet(url+id);

        HttpPost httpreq = new HttpPost(url+id);
        httpreq.setHeader("Authorization", AutorisationString);
        HttpResponse response = httpclient.execute(httpreq);
        int code = response.getStatusLine().getStatusCode();
        System.out.println("CODE RESPONCE::"+code);

        System.out.println("EXECUTED REQUEST TO::"+httpreq);
        HttpEntity entity = response.getEntity();
        String resp = "";
        resp = EntityUtils.toString(response.getEntity());
        return resp;

    }


    }

