package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.entity.QaAnswer;
import net.zjitc.entity.QaQuestion;

import java.util.List;
import java.util.Map;

public interface QaService extends IService<QaQuestion> {
    List<QaQuestion> getQuestionList();
    QaQuestion getQuestionDetail(Long id);
    boolean createQuestion(Long userId, String title, String description);
    List<QaQuestion> searchQuestions(String keyword);

    // 回答
    List<QaAnswer> getAnswers(Long questionId);
    boolean addAnswer(Long questionId, Long userId, String content);
    boolean acceptAnswer(Long answerId, Long questionId, Long userId);
}
