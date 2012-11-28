package net.lariverosc.jesquespring;

import java.util.Collection;
import java.util.concurrent.Callable;
import net.greghaines.jesque.Config;
import net.greghaines.jesque.worker.WorkerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author Alejandro Riveros Cruz <lariverosc@gmail.com>
 */
public class SpringWorkerFactory implements Callable<WorkerImpl>, ApplicationContextAware {

	private Logger logger = LoggerFactory.getLogger(SpringWorkerFactory.class);
	private final Config config;
	private final Collection<String> queues;
	private ApplicationContext applicationContext;

	/**
	 * Creates a new factory for <code>SpringWorker</code> that use the provided arguments.
	 *
	 * @param config used to create a connection to Redis
	 * @param queues the list of queues to poll
	 */
	public SpringWorkerFactory(final Config config, final Collection<String> queues) {
		this.config = config;
		this.queues = queues;
	}


	/**
	 * Create a new <code>SpringWorkerImpl</code> using the arguments provided in the factory constructor.
	 */
	@Override
	public WorkerImpl call() {
		WorkerImpl springWorker = new SpringWorker(this.config, this.queues);
		((SpringWorker) springWorker).setApplicationContext(this.applicationContext);
		return springWorker;
	}

	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
