import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: Jemy-Tan
 * @date: 2019/5/23
 * @description:解析器
 */
public class Parser {
    private String text;

    public Parser(String text) {
        this.text = text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private List<String> searchContains(String regex, String text) {
        Matcher mat = Pattern.compile(regex).matcher(text);

        List<String> list = new ArrayList<>();
        while (mat.find()) {
            list.add(mat.group());
        }
        return list;
    }

    private String clearText(String text) {
        text = text.replaceAll("&.+?;", " ");

        text = text.replaceAll("#.+?;", " ");
        text = text.replaceAll("( )+", " "); // 将多个空格替换为一个
        text = text.replaceAll("<br>", " ");
        text = text.replaceAll("<(.*?)>", ""); // 去掉标签

        return text;
    }

    public void parse() throws SQLException, ClassNotFoundException {
        List<String> tables = searchContains("<table cellpadding=(.*?)[\\s\\S]*?</table>", text);

        for (String table : tables) {
//            System.out.println(table);
            String title = "";
            String options = "";
            String remark = "";
            String answer = "";


            List<String> trs = searchContains("<tr>[\\s\\S]*?</tr>", table);

            String matcher_text = trs.get(1); //解析标题和选项
            Matcher mat = Pattern.compile("value='.*?'>").matcher(matcher_text);
            if (mat.find()) { // 标题解析
                String raw_title = mat.group().substring("value='".length(), mat.group().length() - "'>".length());
                title = clearText(raw_title);
            }

            mat = Pattern.compile("answer(.*?)[\\s\\S]*?(<br>|</td>)+?").matcher(matcher_text);
            while (mat.find()) { // 选项解析
                String raw_options = mat.group().substring("answer\" >".length(), mat.group().length() - "</br>".length());
                String option = clearText(raw_options) + "\n";
                options += option;
            }

            int answer_num = 2;
            if (trs.size() >= 5) { // 试题解析
                matcher_text = trs.get(answer_num++);
                String temp_remark = searchContains("<td colspan=(.*?)[\\s\\S]*?</tr>", matcher_text).get(0);
                temp_remark = temp_remark.substring("<td colspan=\"2\" class=\"Fimg\">[试题解析]".length(), temp_remark.length() - "</tr>".length());
                remark = clearText(temp_remark);
            }

            matcher_text = trs.get(answer_num); // 正确答案
            String temp_anwser = searchContains("<td colspan(.*?)[\\s\\S]*?</td>", matcher_text).get(0);
            temp_anwser = temp_anwser.substring("<td colspan=\"2\" class=\"Fimg\">[参考答案]".length(), temp_anwser.length() - "</td>".length());
            answer = clearText(temp_anwser);

            Question question = new Question(Question.Type.singular_select, title, options, answer, remark);
            boolean isMatch = question.save();

        }


    }


    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException, InterruptedException {
        // 下面这个url填对应课程点进去的url,参照这个格式去找
        String url = "http://szjx.ouc.edu.cn/meol/common/question/test/student/list.jsp?tagbug=client&cateId=24949&status=1&strStyle=new06";

        // 登陆方式一: 使用cookie登陆
        String cookie = "JSESSIONID=751E7843660A17DC38A5491FF82A2849;";
        WebReptiler webReptiler = new WebReptiler(url, cookie);

//         登陆方式二: 使用账号密码登陆
//        String username = ""; // 账号
//        String password = ""; // 密码
//        WebReptiler webReptiler=new WebReptiler(url);
//        webReptiler.login(username,password);


        webReptiler.auto_click_test("22400165",20); // 自动答题,testid是测试页面的testid,用浏览器F12开发者工具可以查看到

        List<String> pages = webReptiler.get_pages_content();
        for (String text : pages) {
            Parser parser = new Parser(text);
            parser.parse();
        }
        System.out.println("解析所有试题内容完成...");
        webReptiler.logout(); // 注销账号

        WebRenderer renderer = new WebRenderer();
        System.out.println(renderer.render());
    }
}
