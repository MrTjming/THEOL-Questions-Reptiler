
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Jemy-Tan
 * @date: 2019/5/23
 * @description:
 */


public class Question {
    public enum Type {
        singular_select, mutil_select, judge;
    }

    private Type type; // 题目类型
    private String title;  // 标题
    private String options; //选项
    private String answer;  //回答
    private String remark; //
    private SqliteHelper sql;

    public String getTitle() {
        return title;
    }

    public String getOptions() {
        return options;
    }

    public String getAnswer() {
        return answer;
    }

    public Question(Type type, String title, String options, String answer) throws SQLException, ClassNotFoundException {
        this.type = type;
        if (title.contains("判断题|")) {
            this.type = Type.judge;
            title = title.replaceAll("判断题", "");
        } else if (title.contains("单项选择题")) {
            this.type = Type.singular_select;
            title = title.replaceAll("单项选择题", "");
        } else if (title.contains("多项选择题")) {
            this.type = Type.mutil_select;
            title = title.replaceAll("多项选择题", "");
        }
        this.title = title;
        this.options = options;
        this.answer = answer;
        this.sql = new SqliteHelper("testHelper.db");
    }

    public Question(Type type, String title, String options, String answer, String remark) throws SQLException, ClassNotFoundException {
        this.type = type;
        if (title.contains("判断题|")) {
            this.type = Type.judge;
            title.replaceAll("判断题|", "");
        } else if (title.contains("单项选择题")) {
            this.type = Type.singular_select;
            title.replaceAll("单项选择题|", "");
        } else if (title.contains("多项选择题")) {
            this.type = Type.mutil_select;
            title.replaceAll("多项选择题|", "");
        }


        this.title = title;
        this.options = options;
        this.answer = answer;
        this.remark = remark;
        this.sql = new SqliteHelper("testHelper.db");

    }

    // 判断是否存在该title的题
    public boolean has_question(String title, String answer) {
        try {
            if (answer.equals(" -- "))
                return true;

            List<Integer> sList = sql.executeQuery("select count(*) from questions where title ='" + title + "' and answer ='" + answer + "'", new RowMapper<Integer>() {
                @Override
                public Integer mapRow(ResultSet rs, int index)
                        throws SQLException {
                    String num = rs.getString("Count(*)");
                    return Integer.parseInt(num);
                }
            });
//            System.out.printf("num is :%d",sList.get(0));
            return sList.get(0) > 0 ? true : false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean save() {
        try {
            if (has_question(title, answer))
                return false;

            sql.executeUpdate("insert into questions (title,options,answer,remark,type) values('" + title + "','" + options + "','" + answer + "','" + remark + "','" + type + "')");
            return true;
//            List<Question> sList = sql.executeQuery("select count(*) from questions where title ='"+title+"'", new RowMapper<Question>() {
//                @Override
//                public Question mapRow(ResultSet rs, int index)
//                        throws SQLException {
//                    String title=rs.getString("title");
//                    return new Question(Type.singular_select,title,"11","1");
//                }
//            });
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Question> get_all_question() throws SQLException, ClassNotFoundException {
        List<Question> sList = sql.executeQuery("select * from questions", new RowMapper<Question>() {
            @Override
            public Question mapRow(ResultSet rs, int index)
                    throws SQLException, ClassNotFoundException {
                String title = rs.getString("title");
                String options = rs.getString("options");
                String answer = rs.getString("answer");
                return new Question(Type.singular_select, title, options, answer);
            }
        });
        return sList;
    }

    class DataFiller implements RowMapper {
        @Override
        public Question mapRow(ResultSet rs, int index)
                throws SQLException, ClassNotFoundException {
            String title = rs.getString("title");
            String options = rs.getString("options");
            String answer = rs.getString("answer");
            return new Question(Type.singular_select, title, options, answer);
        }
    }

    public List<Question> get_all_question_sorted() throws SQLException, ClassNotFoundException {
        List<Question> result_list = new ArrayList<>();
        result_list.addAll(sql.executeQuery("SELECT * FROM \"questions\" where title like \"%前言%\";", new DataFiller()));
        result_list.addAll(sql.executeQuery("SELECT * FROM \"questions\" where title like \"%第一章%\";", new DataFiller()));
        result_list.addAll(sql.executeQuery("SELECT * FROM \"questions\" where title like \"%第二章%\";", new DataFiller()));
        result_list.addAll(sql.executeQuery("SELECT * FROM \"questions\" where title like \"%第四章%\";", new DataFiller()));
        result_list.addAll(sql.executeQuery("SELECT * FROM \"questions\" where title like \"%第五章%\";", new DataFiller()));
        result_list.addAll(sql.executeQuery("SELECT * FROM \"questions\" where title like \"%第六章%\";", new DataFiller()));
        result_list.addAll(sql.executeQuery("SELECT * FROM \"questions\" where title like \"%第七章%\";", new DataFiller()));
        result_list.addAll(sql.executeQuery("SELECT * FROM \"questions\" where title like \"%第八章%\";", new DataFiller()));
        result_list.addAll(sql.executeQuery("SELECT * FROM \"questions\" where title like \"%第九章%\";", new DataFiller()));
        result_list.addAll(sql.executeQuery("SELECT * FROM \"questions\" where title like \"%第十章%\";", new DataFiller()));
        result_list.addAll(sql.executeQuery("SELECT * FROM \"questions\" where title like \"%第十一章%\";", new DataFiller()));
        result_list.addAll(sql.executeQuery("SELECT * FROM \"questions\" where title like \"%第十四章%\";", new DataFiller()));
        result_list.addAll(sql.executeQuery("SELECT * FROM \"questions\" where title like \"%结束语%\";", new DataFiller()));


        return result_list;
    }

    public void print() {
        System.out.printf("标题:%s\n选项:\n%s\n答案:%s\n解析%s\n", title, options, answer, remark);
    }
}
