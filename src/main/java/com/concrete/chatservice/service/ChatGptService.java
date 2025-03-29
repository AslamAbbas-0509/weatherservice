package com.concrete.chatservice.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.concrete.chatservice.dto.ChatGptRequest;
import com.concrete.chatservice.dto.ChatGptResponse;
import com.concrete.chatservice.model.ChatRequest;
import com.concrete.chatservice.repository.ChatGptRepository;

@Service
public class ChatGptService {
	private final WebClient webClient;

	private final Map<String, List<ChatGptRequest.Message>> conversationHistory = new ConcurrentHashMap<>();

	private ChatGptRepository chatGptRepository;

	public ChatGptService(@Value("${openai.api.url}") String apiUrl, @Value("${openai.api.key}") String apiKey,
			ChatGptRepository chatGptRepository) {
		this.webClient = WebClient.builder().baseUrl(apiUrl)
				.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
		this.chatGptRepository = chatGptRepository;
	}

	public List<ChatGptRequest.Message> chatWithGpt(String userMessage) {

		List<ChatGptRequest.Message> messages = conversationHistory.getOrDefault("1", new ArrayList<>());

		messages.add(new ChatGptRequest.Message("user", userMessage));

		// Limit conversation history (Optional: Keep last 10 messages)
		if (messages.size() > 10) {
			messages = messages.subList(messages.size() - 10, messages.size());
		}

		ChatGptRequest request = new ChatGptRequest("gpt-3.5-turbo", messages);

		String responseChat = webClient.post().bodyValue(request).retrieve().bodyToMono(ChatGptResponse.class)
				.map(response -> response.getChoices().get(0).getMessage().getContent()).block(); // Blocking for
																									// simplicity
		messages.add(new ChatGptRequest.Message("assistant", responseChat));
		conversationHistory.put("1", messages);
		return Arrays.asList(new ChatGptRequest.Message("assistant",responseChat) );
	}

	@Transactional
	@CacheEvict(value = "ChatHistory", key = "#userId")
	public void chatDelete(Integer userId) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		chatGptRepository.deleteByUserId(userId);
	}
	
	@Transactional
	@CacheEvict(value="ChatHistory",key="#userId")
	public void cacheEvict(Integer userId) {
	}
	@Cacheable(value="ChatHistory",key="#userId")
	public List<ChatGptRequest.Message> getHistoryByUserId(Integer userId)
	{
		List<ChatRequest> chatRequests = chatGptRepository.findByUserIdOrderByIdAsc(userId);
		List<ChatGptRequest.Message> messages = chatRequests.stream()
				.map(a -> new ChatGptRequest.Message(a.getRole(), a.getMessage())).collect(Collectors.toList());

		return messages;
	}
	
	@CacheEvict(value="ChatHistory",key="#userId")
	public List<ChatGptRequest.Message> chatWithDBGpt(String userMessage, Integer userId) {

		List<ChatRequest> chatRequests = chatGptRepository.findByUserIdOrderByIdAsc(userId);
		ChatRequest chatRequest = new ChatRequest(null, userId, "user", userMessage);
		chatGptRepository.save(chatRequest);
		List<ChatGptRequest.Message> messages = chatRequests.stream()
				.map(a -> new ChatGptRequest.Message(a.getRole(), a.getMessage())).collect(Collectors.toList());

		messages.add(new ChatGptRequest.Message("user", userMessage));

		// Limit conversation history (Optional: Keep last 10 messages)
		if (messages.size() > 10) {
			messages = messages.subList(messages.size() - 10, messages.size());
		}
		cacheEvict(userId);
		ChatGptRequest request = new ChatGptRequest("gpt-3.5-turbo", messages);

		String responseChat = webClient.post().bodyValue(request).retrieve().bodyToMono(ChatGptResponse.class)
				.map(response -> response.getChoices().get(0).getMessage().getContent()).block(); // Blocking for
		messages.add(new ChatGptRequest.Message("user", userMessage));																							// simplicity
		ChatRequest chatRequest1 = new ChatRequest(null, userId, "assistant", responseChat);
		chatGptRepository.save(chatRequest1);
		return Arrays.asList(new ChatGptRequest.Message("assistant",responseChat) );
	}

}
