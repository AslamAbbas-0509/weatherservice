package com.concrete.chatservice.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.concrete.chatservice.dto.ChatGptRequest;
import com.concrete.chatservice.service.ChatGptService;

@RestController
@RequestMapping("/chats")
public class ChatController {

    private final ChatGptService chatGptService;

    public ChatController(ChatGptService chatGptService) {
        this.chatGptService = chatGptService;
    }
    
    @GetMapping("/history/{id}")
    public List<ChatGptRequest.Message> chatHistory(@PathVariable Integer id)
    {
    	return chatGptService.getHistoryByUserId(id)  ;
    }

    @GetMapping
    public ResponseEntity<List<ChatGptRequest.Message>> chat(@RequestParam String message,@RequestParam Integer userId) throws Exception {
    	
    	if(userId!=12)
    	throw new Exception("Userid is invalid");
    	
        return new ResponseEntity<> (chatGptService.chatWithDBGpt(message,userId),HttpStatus.OK);
    
    }
    
   
    @DeleteMapping
    public void chatDelete(@RequestParam Integer userId) throws InterruptedException, ExecutionException {
		CompletableFuture<String> result = CompletableFuture.supplyAsync(() ->
		{
			
			return "String";
					});
		chatGptService.chatDelete(userId);
		
    }
    
    
}