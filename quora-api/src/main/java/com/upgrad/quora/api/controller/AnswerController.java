package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.business.CommonService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping( "/question")
public class AnswerController {
     @Autowired
     private AnswerService answerService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private CommonService commonService;
    @RequestMapping(method = RequestMethod.POST, path = "/{questionId}/answer/create", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@RequestHeader("authorization") final String authToken, @PathVariable("questionId") final String questionId, final AnswerRequest answerRequest) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity userAuthEntity = commonService.authorizeUser(authToken);
        QuestionEntity questionEntity = questionService.getQuestionById(questionId);
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setAns(answerRequest.getAnswer());
        answerEntity.setQuestion(questionEntity);
        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setDate(LocalDateTime.now());
        answerEntity.setUser(userAuthEntity.getUser());
        final AnswerEntity persistedAnswerEntity = answerService.createAnswer(answerEntity);
        AnswerResponse answerResponse = new AnswerResponse().id(persistedAnswerEntity.getUuid()).status("ANSWER CREATED");
        return new ResponseEntity<>(answerResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> updateAnswer(@RequestHeader("authorization") final String authToken, @PathVariable("answerId") final String answerId, final AnswerEditRequest answerEditRequest) throws AuthorizationFailedException, AnswerNotFoundException {
        AnswerEntity answerEntity = answerService.updateAnswer(authToken,answerId,answerEditRequest.getContent());
        AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(answerEntity.getUuid()).status("ANSWER EDITED");
        return new ResponseEntity<>(answerEditResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteQuestion(@PathVariable("answerId") final String answerId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException, AnswerNotFoundException {

        final AnswerEntity answerEntity = answerService.deleteAnswer( authorization,answerId);

        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().id(answerId).status("ANSWER DELETED");

        return new ResponseEntity<>(answerDeleteResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path="answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllQuestionsByUser(@PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authToken) throws AuthorizationFailedException, UserNotFoundException, InvalidQuestionException {

        List<AnswerEntity> allAnswers = answerService.getAllAnswersByQuestionId(questionId,authToken);
        List<AnswerDetailsResponse> responseList = new LinkedList();
        QuestionEntity questionEntity = questionService.getQuestionById(questionId);

        for(AnswerEntity answerEntity : allAnswers) {
            responseList.add(new AnswerDetailsResponse().id(questionEntity.getUuid()).questionContent(questionEntity.getContent()).answerContent(answerEntity.getAns()));
        }

        return new ResponseEntity<>(responseList, HttpStatus.OK);

    }
}
