package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

	@PersistenceContext
	private EntityManager entityManager;

	public QuestionEntity createQuestion(QuestionEntity questionEntity) {
		try {
			entityManager.persist(questionEntity);
			return questionEntity;
		} catch (Exception e) {
			return null;
		}
	}

	public List<QuestionEntity> getAllQuestions() {
		try {
			return entityManager.createNamedQuery("getAllQuestions", QuestionEntity.class).getResultList();
		} catch (NoResultException ex) {
			return null;
		}
	}

	public List<QuestionEntity> getAllQuestionsByUser(final String authToken, final String uuid) {
		return entityManager.createNamedQuery("allQuestionsByUserId", QuestionEntity.class).setParameter("uuid", uuid).getResultList();
	}

	public QuestionEntity getQuestionById(final String uuid) {
		try {
			return entityManager.createNamedQuery("questionByUuid", QuestionEntity.class).setParameter("uuid", uuid).getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	public QuestionEntity editQuestionContent(QuestionEntity questionEntity) {
		entityManager.merge(questionEntity);
		return questionEntity;
	}

}
