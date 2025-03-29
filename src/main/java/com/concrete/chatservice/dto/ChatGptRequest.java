package com.concrete.chatservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class ChatGptRequest implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String model;
    private List<Message> messages;

    @Data
    @AllArgsConstructor
    public static class Message implements Serializable {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String role;   // "user" or "assistant"
        private String content;
    }
}