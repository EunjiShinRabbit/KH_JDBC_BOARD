package dao;

import com.board.kh.util.Common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class BoardDAO {
    // 로그인함수 로그인 여부 확인해서 로그인 성공하면 회원번호 반환하고 아니면 -1 반환
    public int login(){
        Scanner sc = new Scanner(System.in);
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String nickname, pwd;
        int member_num;
        System.out.print("로그인할 닉네임을 입력: ");
        nickname = sc.next();
        System.out.print("비밀번호를 입력: ");
        pwd = sc.next();

        try{
            String sql = "SELECT * FROM MEMBER WHERE NICKNAME = ? AND PWD = ?";
            conn = Common.getConnection(); // 연결 생성
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nickname);
            pstmt.setString(2, pwd);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                member_num = rs.getInt("MEMBER_NUM");
            } else member_num = -1;
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);

        } catch (Exception e){
            e.printStackTrace();
            return -1;
        }
        if (member_num != -1 )System.out.print("회원번호 "+member_num+"으로 ");
        return member_num;

    }

    public void memInsert(){
        Scanner sc = new Scanner(System.in);
        Connection conn = null;
        PreparedStatement pstmt = null;

        System.out.println("회원가입에 필요한 정보를 입력 하세요");
        System.out.print("닉네임 : ");
        String nickname, pwd, pwdCheck;
        nickname = sc.next();
        while (true){
            System.out.print("비밀번호(8자리 초과, 최대 20자) : ");
            pwd = sc.next();
            if (pwd.length() <= 8 || pwd.length() > 20) {
                System.out.println("비밀번호의 길이를 확인해주세요");
                continue;
            }
            System.out.print("비밀번호 확인: ");
            pwdCheck = sc.next();
            if (!pwd.equals(pwdCheck)) System.out.println("비밀번호와 비밀번호 확인이 일치하지 않습니다 다시 입력해주세요");
            else break;
        }

        String sql = "INSERT INTO MEMBER(MEMBER_NUM, NICKNAME, PWD) VALUES (MEMBER_NUM.NEXTVAL, ?, ?)";
        try{
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nickname);
            pstmt.setString(2, pwd);
            pstmt.executeUpdate();

        } catch (Exception e){e.printStackTrace();}
        Common.close(pstmt);
        Common.close(conn);
        System.out.println(nickname + "회원님의 가입이 성공적으로 완료되었습니다");
    }

}
