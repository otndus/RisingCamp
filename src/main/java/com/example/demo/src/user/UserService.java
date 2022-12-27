package com.example.demo.src.user;


import com.example.demo.config.BaseException;

import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;


    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;

    }


    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        if (userProvider.checkPhone(postUserReq.getPhoneNum()) == 1) {
            throw new BaseException(POST_USERS_WITHDRAW);
        }
//        String phoneNum;
//        try{
//            //암호화
//            phoneNum = new SHA256().encrypt(postUserReq.getPhoneNum());
//            postUserReq.setPhoneNum(phoneNum);
//        } catch (Exception ignored) {
//            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
//        }
        try{
            int userIdx = userDao.createUser(postUserReq);
            String jwt = jwtService.createJwt(userIdx);
            return new PostUserRes(jwt,userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public PostLoginRes loginUser(PostLoginReq postLoginReq) throws BaseException{
        PostLoginReq user = userDao.getPwd(postLoginReq);
        if (userProvider.checkPhone(postLoginReq.getPhoneNum()) == 1) {
            throw new BaseException(POST_USERS_WITHDRAW);
        }

        if(user.getPhoneNum().equals(postLoginReq.getPhoneNum())){
            String jwt = jwtService.createJwt(user.getUserIdx());
            return new PostLoginRes(user.getUserIdx(),jwt);
        }else{
            throw new BaseException(FAILED_TO_LOGIN);
        }
    }

    public void modifyUserName(PatchUserReq patchUserReq) throws BaseException {
        try{
            int result = userDao.modifyUserName(patchUserReq);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_USERNAME);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void withdrawUser(DeleteUserReq deleteUserReq) throws BaseException {
        try{
            int result = userDao.withdrawUser(deleteUserReq);
            if(result == 0){
                throw new BaseException(WITHDRAW_FAIL_USER);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
