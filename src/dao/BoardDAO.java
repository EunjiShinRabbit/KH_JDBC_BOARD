package dao;

import com.board.kh.util.Common;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BoardDAO {

    // 테이블 정보 조회해보기
    public void selectMem(){
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
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);
        } catch(Exception e) {}
    }

    public void selectWrite(){
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        // 테이블에 정보 잘 들어있는지 먼저 확인
        try{
            String sql = "SELECT * FROM WRITE";
            conn = Common.getConnection(); // 연결 생성
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            System.out.println("게시글 테이블의 정보를 불러옵니다");
            while (rs.next()) {
                System.out.println(rs.getInt("WRITE_NUM")+" "+rs.getString("BOARD_NAME")
                        + " " + rs.getString("WRITE_NAME"));
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);
        } catch(Exception e) {}
    }

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

    public void recentSelect(int memberNum){
        Scanner sc = new Scanner(System.in);
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM (SELECT COUNT(*) \"좋아요 수\", W.WRITE_NUM \"게시글 번호\", BOARD_NAME \"게시판명\", WRITE_NAME \"게시글명\", NICKNAME \"글작성자\", \n" +
                    "WRITE_DATE \"작성일\", WRITE_CONTENTS \"글내용\" FROM MEMBER M, WRITE W, (SELECT DISTINCT * FROM GOOD) G WHERE M.MEMBER_NUM = W.MEMBER_NUM \n" +
                    "AND G.WRITE_NUM = W.WRITE_NUM GROUP BY W.WRITE_NUM, BOARD_NAME, WRITE_NAME, NICKNAME, WRITE_DATE, WRITE_CONTENTS\n" +
                    "UNION SELECT 0 \"좋아요 수\", W.WRITE_NUM \"게시글 번호\", BOARD_NAME \"게시판명\", WRITE_NAME \"게시글명\", NICKNAME \"글작성자\", \n" +
                    "WRITE_DATE \"작성일\", WRITE_CONTENTS \"글내용\" FROM MEMBER M, WRITE W WHERE M.MEMBER_NUM = W.MEMBER_NUM \n" +
                    "AND W.WRITE_NUM NOT IN (SELECT WRITE_NUM FROM GOOD) ORDER BY \"작성일\" DESC) WHERE ROWNUM <= 5";
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            System.out.println("전체 게시판의 최신글 5개를 조회합니다");
            System.out.println("게시글 번호 / 게시판명 / 게시글명 / 작성자 / 작성일 / 글 내용 / 좋아요수");
            while (rs.next()) {
                int writeNum = rs.getInt("게시글 번호");
                System.out.println(writeNum+" / "+ rs.getString("게시판명") + " / " +
                        rs.getString("게시글명") + " / " + rs.getString("글작성자") +
                        " / " + rs.getDate("작성일") + " / " + rs.getString("글내용") +
                        " / " + rs.getInt("좋아요 수"));
                String temp_Sql = "SELECT NICKNAME \"댓글작성자\", COMMENT_CONTENT \"댓글내용\", " +
                        "C.WRITE_DATE \"댓글 작성일\" FROM COMMENTS C, MEMBER M, WRITE W " +
                        "WHERE C.WRITE_NUM = W.WRITE_NUM AND C.MEMBER_NUM = M.MEMBER_NUM " +
                        "AND W.WRITE_NUM = ?";
                PreparedStatement temp_Pstmt = conn.prepareStatement(temp_Sql);
                temp_Pstmt.setInt(1, writeNum);
                ResultSet temp_Rs = temp_Pstmt.executeQuery();
                System.out.println("댓글 작성자 /  댓글 내용");
                while (temp_Rs.next()){
                    System.out.println(temp_Rs.getString("댓글작성자") + " / " +
                            temp_Rs.getString("댓글내용"));
                }
                System.out.println("조회된 현재 게시글에 좋아요를 주시겠습니까? (y/n)");
                char sel1 = sc.next().charAt(0);
                if (sel1 == 'y' || sel1 == 'Y'){
                    goodInsert(memberNum, writeNum);
                }
                System.out.println("조회된 현재 게시글에 댓글을 다시겠습니까? (y/n)");
                char sel2 = sc.next().charAt(0);
                if (sel2 == 'y' || sel2 == 'Y'){
                    commentsInsert(memberNum, writeNum);
                }
            }
        } catch (Exception e){e.printStackTrace();}
    }

    public List<String> boardList(){
        List<String> boardName = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try{
            String sql = "SELECT * FROM BOARD";
            conn = Common.getConnection(); // 연결 생성
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) boardName.add(rs.getString("BOARD_NAME"));
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);
        } catch (Exception e){e.printStackTrace();}
        return boardName;
    }
    public void boardSelect(List<String> boardName, int selNum, int memberNum){
        Scanner sc = new Scanner(System.in);
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT COUNT(*) \"좋아요 수\", W.WRITE_NUM \"게시글 번호\", BOARD_NAME \"게시판명\", WRITE_NAME \"게시글명\", NICKNAME \"글작성자\", \n" +
                    "WRITE_DATE \"작성일\", WRITE_CONTENTS \"글내용\" FROM MEMBER M, WRITE W, (SELECT DISTINCT * FROM GOOD) G " +
                    "WHERE M.MEMBER_NUM = W.MEMBER_NUM AND G.WRITE_NUM = W.WRITE_NUM AND BOARD_NAME = ? GROUP BY W.WRITE_NUM, BOARD_NAME, WRITE_NAME, NICKNAME, WRITE_DATE, WRITE_CONTENTS\n" +
                    "UNION SELECT 0 \"좋아요 수\", W.WRITE_NUM \"게시글 번호\", BOARD_NAME \"게시판명\", WRITE_NAME \"게시글명\", NICKNAME \"글작성자\", \n" +
                    "WRITE_DATE \"작성일\", WRITE_CONTENTS \"글내용\" FROM MEMBER M, WRITE W WHERE M.MEMBER_NUM = W.MEMBER_NUM \n" +
                    "AND W.WRITE_NUM NOT IN (SELECT WRITE_NUM FROM GOOD) AND BOARD_NAME = ? ORDER BY \"게시글 번호\"";
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            String name = boardName.get(selNum - 1);
            pstmt.setString(1, name);
            pstmt.setString(2, name);
            rs = pstmt.executeQuery();
            System.out.println(name + "게시판의 글을 조회합니다");
            System.out.println("게시글 번호 / 게시판명 / 게시글명 / 작성자 / 작성일 / 글 내용 / 좋아요수");
            while (rs.next()) {
                int writeNum = rs.getInt("게시글 번호");
                System.out.println(writeNum+" / "+ rs.getString("게시판명") + " / " +
                        rs.getString("게시글명") + " / " + rs.getString("글작성자") +
                        " / " + rs.getDate("작성일") + " / " + rs.getString("글내용") +
                        " / " + rs.getInt("좋아요 수"));
                String temp_Sql = "SELECT NICKNAME \"댓글작성자\", COMMENT_CONTENT \"댓글내용\", " +
                        "C.WRITE_DATE \"댓글 작성일\" FROM COMMENTS C, MEMBER M, WRITE W " +
                        "WHERE C.WRITE_NUM = W.WRITE_NUM AND C.MEMBER_NUM = M.MEMBER_NUM " +
                        "AND W.WRITE_NUM = ?";
                PreparedStatement temp_Pstmt = conn.prepareStatement(temp_Sql);
                temp_Pstmt.setInt(1, writeNum);
                ResultSet temp_Rs = temp_Pstmt.executeQuery();
                System.out.println("댓글 작성자 /  댓글 내용");
                while (temp_Rs.next()){
                    System.out.println(temp_Rs.getString("댓글작성자") + " / " +
                            temp_Rs.getString("댓글내용"));
                }
                System.out.println("조회된 현재 게시글에 좋아요를 주시겠습니까? (y/n)");
                char sel1 = sc.next().charAt(0);
                if (sel1 == 'y' || sel1 == 'Y'){
                    goodInsert(memberNum, writeNum);
                }
                System.out.println("조회된 현재 게시글에 댓글을 다시겠습니까? (y/n)");
                char sel2 = sc.next().charAt(0);
                if (sel2 == 'y' || sel2 == 'Y'){
                    commentsInsert(memberNum, writeNum);
                }
            }
        } catch (Exception e){e.printStackTrace();}
    }

    public void writeSearchKeyword(int memberNum, String keyword){
        Scanner sc =  new Scanner(System.in);
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM (SELECT COUNT(*) \"좋아요 수\", W.WRITE_NUM \"게시글 번호\", BOARD_NAME \"게시판명\", WRITE_NAME \"게시글명\", NICKNAME \"글작성자\", \n" +
                    "WRITE_DATE \"작성일\", WRITE_CONTENTS \"글내용\" FROM MEMBER M, WRITE W, (SELECT DISTINCT * FROM GOOD) G WHERE M.MEMBER_NUM = W.MEMBER_NUM \n" +
                    "AND G.WRITE_NUM = W.WRITE_NUM GROUP BY W.WRITE_NUM, BOARD_NAME, WRITE_NAME, NICKNAME, WRITE_DATE, WRITE_CONTENTS\n" +
                    "UNION SELECT 0 \"좋아요 수\", W.WRITE_NUM \"게시글 번호\", BOARD_NAME \"게시판명\", WRITE_NAME \"게시글명\", NICKNAME \"글작성자\", \n" +
                    "WRITE_DATE \"작성일\", WRITE_CONTENTS \"글내용\" FROM MEMBER M, WRITE W WHERE M.MEMBER_NUM = W.MEMBER_NUM \n" +
                    "AND W.WRITE_NUM NOT IN (SELECT WRITE_NUM FROM GOOD) ORDER BY \"게시글 번호\") \n" +
                    "WHERE \"게시글명\" LIKE '%'||?||'%' OR \"글내용\" LIKE '%'||?||'%'";
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, keyword);
            pstmt.setString(2, keyword);
            rs = pstmt.executeQuery();
            System.out.println(keyword+"가 포함된 게시글을 조회합니다");
            System.out.println("게시글 번호 / 게시판명 / 게시글명 / 작성자 / 작성일 / 글 내용 / 좋아요수");
            while (rs.next()) {
                int writeNum = rs.getInt("게시글 번호");
                System.out.println(writeNum+" / "+ rs.getString("게시판명") + " / " +
                        rs.getString("게시글명") + " / " + rs.getString("글작성자") +
                        " / " + rs.getDate("작성일") + " / " + rs.getString("글내용") +
                        " / " + rs.getInt("좋아요 수"));
                String temp_Sql = "SELECT NICKNAME \"댓글작성자\", COMMENT_CONTENT \"댓글내용\", " +
                        "C.WRITE_DATE \"댓글 작성일\" FROM COMMENTS C, MEMBER M, WRITE W " +
                        "WHERE C.WRITE_NUM = W.WRITE_NUM AND C.MEMBER_NUM = M.MEMBER_NUM " +
                        "AND W.WRITE_NUM = ?";
                PreparedStatement temp_Pstmt = conn.prepareStatement(temp_Sql);
                temp_Pstmt.setInt(1, writeNum);
                ResultSet temp_Rs = temp_Pstmt.executeQuery();
                System.out.println("댓글 작성자 /  댓글 내용");
                while (temp_Rs.next()){
                    System.out.println(temp_Rs.getString("댓글작성자") + " / " +
                            temp_Rs.getString("댓글내용"));
                }
                System.out.println("조회된 현재 게시글에 좋아요를 주시겠습니까? (y/n)");
                char sel1 = sc.next().charAt(0);
                if (sel1 == 'y' || sel1 == 'Y'){
                    goodInsert(memberNum, writeNum);
                }
                System.out.println("조회된 현재 게시글에 댓글을 다시겠습니까? (y/n)");
                char sel2 = sc.next().charAt(0);
                if (sel2 == 'y' || sel2 == 'Y'){
                    commentsInsert(memberNum, writeNum);
                }
            }
        } catch (Exception e){e.printStackTrace();}
    }

    public void writeInsert(int memberNum){
        Scanner sc = new Scanner(System.in);
        Connection conn = null;
        PreparedStatement pstmt = null;
        String name = null;

        try{
            while(true){
                System.out.println("게시글을 작성할 게시판을 선택하세요");
                List<String> boardName =boardList();
                int boardCnt = 1;
                for(String e : boardName)System.out.print("[" + boardCnt++ +"]" + e + " ");
                System.out.println();
                int selNum = sc.nextInt();
                name = boardName.get(selNum - 1);
                String temp_Sql = "SELECT GRADE \"등급\" " +
                        "FROM WRITE W, MEMBER M, MEMGRADE G " +
                        "WHERE M.MEMBER_NUM = W.MEMBER_NUM AND M.MEMBER_NUM = ? " +
                        "AND (SELECT COUNT(*) FROM WRITE W " +
                        "GROUP BY W.MEMBER_NUM " +
                        "HAVING W.MEMBER_NUM = M.MEMBER_NUM) BETWEEN LOWRITE AND HIWRITE " +
                        "GROUP BY W.MEMBER_NUM, NICKNAME, GRADE";
                conn = Common.getConnection();
                PreparedStatement temp_Pstmt = conn.prepareStatement(temp_Sql);
                temp_Pstmt.setInt(1, memberNum);
                ResultSet rs = temp_Pstmt.executeQuery();
                rs.next();
                int memGrade = rs.getInt("등급");
                Common.close(temp_Pstmt);
                Common.close(conn);
                if (name.equals("성실회원") && memGrade < 2) {
                    System.out.println("게시판에 글을 작성할 권한이 없습니다!");
                    System.out.println("(성실회원 게시판은 2등급부터 사용 가능합니다. 현재 등급 : "+ memGrade+" )");
                    continue;
                }
                else break;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        System.out.println("게시글 작성에 필요한 정보를 입력 하세요");
        System.out.println("게시글 제목 : ");
        sc.nextLine();
        String writeName = sc.nextLine();
        Scanner sc2 = new Scanner(System.in);
        System.out.println("게시글 내용 : ");;
        String writeContents = sc2.nextLine();

        try{
            String sql = "INSERT INTO WRITE(BOARD_NAME, WRITE_NUM, WRITE_NAME, MEMBER_NUM, WRITE_CONTENTS)" +
                    " VALUES(?, WRITE_NUM.NEXTVAL, ?, ?, ?)";
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, writeName);
            pstmt.setInt(3, memberNum);
            pstmt.setString(4, writeContents);
            pstmt.executeUpdate();

        } catch (Exception e){e.printStackTrace();}
        Common.close(pstmt);
        Common.close(conn);
        System.out.println("게시글의 작성이 성공적으로 완료되었습니다");
    }

    public void writeUpdateMember(int memberNum){
        Scanner sc = new Scanner(System.in);
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT COUNT(*) \"좋아요 수\", W.WRITE_NUM \"게시글 번호\", BOARD_NAME \"게시판명\", WRITE_NAME \"게시글명\", NICKNAME \"글작성자\", \n" +
                    "WRITE_DATE \"작성일\", WRITE_CONTENTS \"글내용\" FROM MEMBER M, WRITE W, (SELECT DISTINCT * FROM GOOD) G \n" +
                    "WHERE M.MEMBER_NUM = W.MEMBER_NUM AND G.WRITE_NUM = W.WRITE_NUM AND M.MEMBER_NUM = ? \n" +
                    "GROUP BY W.WRITE_NUM, BOARD_NAME, WRITE_NAME, NICKNAME, WRITE_DATE, WRITE_CONTENTS\n" +
                    "UNION SELECT 0 \"좋아요 수\", W.WRITE_NUM \"게시글 번호\", BOARD_NAME \"게시판명\", WRITE_NAME \"게시글명\", NICKNAME \"글작성자\", \n" +
                    "WRITE_DATE \"작성일\", WRITE_CONTENTS \"글내용\" FROM MEMBER M, WRITE W WHERE M.MEMBER_NUM = W.MEMBER_NUM \n" +
                    "AND M.MEMBER_NUM = ? AND W.WRITE_NUM NOT IN (SELECT WRITE_NUM FROM GOOD) ORDER BY \"게시글 번호\"";
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNum);
            pstmt.setInt(2, memberNum);
            rs = pstmt.executeQuery();
            System.out.println("내가 작성한 게시글을 조회합니다");
            System.out.println("게시글 번호 / 게시판명 / 게시글명 / 작성자 / 작성일 / 글 내용 / 좋아요수");
            while (rs.next()) {
                int writeNum = rs.getInt("게시글 번호");
                System.out.println(writeNum+" / "+ rs.getString("게시판명") + " / " +
                        rs.getString("게시글명") + " / " + rs.getString("글작성자") +
                        " / " + rs.getDate("작성일") + " / " + rs.getString("글내용") +
                        " / " + rs.getInt("좋아요 수") + "\n");
                System.out.println("조회된 현재 게시글을 수정하시겠습니까? (y/n)");
                char sel = sc.next().charAt(0);
                if (sel == 'y' || sel == 'Y'){
                    System.out.println("게시글 수정 메뉴를 선택하세요");
                    System.out.println("[1]게시글 제목 수정 [2]게시글 내용 수정 [3]게시글 제목과 내용 수정");
                    int tempSel = sc.nextInt();
                    switch(tempSel){
                        case 1:
                            String temp_Sql1 = "UPDATE WRITE SET WRITE_NAME = ? WHERE WRITE_NUM = ?";
                            PreparedStatement temp_Pstmt1 = conn.prepareStatement(temp_Sql1);
                            System.out.println("게시글 수정에 필요한 정보를 입력 하세요");
                            System.out.println("게시글 제목 : ");
                            sc.nextLine();
                            String writeName = sc.nextLine();
                            temp_Pstmt1.setString(1, writeName);
                            temp_Pstmt1.setInt(2, writeNum);
                            temp_Pstmt1.executeUpdate();
                            break;
                        case 2:
                            String temp_Sql2 = "UPDATE WRITE SET WRITE_CONTENTS = ? WHERE WRITE_NUM = ?";
                            PreparedStatement temp_Pstmt2 = conn.prepareStatement(temp_Sql2);
                            System.out.println("게시글 수정에 필요한 정보를 입력 하세요");
                            sc.nextLine();
                            System.out.println("게시글 내용 : ");;
                            String writeContents = sc.nextLine();
                            temp_Pstmt2.setString(1, writeContents);
                            temp_Pstmt2.setInt(2, writeNum);
                            temp_Pstmt2.executeUpdate();
                            break;
                        case 3:
                            String temp_Sql3 = "UPDATE WRITE SET WRITE_NAME = ?, WRITE_CONTENTS = ? WHERE WRITE_NUM = ?";
                            PreparedStatement temp_Pstmt3 = conn.prepareStatement(temp_Sql3);
                            System.out.println("게시글 수정에 필요한 정보를 입력 하세요");
                            System.out.println("게시글 제목 : ");
                            sc.nextLine();
                            String writeName2 = sc.nextLine();
                            Scanner sc2 = new Scanner(System.in);
                            System.out.println("게시글 내용 : ");;
                            String writeContents2 = sc2.nextLine();
                            temp_Pstmt3.setString(1, writeName2);
                            temp_Pstmt3.setString(2, writeContents2);
                            temp_Pstmt3.setInt(3, writeNum);
                            temp_Pstmt3.executeUpdate();
                            break;
                    }
                    System.out.println("게시글이 성공적으로 수정되었습니다 다음 게시글을 조회합니다");
                } else System.out.println("수정하지 않고 다음 게시글을 조회합니다");
            }
            System.out.println("더이상 조회할 게시글이 없습니다 게시글 수정을 종료합니다");

        } catch (Exception e){e.printStackTrace();}
    }

    public void writeDeleteMember(int memberNum){
        Scanner sc = new Scanner(System.in);
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT COUNT(*) \"좋아요 수\", W.WRITE_NUM \"게시글 번호\", BOARD_NAME \"게시판명\", WRITE_NAME \"게시글명\", NICKNAME \"글작성자\", \n" +
                    "WRITE_DATE \"작성일\", WRITE_CONTENTS \"글내용\" FROM MEMBER M, WRITE W, (SELECT DISTINCT * FROM GOOD) G \n" +
                    "WHERE M.MEMBER_NUM = W.MEMBER_NUM AND G.WRITE_NUM = W.WRITE_NUM AND M.MEMBER_NUM = ? \n" +
                    "GROUP BY W.WRITE_NUM, BOARD_NAME, WRITE_NAME, NICKNAME, WRITE_DATE, WRITE_CONTENTS\n" +
                    "UNION SELECT 0 \"좋아요 수\", W.WRITE_NUM \"게시글 번호\", BOARD_NAME \"게시판명\", WRITE_NAME \"게시글명\", NICKNAME \"글작성자\", \n" +
                    "WRITE_DATE \"작성일\", WRITE_CONTENTS \"글내용\" FROM MEMBER M, WRITE W WHERE M.MEMBER_NUM = W.MEMBER_NUM \n" +
                    "AND M.MEMBER_NUM = ? AND W.WRITE_NUM NOT IN (SELECT WRITE_NUM FROM GOOD) ORDER BY \"게시글 번호\"";
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNum);
            pstmt.setInt(2, memberNum);
            rs = pstmt.executeQuery();
            System.out.println("내가 작성한 게시글을 조회합니다");
            System.out.println("게시글 번호 / 게시판명 / 게시글명 / 작성자 / 작성일 / 글 내용 / 좋아요수");
            while (rs.next()) {
                int writeNum = rs.getInt("게시글 번호");
                System.out.println(writeNum+" / "+ rs.getString("게시판명") + " / " +
                        rs.getString("게시글명") + " / " + rs.getString("글작성자") +
                        " / " + rs.getDate("작성일") + " / " + rs.getString("글내용") +
                        " / " + rs.getInt("좋아요 수") + "\n");
                System.out.println("조회된 현재 게시글을 삭제하시겠습니까? (y/n)");
                char sel = sc.next().charAt(0);
                if (sel == 'y' || sel == 'Y'){
                    String temp_Sql = "DELETE FROM WRITE WHERE WRITE_NUM = ?";
                    PreparedStatement temp_Pstmt = conn.prepareStatement(temp_Sql);
                    temp_Pstmt.setInt(1, writeNum);
                    temp_Pstmt.executeUpdate();
                    System.out.println("게시글이 성공적으로 삭제되었습니다 다음 게시글을 조회합니다");
                } else System.out.println("삭제하지 않고 다음 게시글을 조회합니다");
            }
            System.out.println("더이상 조회할 게시글이 없습니다 게시글 삭제를 종료합니다");

        } catch (Exception e){e.printStackTrace();}
    }

    public void commentsInsert(int memberNum, int writeNum){
        Scanner sc = new Scanner(System.in);
        Connection conn = null;
        PreparedStatement pstmt = null;

        System.out.println("댓글 작성에 필요한 정보를 입력 하세요");
        System.out.println("댓글 내용 : ");
        String commentsContent = sc.nextLine();

        try{
            String sql = "INSERT INTO COMMENTS(MEMBER_NUM, COMMENT_CONTENT, WRITE_NUM) VALUES(?, ?, ?)";
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNum);
            pstmt.setString(2, commentsContent);
            pstmt.setInt(3, writeNum);
            pstmt.executeUpdate();

        } catch (Exception e){e.printStackTrace();}
        Common.close(pstmt);
        Common.close(conn);
        System.out.println("댓글의 작성이 성공적으로 완료되었습니다");
    }

    public void goodInsert(int memberNum, int writeNum){
        Scanner sc = new Scanner(System.in);
        Connection conn = null;
        PreparedStatement pstmt = null;

        try{
            String sql = "INSERT INTO GOOD VALUES(?, ?)";
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, writeNum);
            pstmt.setInt(2, memberNum);
            pstmt.executeUpdate();

        } catch (Exception e){e.printStackTrace();}
        Common.close(pstmt);
        Common.close(conn);
        System.out.println("게시글 좋아요가 성공적으로 반영되었습니다");
    }
}
