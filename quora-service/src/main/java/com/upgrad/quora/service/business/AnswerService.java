package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class AnswerService {

    @Autowired
    private AnswerDao answerDao;
    @Autowired
    private CommonService commonService;

    @Autowired
    private UserDao userDao;
    @Autowired
    private QuestionDao questionDao;

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
        if (!userAuthEntity.getUser().getUuid().equals(answerEntity.getUser().getUuid()) && !userAuthEntity.getUser().getRole().equals("admin")) {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
        }
        return answerDao.updateAnswer(answerEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<AnswerEntity> getAllAnswersByQuestionId(final String questionId, final String authToken) throws AuthorizationFailedException, UserNotFoundException, InvalidQuestionException {
        UserAuthEntity userAuthEntity = userDao.getUserAuthEntityByToken(authToken);

        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else {
            ZonedDateTime logoutAt = userAuthEntity.getLogoutAt();
            if (logoutAt != null) {
                throw new AuthorizationFailedException(
                        "ATHR-002", "User is signed out.Sign in first to get the answers");
            }
        }

        QuestionEntity questionEntity = questionDao.getQuestionById(questionId);
        if(questionEntity == null){
            throw new InvalidQuestionException("QUES-001","The question with entered uuid whose details are to be seen does not exist");
        }
        return answerDao.getAllAnswersByQuestionId( questionId);
    }
}
