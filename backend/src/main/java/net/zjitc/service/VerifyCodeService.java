package net.zjitc.service;

public interface VerifyCodeService {
    /**
     * 生成验证码并发送到指定邮箱
     */
    void sendCode(String email);

    /**
     * 校验验证码是否正确
     * @return true 校验通过
     */
    boolean verifyCode(String email, String code);

    /**
     * 校验通过后移除验证码（一次性使用）
     */
    void removeCode(String email);
}
