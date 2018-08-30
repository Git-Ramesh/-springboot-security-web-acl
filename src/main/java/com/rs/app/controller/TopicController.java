package com.rs.app.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.rs.app.model.Topic;
import com.rs.app.service.TopicService;

@RestController
@RequestMapping("/user")
public class TopicController {
	private static final Logger LOGGER = LoggerFactory.getLogger(TopicController.class);
	@Autowired
	private TopicService topicService;

	@GetMapping("/topic/{id}")
//	@PostAuthorize("hasPermisssion(#model[topic],'read')")
	//@PostAuthorize("hasPermission(returnObject, 'READ')")
	public Topic getTopicById(@PathVariable("id") Integer id, Model model ) {
		Topic topic = topicService.getTopicById(id);
		model.addAttribute("topic", topic);
		System.out.println("model: " + model);
		return topic;
		//return new ResponseEntity<Topic>(topic, HttpStatus.OK);
	}

	@GetMapping("/topics")
	public ResponseEntity<List<Topic>> getAllTopics() {
		List<Topic> list = topicService.getAllTopics();
		return new ResponseEntity<List<Topic>>(list, HttpStatus.OK);
	}

	@PostMapping("/topic")
	public ResponseEntity<String> addTopic(@RequestBody Topic topic, UriComponentsBuilder builder) {
		boolean flag = topicService.addTopic(topic);
		if (flag == false) {
			return new ResponseEntity<String>("This Topic already exist", HttpStatus.CONFLICT);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(builder.path("/topic/{id}").buildAndExpand(topic.getId()).toUri());
		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}

	@PutMapping("/topic")
	public ResponseEntity<Topic> updateTopic(@RequestBody Topic topic) {
		topicService.updateTopic(topic);
		return new ResponseEntity<Topic>(topic, HttpStatus.OK);
	}

	@DeleteMapping("/topic/{id}")
	public ResponseEntity<Void> deleteTopic(@PathVariable("id") Integer id) {
		topicService.deleteTopic(id);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
}
