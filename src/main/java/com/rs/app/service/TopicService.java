package com.rs.app.service;

import java.util.List;

import com.rs.app.model.Topic;

public interface TopicService {
	List<Topic> getAllTopics();

	Topic getTopicById(int id);

	boolean addTopic(Topic topic);

	void updateTopic(Topic topic);

	void deleteTopic(int id);
}
