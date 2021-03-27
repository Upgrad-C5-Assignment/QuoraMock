package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.CommonService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/question")
public class QuestionController {

	@Autowired
	private QuestionService questionService;

	@Autowired
	private CommonService commonService;

	@RequestMapping(method = RequestMethod.GET, path="/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String authToken) throws AuthorizationFailedException {

		UserAuthEntity userAuthEntity = commonService.authorizeUser(authToken);

		List<QuestionEntity> allQuestions = questionService.getAllQuestions();
		List<QuestionDetailsResponse> responseList = new LinkedList();

		for(QuestionEntity questionEntity : allQuestions) {
			responseList.add(new QuestionDetailsResponse().id(questionEntity.getUuid()).content(questionEntity.getContent()));
		}

		return new ResponseEntity<List<QuestionDetailsResponse>>(responseList, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, path="/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(@PathVariable("userId") final String userId, @RequestHeader("authorization") final String authToken) throws AuthorizationFailedException, UserNotFoundException {

		UserAuthEntity userAuthEntity = commonService.authorizeUser(authToken);

		List<QuestionEntity> allQuestions = questionService.getAllQuestionsByUser(userId, authToken);
		List<QuestionDetailsResponse> responseList = new LinkedList();

		for(QuestionEntity questionEntity : allQuestions) {
			responseList.add(new QuestionDetailsResponse().id(questionEntity.getUuid()).content(questionEntity.getContent()));
		}

		return new ResponseEntity<List<QuestionDetailsResponse>>(responseList, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, path = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<QuestionResponse> create(@RequestHeader("authorization") final String authToken,
			final QuestionRequest questionRequest) throws
			AuthorizationFailedException {

		UserAuthEntity userAuthEntity = commonService.authorizeUser(authToken);

		QuestionEntity questionEntity = new QuestionEntity();
		questionEntity.setUuid(UUID.randomUUID().toString());
		questionEntity.setContent(questionRequest.getContent());
		questionEntity.setDate(ZonedDateTime.now());
		questionEntity.setUser(userAuthEntity.getUser());

		QuestionEntity createdQuestion = questionService.createQuestion(questionEntity);

		QuestionResponse questionResponse = new QuestionResponse().id(createdQuestion.getUuid()).status("QUESTION CREATED");

		return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.PUT, path = "/edit/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<QuestionEditResponse> editQuestionContent(final QuestionEditRequest questionEditRequest, @PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

		final QuestionEntity questionEntity = questionService.editQuestionContent(questionEditRequest.getContent(), questionId, authorization);

		QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(questionEntity.getUuid()).status("QUESTION EDITED");

		return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.DELETE, path = "/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<QuestionResponse> deleteQuestion(@PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

		final QuestionEntity questionEntity = questionService.deleteQuestion(questionId, authorization);

		QuestionResponse questionResponse = new QuestionResponse().id(questionEntity.getUuid()).status("QUESTION DELETED");

		return  new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.OK);
	}

}
