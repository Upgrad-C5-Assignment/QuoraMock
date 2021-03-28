package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.business.CommonService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
}
