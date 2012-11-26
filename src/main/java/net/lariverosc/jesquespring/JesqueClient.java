package net.lariverosc.jesquespring;

import net.greghaines.jesque.Config;
import net.greghaines.jesque.Job;
import net.greghaines.jesque.client.Client;
import net.greghaines.jesque.client.ClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Alejandro Riveros Cruz <lariverosc@gmail.com>
 */
public class JesqueClient {

	private Logger logger = LoggerFactory.getLogger(JesqueClient.class);
	private Client client;

	/**
	 *
	 * @param config
	 */
	public JesqueClient(Config config) {
		this.client = new ClientImpl(config);
	}

	/**
	 *
	 * @param className
	 * @param args
	 * @return
	 */
	public void execute(Class clazz, Object... args) {
		Job job = new Job(clazz.getName(), args);
		client.enqueue("JESQUE_QUEUE", job);
		logger.info("Job {} succesfully enqueued ", clazz.getName());
	}

	/**
	 *
	 * @param className
	 * @param args
	 * @return
	 */
	public void execute(String beanId, Object... args) {
		Job job = new Job(beanId, args);
		client.enqueue("JESQUE_QUEUE", job);
		logger.info("Job {} succesfully enqueued ", beanId);
	}
}
