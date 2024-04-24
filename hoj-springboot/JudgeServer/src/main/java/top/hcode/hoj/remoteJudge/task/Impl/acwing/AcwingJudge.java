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
        WebSocketWithCookies client = new WebSocketWithCookies(URI.create(SUBMIT_URL), cookieMap);
        client.connect();//websocket进行连接
        //进行JSON字符串的拼接
        JSONObject json = new JSONObject();
        json.set("activity", "problem_submit_code")
                .set("problem_id", remoteJudgeDTO.getPid())
                .set("code", remoteJudgeDTO.getUserCode())
                .set("language", remoteJudgeDTO.getLanguage())
                .set("mode","normal")
                .set("problem_activity_id",0)
                .set("record","[]")
                .set("program_time",0);
        client.send(json.toString());
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

//    /**
//     * 从给定URL的HTML内容中检索CSRF令牌。
//     * CSRF（跨站请求伪造）令牌用于防止CSRF攻击。
//     * 此方法向指定的URL发送GET请求，并解析响应以在HTML中找到CSRF令牌输入字段。
//     *
//     * @param url 要从中获取CSRF令牌的URL。
//     * @return 如果找到了CSRF令牌，则返回该令牌，否则返回null。
//     */
//    private String getCsrfToken(String url) {
//        // 为指定的URL创建一个GET请求。
//        HttpRequest request = HttpUtil.createGet(url);
//
//        // 添加必要的请求头到请求中。这里假设'headers'是之前已经定义好的变量。
//        request.addHeaders(headers);
//
//        // 执行请求并接收响应。
//        HttpResponse response = request.execute();
//
//        // 将响应的正文内容提取为字符串。
//        String body = response.body();
//
//        // 解析正文的HTML内容来查找CSRF令牌输入字段。
//        // 这里使用Jsoup，一个Java HTML解析库，来搜索名称为'csrfmiddlewaretoken'的输入元素。
//        Element csrfInput = Jsoup.parse(body).select("input[name=csrfmiddlewaretoken]").first();
//
//        // 检查是否找到了CSRF令牌输入字段。
//        if (csrfInput != null) {
//            // 如果找到了输入字段，将其值打印到控制台（用于调试目的）。
//            System.out.println(csrfInput.attr("value"));
//
//            // 返回CSRF令牌的值。
//            return csrfInput.attr("value");
//        } else {
//            // 如果没有找到CSRF令牌输入字段，返回null。
//            // 这可以替换为适当的默认值，或者如果需要的话可以抛出异常。
//            return null;
//        }
//    }


    @Override
    public String getLanguage(String language) {
        return "";
    }
}
