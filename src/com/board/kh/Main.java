package com.board.kh;
import com.board.kh.util.Common;
import dao.*;
import vo.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        boardSelect();
    }

    public static void boardSelect(){
        Scanner sc = new Scanner(System.in);
        BoardDAO dao = new BoardDAO();

        Connection conn = null;
        Statement stmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        // 테이블에 정보 잘 들어있는지 먼저 확인
        try{
            String sql = "SELECT * FROM MEMBER";
            conn = Common.getConnection(); // 연결 생성
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            System.out.println("멤버 테이블의 정보를 불러옵니다");
            while (rs.next()) {
                System.out.println(rs.getInt("MEMBER_NUM")+" "+rs.getString("NICKNAME")
                + " " + rs.getString("PWD"));
            }
        } catch(Exception e) {}
        
        int  memberNum = -1; // 게시판을 사용할 멤버아이디 
        while(true){ // 로그인 하는 while문
            System.out.println("===== [KH 커뮤니티 게시판] =====");
            System.out.println("로그인을 해주세요\n[1]로그인(기존회원) [2]회원가입(신규회원) [3]종료");
            int selNum;
            selNum = sc.nextInt();
            switch (selNum){
                case 1:
                    boolean isWrongPwd = true;
                    while (isWrongPwd){
                        memberNum = dao.login();
                        if(memberNum != -1) {
                            isWrongPwd = false;
                            System.out.println("정상 로그인 되었습니다");
                            continue;
                        }
                        else {
                            System.out.println("아이디 패스워드를 잘못 입력하셨거나 회원이 아닙니다!");
                            System.out.println("실행할 메뉴를 선택해주세요\n[1]로그인을 다시 시도 [2]신규 회원 가입");
                            int tempSel = sc.nextInt();
                            if(tempSel == 1) continue;
                            else if(tempSel == 2) break;
                            else System.out.println("잘못된 입력입니다 다시 로그인을 시도하십시오");
                        }
                    }
                    if(!isWrongPwd) { break; }
                case 2:
                    System.out.println("신규 회원 가입 절차를 진행합니다");
                    dao.memInsert();
                    break;
                case 3: System.out.println("게시판 프로그램을 종료합니다"); return;
            }
            if (memberNum != -1) break;
        }
        // 게시판 동작하는 while 문
        

    }
}
