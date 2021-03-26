package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {

	@Autowired
	private QuestionDao questionDao;

	@Autowired
	private CommonService commonService;

	@Autowired
	private UserDao userDao;

	@Transactional
	public QuestionEntity createQuestion(QuestionEntity questionEntity) throws
			AuthorizationFailedException {
		return questionDao.createQuestion(questionEntity);
	}

	@Transactional
	public List<QuestionEntity> getAllQuestions() throws AuthorizationFailedException {
		return questionDao.getAllQuestions();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<QuestionEntity> getAllQuestionsByUser(final String userId, final String authToken) throws AuthorizationFailedException, UserNotFoundException {

		UserAuthEntity userAuthEntity = commonService.authorizeUser(authToken);
		UserEntity userEntity = userDao.getUserByUuid(userId);

		if (userAuthEntity == null) {
			throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
		} else if (userAuthEntity.getLogoutAt() != null) {
			throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
		}

		if (userEntity == null) {
			throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
		}
		return questionDao.getAllQuestionsByUser(authToken, userId);
	}

}
