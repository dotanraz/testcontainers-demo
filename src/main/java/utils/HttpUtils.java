package utils;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpUtils {

    public static CloseableHttpResponse httpGetRequest(String url) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        return client.execute(httpGet);
    }

    public static CloseableHttpResponse httpPostWithBody(String url, String jsonBody) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        StringEntity body = new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
        httpPost.setEntity(body);
        return client.execute(httpPost);
    }

    public static String httpResponseToString(CloseableHttpResponse response) throws IOException {
        return EntityUtils.toString(response.getEntity());
    }

}
