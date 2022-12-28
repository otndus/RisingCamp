package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.config.BaseResponseStatus.POST_USERS_INVALID_PHONENUM;
import static com.example.demo.utils.ValidationRegex.*;

@RestController
@RequestMapping("/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;




    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * 회원 조회 API
     * [GET] /users/:userIdx
     * @return
     */
    @ResponseBody
    @GetMapping("/{userIdx}")
    public BaseResponse<GetUserRes> getUserByIdx(@PathVariable("userIdx") int userIdx){
        try{
            GetUserRes getUserRes = userProvider.getUsersByIdx(userIdx);
            return new BaseResponse<>(getUserRes);
        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 회원가입 API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    // Body
    @ResponseBody
    @PostMapping("") // (POST) 127.0.0.1:9000/users
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {

        if(postUserReq.getPhoneNum() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_PHONENUM);
        }
        if(postUserReq.getNickName() == null || postUserReq.getNickName().length() < 2){
            return new BaseResponse<>(POST_USERS_NICKNAME);
        }
        if(!isRegexNickName(postUserReq.getNickName())){
            return new BaseResponse<>(POST_USERS_INVALID_NICKNAME);
        }

        if(!isRegexPhoneNum(postUserReq.getPhoneNum())){
            return new BaseResponse<>(POST_USERS_INVALID_PHONENUM);
        }
        try{
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 유저정보변경 API
     * [PATCH] /users/:userIdx
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}") // (PATCH) 127.0.0.1:9000/users/:userIdx
    public BaseResponse<String> modifyUserName(@PathVariable("userIdx") int userIdx, @RequestBody User user){
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            if(user.getNickName() == null || user.getNickName().length() < 2){
                return new BaseResponse<>(POST_USERS_NICKNAME);
            }
            if(!isRegexNickName(user.getNickName())){
                return new BaseResponse<>(POST_USERS_INVALID_NICKNAME);
            }

            PatchUserReq patchUserReq = new PatchUserReq(userIdx, user.getNickName(), user.getImgUrl());
            userService.modifyUserName(patchUserReq);

            String result = "회원정보가 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 로그인 API
     * [POST] /users/logIn
     */
    @ResponseBody
    @PostMapping("/log-in")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) {
        if (postLoginReq.getPhoneNum() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PHONENUM);
        } else if (!isRegexPhoneNum(postLoginReq.getPhoneNum())) {
            return new BaseResponse<>(POST_USERS_INVALID_PHONENUM);
        }
        try {
            PostLoginRes postLoginRes = userService.loginUser(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 회원탈퇴 API
     * [PATCH] /users/:userIdx
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/withdraw/{userIdx}") // (PATCH) 127.0.0.1:9000/users/:userIdx
    public BaseResponse<String> withdrawUser(@PathVariable("userIdx") int userIdx){
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            DeleteUserReq deleteUserReq = new DeleteUserReq(userIdx);
            userService.withdrawUser(deleteUserReq);

            String result = "회원 탈퇴에 성공하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


}
