import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class test {
    public static void main(String[] args) throws Exception {

        Map<String, String> post = new HashMap<>();
        post.put("u", "qmvlei@hi2.in");
        post.put("hpassword", "ba3253876aed6bc22d4a6ff53d8406c6ad864195ed144ab5c87621b6c233b548baeae6956df346ec8c17f5ea10f35ee3cbc514797ed7ddd3145464e2a0bab413");
        post.put("cv", "3.5.4");
        post.put("lan", "zh-CN");
        post.put("lser", "qmvlei@hi2.in");
        post.put("lang", "zh");
        post.put("h", "login");
        post.put("platform", "chrome");
        post.put("base", "https://base4-sv.diltwo.com/client/");
        post.put("ua", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");
        post.put("lid", "c77963fa-d7f8-4eb2-8786-667a3075f018");
        post.put("os", "win");
        String json = sendPost("https://base4-sv.diltwo.com/client/rest/config/extension", post);
        Map<String, String> retMap = new HashMap<>();
        JSONObject jsonObject = JSON.parseObject(json);
        JSONObject json_data = JSON.parseObject(jsonObject.getString("data"));
        JSONObject json_PUBLICSERVERS = JSON.parseObject(json_data.getString("PUBLICSERVERS"));
        for (Entry<String, Object> string : json_PUBLICSERVERS.entrySet()) {
            JSONObject serverInfo = JSON.parseObject(string.getValue().toString());
            retMap.put(serverInfo.getString("Host"), serverInfo.getString("Port") + "#" + serverInfo.getString("Country"));
        }
        JSONObject json_SERVERS = JSON.parseObject(json_data.getString("SERVERS"));
        for (Entry<String, Object> string : json_SERVERS.entrySet()) {
            JSONObject serverInfo = JSON.parseObject(string.getValue().toString());
            retMap.put(serverInfo.getString("Host"), serverInfo.getString("Port") + "#" + serverInfo.getString("Country"));
        }
        JSONObject json_PREMIUMSERVERS = JSON.parseObject(json_data.getString("PREMIUMSERVERS"));
        for (Entry<String, Object> string : json_PREMIUMSERVERS.entrySet()) {
            JSONObject serverInfo = JSON.parseObject(string.getValue().toString());
            retMap.put(serverInfo.getString("Host"), serverInfo.getString("Port") + "#" + serverInfo.getString("Country"));
        }

        // System.out.println("username:" + jsonObject.getString("s_login"));
        // System.out.println("password:" + jsonObject.getString("s_token"));

        String baseJson = "{\"+auto switch\":{\"color\":\"#99dd99\",\"defaultProfileName\":\"direct\",\"name\":\"auto switch\",\"profileType\":\"SwitchProfile\",\"rules\":[{\"condition\":{\"conditionType\":\"HostWildcardCondition\",\"pattern\":\"internal.example.com\"},\"profileName\":\"direct\"}],\"revision\":\"16937f2184a\"},\"-addConditionsToBottom\":false,\"-confirmDeletion\":true,\"-downloadInterval\":1440,\"-enableQuickSwitch\":false,\"-monitorWebRequests\":true,\"-quickSwitchProfiles\":[],\"-refreshOnProfileChange\":true,\"-revertProxyChanges\":true,\"-showExternalProfile\":true,\"-showInspectMenu\":true,\"-startupProfileName\":\"\",\"schemaVersion\":2,\"-showConditionTypes\":0}";
        JSONObject base_JsonObject = JSONObject.parseObject(baseJson);

        for (Entry<String, String> string : retMap.entrySet()) {
            String serverProfileName = string.getValue().toString().split("#")[1];
            JSONObject serverProfileName_JSON = new JSONObject();
            {
                JSONObject auth = new JSONObject();
                {
                    JSONObject fallbackProxy = new JSONObject();
                    fallbackProxy.put("password", jsonObject.getString("s_token"));
                    fallbackProxy.put("username", jsonObject.getString("s_login"));
                    auth.put("fallbackProxy", fallbackProxy);

                }
                serverProfileName_JSON.put("auth", auth);
            }
            {
                JSONObject fallbackProxy = new JSONObject();
                fallbackProxy.put("host", string.getKey());
                fallbackProxy.put("port", Integer.parseInt(string.getValue().toString().split("#")[0]));
                fallbackProxy.put("scheme", "https");
                serverProfileName_JSON.put("fallbackProxy", fallbackProxy);
            }
            {
                JSONArray bypassList = new JSONArray();
                {
                    JSONObject bypassListJSON = new JSONObject();
                    bypassListJSON.put("conditionType", "BypassCondition");
                    bypassListJSON.put("pattern", "127.0.0.1");
                    bypassList.add(bypassListJSON);

                }
                {
                    JSONObject bypassListJSON = new JSONObject();
                    bypassListJSON.put("conditionType", "BypassCondition");
                    bypassListJSON.put("pattern", "10.*");
                    bypassList.add(bypassListJSON);

                }
                {
                    JSONObject bypassListJSON = new JSONObject();
                    bypassListJSON.put("conditionType", "BypassCondition");
                    bypassListJSON.put("pattern", "192.168.*");
                    bypassList.add(bypassListJSON);

                }
                serverProfileName_JSON.put("bypassList", bypassList);
            }

            serverProfileName_JSON.put("color", "#d63");
            serverProfileName_JSON.put("name", "SetupCrackByDM-" + serverProfileName);
            serverProfileName_JSON.put("profileType", "FixedProfile");
            serverProfileName_JSON.put("revision", UUID.randomUUID().toString());

            base_JsonObject.put("+SetupCrackByDM-" + serverProfileName, serverProfileName_JSON);
        }
        System.out.println(base_JsonObject.toJSONString());
    }

    public static String sendPost(String url, Map<String, String> map) {
        String result = "";
        try {
            System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
            System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
            System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "info");
            System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "info");
            System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "info");

            CloseableHttpClient httpclient = HttpClients.createDefault();
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
            HttpPost httppost = new HttpPost(url);

            httppost.setEntity(entity);
            CloseableHttpResponse response = null;
            response = httpclient.execute(httppost);
            HttpEntity entity1 = response.getEntity();
            result = EntityUtils.toString(entity1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
