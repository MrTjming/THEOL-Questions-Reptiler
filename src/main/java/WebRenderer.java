import java.sql.SQLException;
import java.util.List;

/**
 * @author: Jemy-Tan
 * @date: 2019/5/25
 * @description:
 */
public class WebRenderer {
    private String page;
    private int num = 1;

    public WebRenderer() {
        this.page = "";
    }

    public void add_title(String title) {
        String temp = "<h3>问题" + num + ": " + title + "</h3>";
        page += temp;
        num++;
    }

    public void add_options(String options) {
        if (!options.equals("")) {
            String temp = "<h5>选项: " + options + "</h5>";
            page += temp;
        }
    }

    public void add_answer(String answer) {
        String temp = "<p style=\"color:orange\">答案:<b>" + answer + "</b></p>";
        page += temp;
    }

    public void add_blank() {
        String temp = "<p>----------------------------------------------------------------------------</p>";
        page += temp;
    }

    public void add_head() {
        page += "<!DOCTYPE html>\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "<head>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
                "    <title>毛概题库啊</title>\n" +
                "<style>\n" +
                "        .main{position:absolute;left:8%;right: 8%;}\n" +
                "    </style>\n" +
                "</head>\n" +
                "<div class=\"main\">";
    }

    public void add_end() {
        page += "</div>";
    }

    public void add_question(String title, String options, String answer) {

        add_title(title);
        add_options(options);
        add_answer(answer);
        add_blank();
    }

    public String render() throws SQLException, ClassNotFoundException {
        Question q = new Question(Question.Type.singular_select, "", "", "");
        List<Question> questions = q.get_all_question_sorted();

        add_head();
        for (Question temp : questions) {
            add_question(temp.getTitle(), temp.getOptions(), temp.getAnswer());
        }
        add_end();
        return page;
    }
}
