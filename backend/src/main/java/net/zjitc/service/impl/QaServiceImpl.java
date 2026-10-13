package net.zjitc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.zjitc.entity.QaAnswer;
import net.zjitc.entity.QaQuestion;
import net.zjitc.mapper.QaAnswerMapper;
import net.zjitc.mapper.QaQuestionMapper;
import net.zjitc.service.QaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QaServiceImpl extends ServiceImpl<QaQuestionMapper, QaQuestion> implements QaService {

    private final QaAnswerMapper answerMapper;

    public QaServiceImpl(QaAnswerMapper answerMapper) {
        this.answerMapper = answerMapper;
    }

    @Override
    public List<QaQuestion> getQuestionList() {
        LambdaQueryWrapper<QaQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(QaQuestion::getCreateTime);
        return list(wrapper);
    }

    @Override
    public QaQuestion getQuestionDetail(Long id) {
        baseMapper.incrementViewCount(id);
        return getById(id);
    }

    @Override
    public boolean createQuestion(Long userId, String title, String description) {
        QaQuestion question = new QaQuestion();
        question.setUserId(userId);
        question.setTitle(title);
        question.setDescription(description);
        return save(question);
    }

    @Override
    public List<QaQuestion> searchQuestions(String keyword) {
        return baseMapper.searchByKeyword(keyword);
    }

    @Override
    public List<QaAnswer> getAnswers(Long questionId) {
        return answerMapper.selectByQuestionId(questionId);
    }

    @Override
    public boolean addAnswer(Long questionId, Long userId, String content) {
        QaAnswer answer = new QaAnswer();
        answer.setQuestionId(questionId);
        answer.setUserId(userId);
        answer.setContent(content);
        answer.setIsAccepted(false);
        answerMapper.insert(answer);
        baseMapper.incrementAnswerCount(questionId);
        return true;
    }

    @Override
    @Transactional
    public boolean acceptAnswer(Long answerId, Long questionId, Long userId) {
        QaQuestion question = getById(questionId);
        if (question == null || !question.getUserId().equals(userId)) {
            return false;
        }
        // 取消其他已采纳的回答
        answerMapper.update(null, new LambdaUpdateWrapper<QaAnswer>()
                .eq(QaAnswer::getQuestionId, questionId)
                .eq(QaAnswer::getIsAccepted, true)
                .set(QaAnswer::getIsAccepted, false));
        // 采纳当前回答
        QaAnswer answer = answerMapper.selectById(answerId);
        if (answer != null) {
            answer.setIsAccepted(true);
            answerMapper.updateById(answer);
            return true;
        }
        return false;
    }
}
