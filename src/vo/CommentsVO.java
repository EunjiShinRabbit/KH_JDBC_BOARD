package vo;

import java.sql.Date;

public class CommentsVO {
    private int member_num;
    private String comment_content;
    private Date write_date;
    private int write_num;

    public int getMember_num() {
        return member_num;
    }

    public void setMember_num(int member_num) {
        this.member_num = member_num;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public Date getWrite_date() {
        return write_date;
    }

    public void setWrite_date(Date write_date) {
        this.write_date = write_date;
    }

    public int getWrite_num() {
        return write_num;
    }

    public void setWrite_num(int write_num) {
        this.write_num = write_num;
    }
}
