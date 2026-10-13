package net.zjitc.service.impl;

import net.zjitc.service.EmailService;
import net.zjitc.service.VerifyCodeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerifyCodeServiceImpl implements VerifyCodeService {

    private final EmailService emailService;

    /**
     * 存储验证码：email -> { code, expireTime }
     */
    private final Map<String, CodeHolder> codeMap = new ConcurrentHashMap<>();

    /** 验证码有效期（分钟） */
    private static final long EXPIRE_MINUTES = 5;

    /** 发送冷却时间（秒） */
    private static final long COOLDOWN_SECONDS = 60;

    public VerifyCodeServiceImpl(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void sendCode(String email) {
        // 检查冷却时间
        CodeHolder existing = codeMap.get(email);
        if (existing != null) {
            long elapsed = java.time.Duration.between(existing.sendTime, LocalDateTime.now()).getSeconds();
            if (elapsed < COOLDOWN_SECONDS) {
                long wait = COOLDOWN_SECONDS - elapsed;
                throw new RuntimeException("请 " + wait + " 秒后再发送验证码");
            }
        }
        // 生成6位随机数字验证码 (100000 ~ 999999)
        String code = String.valueOf(new Random().nextInt(900000) + 100000);
        codeMap.put(email, new CodeHolder(code, LocalDateTime.now().plusMinutes(EXPIRE_MINUTES), LocalDateTime.now()));
        emailService.sendVerificationCode(email, code);
    }

    @Override
    public boolean verifyCode(String email, String code) {
        CodeHolder holder = codeMap.get(email);
        if (holder == null) {
            return false; // 未发送验证码
        }
        if (LocalDateTime.now().isAfter(holder.expireTime)) {
            codeMap.remove(email); // 过期清理
            return false; // 验证码已过期
        }
        return holder.code.equals(code);
    }

    @Override
    public void removeCode(String email) {
        codeMap.remove(email);
    }

    private static class CodeHolder {
        private final String code;
        private final LocalDateTime expireTime;
        private final LocalDateTime sendTime;

        CodeHolder(String code, LocalDateTime expireTime, LocalDateTime sendTime) {
            this.code = code;
            this.expireTime = expireTime;
            this.sendTime = sendTime;
        }
    }
}
