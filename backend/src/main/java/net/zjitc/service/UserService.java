package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.entity.User;

import java.util.Map;

public interface UserService extends IService<User> {
    User login(String username, String password);
    User register(String username, String password, String email);
    boolean updatePassword(Long userId, String oldPassword, String newPassword);
    boolean resetPassword(String email, String newPassword);
    User getByUsername(String username);
    User getByEmail(String email);
}
