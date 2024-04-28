package top.hcode.hoj.remoteJudge.task.Impl.acwing;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import top.hcode.hoj.remoteJudge.entity.RemoteJudgeDTO;
import top.hcode.hoj.remoteJudge.entity.RemoteJudgeRes;
import top.hcode.hoj.remoteJudge.task.RemoteJudgeStrategy;

import java.net.HttpCookie;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 2024/4/23
 * 栋dong
 */
public class AcwingJudge extends RemoteJudgeStrategy {
    public static final String HOST = "https://www.acwing.com";
    public static final String LOGIN_URL = "/user/account/signin/";
    public static final String SUBMIT_URL = "wss://www.acwing.com/wss/socket/";
    public static final String SUBMISSION_RESULT_URL = "/contests/%s/submissions/%s";
    public static Map<String, String> headers = MapUtil
            .builder(new HashMap<String, String>())
            .put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36")
            .map();

    @Override
    public void submit() {
        login();//先登录
        RemoteJudgeDTO remoteJudgeDTO = getRemoteJudgeDTO();//获取信息
        List<HttpCookie> cookies = remoteJudgeDTO.getCookies();//拿到cookie
        Map<String, String> cookieMap = new HashMap<>();
        for (HttpCookie cookie : cookies) {
            cookieMap.put(cookie.getName(), cookie.getValue());
        }//向cookieMap中添加cookie
        JSONObject code = new JSONObject();
        code.set("activity", "problem_submit_code")
                .set("problem_id", remoteJudgeDTO.getPid())
                .set("code", remoteJudgeDTO.getUserCode())
                .set("language", remoteJudgeDTO.getLanguage())
                .set("mode","normal")
                .set("problem_activity_id",0)
                .set("record","[]")
                .set("program_time",0);
        WebSocketWithCookies client = new WebSocketWithCookies(URI.create(SUBMIT_URL),code.toString(),cookieMap);
        client.connect();//websocket进行连接
        //进行JSON字符串的拼接
    }

    @Override
    public RemoteJudgeRes result() {
        return null;
    }

    @Override
    public void login() {
        // 清除当前线程的cookies缓存
        HttpRequest.getCookieManager().getCookieStore().removeAll();

        RemoteJudgeDTO remoteJudgeDTO = getRemoteJudgeDTO();

        String csrfToken = getCsrfToken(HOST + LOGIN_URL);
        String postData = "csrfmiddlewaretoken=" + csrfToken +
                "&username=" + remoteJudgeDTO.getUsername() +
                "&password=" + remoteJudgeDTO.getPassword() +
                "&remember_me=on";
        HttpResponse response = HttpRequest.post(HOST + LOGIN_URL)
                .header(Header.REFERER, HOST)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8")
                .header("X-CSRFToken", csrfToken)
                .header("X-Requested-With", "XMLHttpRequest")
                .body(postData)
                .execute();

        remoteJudgeDTO.setLoginStatus(response.getStatus())
                .setCookies(response.getCookies())
                .setCsrfToken(csrfToken);
    }

    private String getCsrfToken(String url) {
        HttpRequest request = HttpUtil.createGet(url);
        request.addHeaders(headers);
        HttpResponse response = request.execute();
        String body = response.body();
        return ReUtil.get("name=\"csrfmiddlewaretoken\" value=\"([\\s\\S]*?)\"", body, 1);
    }


    @Override
    public String getLanguage(String language) {
        return "";
    }
}
