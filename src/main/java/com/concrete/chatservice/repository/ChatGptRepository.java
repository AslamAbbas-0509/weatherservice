package com.concrete.chatservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.concrete.chatservice.model.ChatRequest;

@Repository
public interface ChatGptRepository extends JpaRepository<ChatRequest, Integer > {
	
	List<ChatRequest> findByUserIdOrderByIdAsc(Integer userId);
	void deleteByUserId(Integer userId);

}
