package com.rs.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.rs.app.model.Topic;
import com.rs.app.repository.TopicRepository;

@Service
public class TopicServiceImpl implements TopicService {
	@Autowired
	private TopicRepository topicRepository;
	
//	@PostFilter("hasPermission(filterObject, 'WRITE')")
	//@PreAuthorize("hasRole('ROLE_USER')")
	@PostFilter("hasPermission(filterObject, 'READ')")
	@Override
	public List<Topic> getAllTopics() {
		List<Topic> topics = null;
		topics = topicRepository.getAllTopics();
		System.out.println(topics);
		return topics;
	}

	@Override
	public Topic getTopicById(int id) {
		return topicRepository.getTopicById(id);
	}

	@Override
	public boolean addTopic(Topic topic) {
		if(topicRepository.topicExists(topic.getTitle(), topic.getCategory())) {
			return false;
		} else {
			topicRepository.addTopic(topic);
			return true;
		}
	}

	@Override
	public void updateTopic(Topic topic) {
		topicRepository.updateTopic(topic);
	}

	@Override
	public void deleteTopic(int id) {
		topicRepository.deleteTopic(id);
	}

}
