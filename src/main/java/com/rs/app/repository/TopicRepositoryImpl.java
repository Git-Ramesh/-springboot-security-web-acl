package com.rs.app.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.rs.app.model.Topic;

@Repository
public class TopicRepositoryImpl implements TopicRepository {
	private static final Logger LOGGER = LoggerFactory.getLogger(TopicRepositoryImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	@SuppressWarnings("unchecked")
	public List<Topic> getAllTopics() {
		return (List<Topic>) entityManager.createQuery("FROM Topic t ORDER BY t.id ASC").getResultList();
	}

	@Override
	public Topic getTopicById(long l) {
		return entityManager.find(Topic.class, l);
	}

	@Override
	public void addTopic(Topic topic) {
		entityManager.persist(topic);
	}

	@Override
	public void updateTopic(final Topic topic) {
		Topic dbTopic = getTopicById(topic.getId());
		if (dbTopic != null) {
			dbTopic.setTitle(topic.getTitle());
			dbTopic.setCategory(topic.getCategory());
			entityManager.flush();
		} else {
			LOGGER.info("Topic doesn't exists");
		}
	}

	@Override
	public void deleteTopic(long id) {
		Topic dbTopic = getTopicById(id);
		if (dbTopic != null) {
			entityManager.remove(dbTopic);
			;
		} else {
			LOGGER.info("Topic doesn't exists to delete");
		}
	}

	@Override
	public boolean topicExists(String title, String category) {
		final String HQL_QUERY = "FROM Topic t WHERE t.title=? AND t.category=?";
		int count = entityManager.createQuery(HQL_QUERY)
									.setParameter(1, title)
									.setParameter(2, category)
									.getResultList().size();
		return count > 0 ? true : false;
	}

}
