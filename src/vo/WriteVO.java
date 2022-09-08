package vo;

import java.sql.Date;

public class WriteVO {
    private String board_name;
    private int write_num;
    private String write_name;
    private int member_num;
    private String write_contents;
    private Date write_date;

    public String getBoard_name() {
        return board_name;
    }

    public void setBoard_name(String board_name) {
        this.board_name = board_name;
    }

    public int getWrite_num() {
        return write_num;
    }

    public void setWrite_num(int write_num) {
        this.write_num = write_num;
    }

    public String getWrite_name() {
        return write_name;
    }

    public void setWrite_name(String write_name) {
        this.write_name = write_name;
    }

    public int getMember_num() {
        return member_num;
    }

    public void setMember_num(int member_num) {
        this.member_num = member_num;
    }

    public String getWrite_contents() {
        return write_contents;
    }

    public void setWrite_contents(String write_contents) {
        this.write_contents = write_contents;
    }

    public Date getWrite_date() {
        return write_date;
    }

    public void setWrite_date(Date write_date) {
        this.write_date = write_date;
    }
}
