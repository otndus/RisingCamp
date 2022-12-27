package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public GetUserRes getUsersByIdx(int userIdx){
        String getUsersByIdxQuery = "select userIdx,nickName,imgUrl,mannerTemp,responseRate,transactionRate from User where userIdx=?";
        int getUsersByIdxParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUsersByIdxQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("nickName"),
                        rs.getString("imgUrl"),
                        rs.getDouble("mannerTemp"),
                        rs.getDouble("responseRate"),
                        rs.getDouble("transactionRate")),
                getUsersByIdxParams);
    }

    public int createUser(PostUserReq postUserReq){
        String createUserQuery = "insert into User (region, phoneNum, nickName) VALUES (?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getRegion(), postUserReq.getPhoneNum(),postUserReq.getNickName()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    public PostLoginReq getPwd(PostLoginReq postLoginReq) {
        String getPwdQuery = "select userIdx, phoneNum from User where userIdx = ? and phoneNum = ?";
        Object[] getPwdParams = new Object[]{postLoginReq.getUserIdx(), postLoginReq.getPhoneNum()};

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs, rowNum) -> new PostLoginReq(
                        rs.getInt("userIdx"),
                        rs.getString("phoneNum")
                ), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getPwdParams
        ); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    public int modifyUserName(PatchUserReq patchUserReq){
        String modifyUserNameQuery = "update User set nickName = ?, imgUrl = ? where userIdx = ? ";
        Object[] modifyUserNameParams = new Object[]{patchUserReq.getNickName(), patchUserReq.getImgUrl(), patchUserReq.getUserIdx()};

        return this.jdbcTemplate.update(modifyUserNameQuery,modifyUserNameParams);
    }

    public int withdrawUser(DeleteUserReq deleteUserReq){
        String withdrawUserQuery = "update User set status = 'N' where userIdx = ? ";
        int withdrawUserParams = deleteUserReq.getUserIdx();

        return this.jdbcTemplate.update(withdrawUserQuery,withdrawUserParams);
    }

    public int checkUser(String phoneNum){
        String checkPhoneQuery = "select exists(select phoneNum from User where phoneNum = ? " +
                "and date(updatedAt) >= date(subdate(now(), interval 7 day)) and date(updatedAt) <= date(now())\n" +
                "and status = 'N')";
        String checkUserParams = phoneNum;
        return this.jdbcTemplate.queryForObject(checkPhoneQuery,
                int.class,
                checkUserParams);
    }


}
