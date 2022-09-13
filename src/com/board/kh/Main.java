package com.board.kh;
import com.board.kh.util.Common;
import dao.*;
import vo.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    // 자꾸왜안되지ㅜㅠ
    public static void main(String[] args) {
        boardSelect();
    }

    public static void boardSelect(){
        Scanner sc = new Scanner(System.in);
        BoardDAO dao = new BoardDAO();
        int  memberNum = -1; // 게시판을 사용할 멤버아이디

        Connection conn = null;
        Statement stmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        dao.selectMem();
        // dao.selectWrite();


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
        // 게시판 동작하는 while 문@
        while (true){
            System.out.println("이용할 게시판 메뉴를 선택해주세요");
            System.out.println("[1]게시글 조회 [2]게시글 검색 [3]게시글 등록 [4]기존 게시글 수정 [5]기존 게시글 삭제 [6]회원정보 [7]종료");
            int selNum;
            selNum = sc.nextInt();
            switch (selNum){
                case 1:
                    System.out.println("[1]최신 게시글 조회(5개) [2]카테고리별 게시글 조회 ");
                    int tempSel = sc.nextInt();
                    if (tempSel == 1) dao.recentSelect(memberNum);
                    else if (tempSel == 2){
                        List<String> boardName =dao.boardList();
                        System.out.println("게시글을 조회할 게시판을 선택하세요");
                        int boardCnt = 1;
                        for(String e : boardName)System.out.print("[" + boardCnt++ +"]" + e + " ");
                        System.out.println();
                        int tempSel2 = sc.nextInt();
                        dao.boardSelect(boardName, tempSel2, memberNum);
                    }
                    break;
                case 2:
                    System.out.println("검색할 게시글의 키워드를 입력하세요");
                    String keyword = sc.next();
                    dao.writeSearchKeyword(memberNum, keyword);
                    break;
                case 3:
                    dao.writeInsert(memberNum);
                    break;
                case 4:
                    dao.writeUpdateMember(memberNum);
                    break;
                case 5:
                    dao.writeDeleteMember(memberNum);
                    break;
                case 6:
                    System.out.println("[1]회원정보 조회 및 수정 [9]회원 탈퇴 ");
                    int tempSel2 = sc.nextInt();
                    switch (tempSel2){
                        case 1: dao.memberUpdate(memberNum); break;
                        case 9: System.out.println("가입하는건 마음대로지만 탈퇴는 아니란다"); break;
                        default: System.out.println("잘못된 입력입니다"); break;
                    }
                case 7: System.out.println("게시판 프로그램을 종료합니다"); return;

            }
        }

    }
}
