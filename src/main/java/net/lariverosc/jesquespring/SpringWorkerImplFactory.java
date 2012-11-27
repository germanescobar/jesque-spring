package net.lariverosc.jesquespring;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import net.greghaines.jesque.Config;
import net.greghaines.jesque.worker.Worker;
import net.greghaines.jesque.worker.WorkerEvent;
import net.greghaines.jesque.worker.WorkerImpl;
import net.greghaines.jesque.worker.WorkerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Alejandro Riveros Cruz <lariverosc@gmail.com>
 */
public class SpringWorkerImplFactory implements Callable<WorkerImpl>  {

	private Logger logger = LoggerFactory.getLogger(SpringWorkerImplFactory.class);
	private final Config config;
	private final Collection<String> queues;
	private final Map<String, ? extends Class<?>> jobTypes;
	private ApplicationContext applicationContext;

	/**
	 * Create a new factory. Returned
	 * <code>WorkerImpl</code>s will use the provided arguments.
	 *
	 * @param config used to create a connection to Redis
	 * @param queues the list of queues to poll
	 * @param jobTypes the list of job types to execute
	 */
	public SpringWorkerImplFactory(final Config config, final Collection<String> queues, final Map<String, ? extends Class<?>> jobTypes) {
		this.config = config;
		this.queues = queues;
		this.jobTypes = jobTypes;
	}

	/**
	 * Create a new
	 * <code>SpringWorkerImpl</code> using the arguments provided to this factory's constructor.
	 */
	@Override
	public WorkerImpl call() {
		WorkerImpl temp = new SpringWorkerImpl(this.config, this.queues, this.jobTypes, this.applicationContext);
		temp.addListener(new WorkerListener() {
			@Override
			public void onEvent(WorkerEvent event, Worker worker, String queue, net.greghaines.jesque.Job job, Object runner, Object result, Exception ex) {
				logger.debug("event {}, worker {}, queue {}", new Object[]{event.name(), worker.getName(), queue});
				switch (event) {
					case JOB_EXECUTE:
						break;
					case JOB_FAILURE:
						break;
					case JOB_PROCESS:
						break;
					case JOB_SUCCESS:
						break;
					case WORKER_ERROR:
						break;
					case WORKER_POLL:
						break;
					case WORKER_START:
						break;
					case WORKER_STOP:
						break;
				}
			}
		});
		return temp;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
