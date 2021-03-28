package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnswerService {

    @Autowired
    private AnswerDao answerDao;
    @Autowired
    private CommonService commonService;

    @Transactional
    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        return answerDao.createAnswer(answerEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity getAnswerById(final String questionId) throws InvalidQuestionException {

        AnswerEntity answerEntity = answerDao.getAnswerById(questionId);
        if (answerEntity == null) {
            throw new InvalidQuestionException("ANS-001", "Entered answer uuid does not exist");
        }

        return answerEntity;
    }

    @Transactional
    public AnswerEntity updateAnswer(final String authToken, final String answerId, final String newContent) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthEntity userAuthEntity = commonService.authorizeUser(authToken);
        AnswerEntity answerEntity = answerDao.getAnswerById(answerId);
        if (answerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }
        if (!userAuthEntity.getUser().getUuid().equals(answerEntity.getUser().getUuid())) {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
        }
        answerEntity.setAns(newContent);
        return answerDao.updateAnswer(answerEntity);
    }

    @Transactional
    public AnswerEntity deleteAnswer(final String authToken, final String answerId) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthEntity userAuthEntity = commonService.authorizeUser(authToken);
        AnswerEntity answerEntity = answerDao.getAnswerById(answerId);
        if (answerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }
        if (!userAuthEntity.getUser().getUuid().equals(answerEntity.getUser().getUuid() )&& !userAuthEntity.getUser().getRole().equals("admin")) {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
        }
        return answerDao.updateAnswer(answerEntity);
    }
}
