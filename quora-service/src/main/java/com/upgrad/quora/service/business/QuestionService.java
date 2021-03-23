package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {

	@Autowired
	private QuestionDao questionDao;

	@Transactional
	public QuestionEntity createQuestion(QuestionEntity questionEntity) throws
			AuthorizationFailedException {
		return questionDao.createQuestion(questionEntity);
	}

	@Transactional
	public List<QuestionEntity> getAllQuestions() throws AuthorizationFailedException {
		return questionDao.getAllQuestions();
	}
}
