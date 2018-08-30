package com.rs.app.repository;

import java.util.List;

import com.rs.app.model.Topic;

public interface TopicRepository {
	List<Topic> getAllTopics();

	Topic getTopicById(long id);

	void addTopic(Topic topic);

	void updateTopic(Topic topic);

	void deleteTopic(long id);

	boolean topicExists(String title, String category);
}
