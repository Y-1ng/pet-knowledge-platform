package net.zjitc.controller;

import net.zjitc.common.Result;
import net.zjitc.entity.User;
import net.zjitc.service.UserService;
import net.zjitc.service.VerifyCodeService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final VerifyCodeService verifyCodeService;

    public UserController(UserService userService, VerifyCodeService verifyCodeService) {
        this.userService = userService;
        this.verifyCodeService = verifyCodeService;
    }

    @PostMapping("/login")
    public Result<User> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || password == null) {
            return Result.error("用户名或密码不能为空");
        }
        User user = userService.login(username, password);
        if (user != null) {
            user.setPassword(null); // 不返回密码
            return Result.success(user);
        }
        return Result.error("用户名或密码错误");
    }

    @PostMapping("/send-code")
    public Result<String> sendCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isEmpty()) {
            return Result.error("邮箱不能为空");
        }
        try {
            verifyCodeService.sendCode(email);
            return Result.success("验证码已发送");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String email = body.get("email");
        String code = body.get("code");
        if (username == null || password == null || email == null || code == null) {
            return Result.error("用户名、密码、邮箱、验证码不能为空");
        }
        if (userService.getByUsername(username) != null) {
            return Result.error("用户名已存在");
        }
        if (!verifyCodeService.verifyCode(email, code)) {
            return Result.error("验证码错误或已过期");
        }
        verifyCodeService.removeCode(email);
        User user = userService.register(username, password, email);
        user.setPassword(null);
        return Result.success(user);
    }

    @GetMapping("/profile")
    public Result<User> getProfile(@RequestParam Long userId) {
        User user = userService.getById(userId);
        if (user != null) {
            user.setPassword(null);
            return Result.success(user);
        }
        return Result.error("用户不存在");
    }

    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
            return Result.success(user);
        }
        return Result.error("用户不存在");
    }

    @PutMapping("/profile")
    public Result<User> updateProfile(@RequestBody User user) {
        User existing = userService.getById(user.getId());
        if (existing == null) {
            return Result.error("用户不存在");
        }
        existing.setNickname(user.getNickname());
        existing.setAvatar(user.getAvatar());
        existing.setBio(user.getBio());
        userService.updateById(existing);
        existing.setPassword(null);
        return Result.success(existing);
    }

    @PutMapping("/password")
    public Result<String> updatePassword(@RequestBody Map<String, String> body) {
        Long userId = Long.valueOf(body.get("userId"));
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        if (userService.updatePassword(userId, oldPassword, newPassword)) {
            return Result.success("密码修改成功");
        }
        return Result.error("原密码错误");
    }

    @PostMapping("/reset-password")
    public Result<String> resetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String newPassword = body.get("newPassword");
        String code = body.get("code");
        if (email == null || newPassword == null || code == null) {
            return Result.error("邮箱、新密码、验证码不能为空");
        }
        if (!verifyCodeService.verifyCode(email, code)) {
            return Result.error("验证码错误或已过期");
        }
        verifyCodeService.removeCode(email);
        if (userService.resetPassword(email, newPassword)) {
            return Result.success("密码重置成功");
        }
        return Result.error("邮箱不存在");
    }

    @PostMapping("/updateAvatar")
    public Result<Map<String, Object>> updateAvatar(@RequestBody Map<String, String> body) {
        String avatarUrl = body.get("url");
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            return Result.error("头像URL不能为空");
        }
        Long userId = Long.valueOf(body.get("userId"));
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setAvatar(avatarUrl);
        userService.updateById(user);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("email", user.getEmail());
        userInfo.put("bio", user.getBio());
        return Result.success(userInfo);
    }
}
