package org.example.server.user.service;

import org.example.server.user.domain.param.SelfUserForm;
import org.example.server.user.domain.param.UserForm;
import org.example.server.user.domain.param.UserPageSearchFilter;
import org.example.server.user.domain.param.UserPwdForm;
import org.example.server.user.domain.vo.UserVO;
import org.example.server.web.domain.request.PageRequest;
import org.example.server.web.domain.request.PageVO;

import java.util.List;

public interface UserService {

    void editPwd(UserPwdForm form);

    void resetPwd(Long userId);

    void removeUser(Long userId);

    void editSelfUser(SelfUserForm form);

    void editUser(UserForm form);

    UserVO createUser(UserForm form);

    PageVO<UserVO> userPage(PageRequest<UserPageSearchFilter> request);

    UserVO getUser(Long userId);

    List<UserVO> getUsersInIds(List<Long> ids);

    List<UserVO> getUsersLikeNickname(String nickname);

    String getNickname(Long id);

    List<String> getNicknames(List<Long> ids);

    UserVO getUserByMobile(String mobile);
}
