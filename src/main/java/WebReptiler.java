import okhttp3.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: MrTjming
 * @date: 2019/5/24
 * @description:
 */
public class WebReptiler {
    private String url;
    private String host;
    private String cookie;
    private  String username;
    private String password;

    public WebReptiler(String url) {
        this.url = url;
        this.cookie="";
    }

    public WebReptiler(String url, String cookie) {
        this.url = url;
        this.cookie = cookie;
        this.host="http://szjx.ouc.edu.cn/meol/common/question/test/student/";
    }


    public List<String> get_pages_content() throws IOException {
        List<String> urls=get_pages_urls();
        System.out.println("获取所有考试地址完成...共"+urls.size()+"次");
        List<String> result=new ArrayList<String>();
        int i=1;
        for (int j = urls.size()-1; j >=0 ; j--) {
            String url=this.host+urls.get(j);
            result.add(getPage(url));
            System.out.println("获取第"+(i++)+"次内容完成...");
            if(i>=21)
                break;
        }
//        for(String raw_url : urls){
//            String url=this.host+raw_url;
//            result.add(getPage(url));
//            System.out.println("获取第"+(i++)+"次内容完成...");
//        }
        System.out.println("获取所有试题内容完成...");
        return result;
    }

    public void login(String username,String password) throws IOException {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "IPT_LOGINUSERNAME="+username+"&IPT_LOGINPASSWORD="+password);
        Request request = new Request.Builder()
                .url("http://szjx.ouc.edu.cn/meol/loginCheck.do")
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("cache-control", "no-cache")
                .addHeader("Postman-Token", "51059882-134e-41b0-a8fd-8b24e9d50bea")
                .build();

        Response response = client.newCall(request).execute();
        Headers headers = response.headers();
        HttpUrl loginUrl = request.url();

//        System.out.println(response.body().string());

        List<Cookie> cookies = Cookie.parseAll(loginUrl, headers);
        if (cookies != null){
            for (Cookie cookie :cookies){
                this.cookie+=cookie.toString();
            }
        }

//        System.out.println(this.cookie);

    }

    public void logout() throws IOException {
        getPage("http://szjx.ouc.edu.cn/meol/popups/logout.jsp");
    }

    public void auto_click_test(String testid) throws IOException, InterruptedException {
        auto_click_test(testid, 20);
    }
    /**
     * @param testid 测试页面的testid,用浏览器F12开发者工具可以查看到
     * @param test_num 要自动测试的次数
     * @throws IOException
     * @throws InterruptedException
     */
    public void auto_click_test(String testid, int test_num) throws IOException, InterruptedException {
        String pre_url="http://szjx.ouc.edu.cn/meol/common/question/test/student/stu_qtest_pre.jsp?testId=";
        String submit_url="http://szjx.ouc.edu.cn/meol/common/question/test/student/stu_qtest_over.jsp?testId=";
        int num=1;
        while (num<=test_num){
            System.out.printf("开始第%d次测试......",num);
            String text=getPage(pre_url+testid);
//                System.out.println(text);

            loop_submit("http://szjx.ouc.edu.cn/meol/common/question/test/student/stu_qtest_question.jsp?testId="+testid,testid);
            text=getPage(submit_url+testid);
            System.out.printf("测试结束 ,等待一分钟后继续,还有%d次测试\n",test_num-num);

            num++;
            if(num<=test_num)
                Thread.sleep(56*1000);

        }
//
//        List<String> testIds=get_testIds();
//        for (int i = 0; i <testIds.size() ; i++) {
//            testIds.set(i,testIds.get(i).replaceAll("stu_qtest_navigate.jsp\\?testId=",""));
//        }
//
//        System.out.println(testIds);
//        for (String testid:testIds){
//            int num=3;
//            while (num>0){
//                String text=getPage(pre_url+testid);
////                System.out.println(text);
//
//                loop_submit("http://szjx.ouc.edu.cn/meol/common/question/test/student/stu_qtest_question.jsp?testId="+testid,testid);
//                text=getPage(submit_url+testid);
//                Thread.sleep(61*1000);
//
////                System.out.println(text);
//                num--;
//            }
//        }

    }

    public void loop_submit(String raw_url,String testId) throws IOException, InterruptedException {
        //System.out.println("http://szjx.ouc.edu.cn/meol/common/question/test/student/stu_qtest_main.jsp?testId="+testId);
        String page_content=getPage("http://szjx.ouc.edu.cn/meol/common/question/test/student/stu_qtest_main.jsp?testId="+testId);
        List<String> question_code=new ArrayList<>();
        //System.out.println(page_content);
        Matcher mat=Pattern.compile("onClick=\"jumpto(.*?)\"").matcher(page_content);
        while (mat.find()){
            String raw_code=mat.group().substring("onClick=\"jumpto(".length(),mat.group().length()-")\"".length());
            question_code.add(raw_code);
        }
//        System.out.println(question_code);

        for(int i=10;i<=14;i++){
            post2(raw_url,question_code.get(i),testId);
            Thread.sleep(1000);
        }
    }

    public void post2(String raw_url,String question_code,String testId) throws IOException {
        URL url = new URL(raw_url);


        //post参数
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("currentSubmitQuestionid", question_code);
        params.put("actionType", "saveAnswer");
        params.put("testId", testId);
        params.put("eqId", question_code);
        params.put("nextquestion", "named");
        params.put("answer", "T");


        //开始访问
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String,Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "gbk"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "gbk"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setRequestProperty("Cookie",this.cookie);
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);

        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

        StringBuilder sb = new StringBuilder();
        for (int c; (c = in.read()) >= 0;)
            sb.append((char)c);
        String response = sb.toString();
//        System.out.println(response);
    }

    private String getPage(String url_str) throws IOException {
        URL url=new URL(url_str);
        URLConnection connection=url.openConnection() ;
        connection.setRequestProperty("Cookie",cookie);
        connection.setRequestProperty("Accept-Encoding","gzip, deflate");
        connection.connect();
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(connection.getInputStream(), "gbk")) ;
        String result="";
        String line="";
      while ((line=bufferedReader.readLine())!=null){
            result+=line;
        }
        bufferedReader.close();
//        System.out.println("本次内容为:"+result);
        return result;


    }

    private List<String> get_testIds() throws IOException{
        List<String> unit_urls=new ArrayList<String>();

        String index_page=getPage(this.url);
//        System.out.println(index_page);
        Matcher mat= Pattern.compile("<a href=\"stu_qtest(.*?)lank\">").matcher(index_page);
        while (mat.find()){
//            System.out.println("group os :");
//            System.out.println(mat.group());

            String temp_url=mat.group().substring("<a href=\"".length(),mat.group().length()-"\" target=\"_blank\">".length());
            unit_urls.add(temp_url);
        }
        System.out.println("testId is :");
        System.out.println(unit_urls);

        return unit_urls;
    }

    private List<String> get_pages_urls() throws IOException {
        List<String> unit_urls=get_testIds();
        List<String> urls=new ArrayList<String>();

        for(String sub_url:unit_urls){
            String url=this.host+sub_url;

            String unit_page=getPage(url);

            Matcher mat= Pattern.compile("<a href=\"(.*?)\"+?").matcher(unit_page);
            while (mat.find()){
//                System.out.println(mat.group());
                String temp_url=mat.group().substring("<a href=\"".length(),mat.group().length()-"\"".length());
                urls.add(temp_url);
            }
        }
        return urls;
    }

}
