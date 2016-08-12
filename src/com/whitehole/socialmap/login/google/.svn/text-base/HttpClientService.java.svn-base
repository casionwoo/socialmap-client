package com.whitehole.socialmap.login.google;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class HttpClientService {
    private static HttpClientService self;

    public static HttpClientService get() {
        if (self == null) {
            self = new HttpClientService();
        }
        return self;
    }

    private HttpContext localContext = null;
    private CookieStore cookieStore = null;
    public static String serverURL =
            "http://casionwoo.appspot.com/";

    private HttpClientService() {
        this.localContext = new BasicHttpContext();
        this.cookieStore = new BasicCookieStore();
        this.localContext.setAttribute(ClientContext.COOKIE_STORE,
                this.cookieStore);
    }

    public CookieStore getCookieStore() {
        return this.cookieStore;
    }

    public HttpResponse execute(final HttpUriRequest request)
            throws ClientProtocolException, IOException {
        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
        for (Cookie c : this.cookieStore.getCookies()) {
            request.addHeader("Cookie", c.getName() + "=" + c.getValue());
        }
        return client.execute(request, this.localContext);
    }

    public <T> T execute(final HttpUriRequest request,
            final ResponseHandler<? extends T> handler)
            throws IOException, ClientProtocolException {
        HttpClient client = new DefaultHttpClient();
        for (Cookie c : this.cookieStore.getCookies()) {
            request.addHeader("Cookie", c.getName() + "=" + c.getValue());
        }
        return client.execute(request, handler, this.localContext);
    }

    public void loginAppEngine(final String token)
            throws ClientProtocolException, IOException {
        HttpClient client = new DefaultHttpClient();
        CookieStore cookieStore = new BasicCookieStore();
        HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS,
                false);
        HttpGet request = new HttpGet(
                serverURL
                        + "_ah/login?continue=http://casionwoo.appspot.com/&auth="
                        + token);
        HttpResponse response = client.execute(request, localContext);
        Log.e("login status code", ""
                + response.getStatusLine().getStatusCode());
        Log.e("url",
                ""
                        + serverURL
                        + "_ah/login?continue=http://casionwoo.appspot.com/&auth="
                        + token);

        if (response.getStatusLine().getStatusCode() != 302) {
            throw new SecurityException("Login failed2");
        }
        boolean success = false;
        Log.e("cook", cookieStore
                .getCookies().toString());
        for (Cookie cookie : cookieStore.getCookies()) {
            Log.e("cookie", cookie.getName());
            this.cookieStore.addCookie(cookie);
            if (cookie.getName().equals("SACSID")
                    || cookie.getName().equals("ACSID")) {
                success = true;
            }
        }
        if (!success) {
            Log.e("login failed", "failed to success");
            this.cookieStore.clear();
            throw new SecurityException("Login failed");
        }
    }

    public void logout() {
        this.cookieStore.clear();
    }

    public String shortenUrl(final String longUrl) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("longUrl", longUrl);
        HttpClient client = new DefaultHttpClient();
        String url = "";
        try {
            HttpPost post = new HttpPost(
                    "https://www.googleapis.com/urlshortener/v1/url");
            post.setHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(json.toString(), "UTF-8"));
            ResponseHandler<String> reshandler = new BasicResponseHandler();
            String response = client.execute(post, reshandler);

            JSONObject responseJson = new JSONObject(response);

            url = responseJson.getString("id");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        catch (IOException e) {
            e.printStackTrace();
        }
        return url;
    }

    /*
     * public String getShortner(final String url) throws IOException { // setup
     * up the HTTP transport HttpTransport transport = GoogleTransport.create();
     * // add default headers GoogleHeaders defaultHeaders = new
     * GoogleHeaders(); transport.defaultHeaders = defaultHeaders;
     * transport.defaultHeaders.put("Content-Type", "application/json");
     * transport.addParser(new JsonHttpParser());
     *
     * // build the HTTP GET request and URL HttpRequest request =
     * transport.buildPostRequest();
     * request.setUrl("https://www.googleapis.com/urlshortener/v1/url");
     * GenericData data = new GenericData(); data.put("longUrl",
     * "http://www.google.com/"); JsonHttpContent content = new
     * JsonHttpContent(); content.data = data; request.content = content;
     * com.google.api.client.http.HttpResponse response = request.execute();
     * Result result = response.parseAs(Result.class); return result.shortUrl; }
     *
     * class Result extends GenericJson { public String shortUrl; }
     */
}
