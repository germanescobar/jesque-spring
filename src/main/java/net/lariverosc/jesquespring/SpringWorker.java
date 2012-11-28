package net.lariverosc.jesquespring;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import net.greghaines.jesque.Config;
import net.greghaines.jesque.Job;
import net.greghaines.jesque.worker.Worker;
import net.greghaines.jesque.worker.WorkerEvent;
import static net.greghaines.jesque.worker.WorkerEvent.JOB_PROCESS;
import net.greghaines.jesque.worker.WorkerImpl;
import net.greghaines.jesque.worker.WorkerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author Alejandro Riveros Cruz <lariverosc@gmail.com>
 */
public class SpringWorker extends WorkerImpl implements ApplicationContextAware {

	private Logger logger = LoggerFactory.getLogger(SpringWorker.class);
	private ApplicationContext applicationContext;

	/**
	 *
	 * @param config
	 * @param queues
	 * @param jobTypes
	 * @param applicationContext
	 */
	public SpringWorker(final Config config, final Collection<String> queues) {
		super(config, queues, Collections.EMPTY_MAP);
		this.addListener(new WorkerListener() {
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
	}

	
	@Override
	protected void process(final Job job, final String curQueue) {
		try {
			Runnable runnableJob = null;
			if (applicationContext.containsBeanDefinition(job.getClassName())) {
				runnableJob = (Runnable) applicationContext.getBean(job.getClassName(), job.getArgs());
			} else {
				try {
					Class clazz = Class.forName(job.getClassName());
					String[] beanNames = applicationContext.getBeanNamesForType(clazz, true, false);
					if (applicationContext.containsBeanDefinition(job.getClassName())) {//check bean id
						runnableJob = (Runnable) applicationContext.getBean(beanNames[0], job.getArgs());
					} else {
						if (beanNames != null && beanNames.length == 1) {
							runnableJob = (Runnable) applicationContext.getBean(beanNames[0], job.getArgs());
						}
					}
				} catch (ClassNotFoundException cnfe) {
					logger.error("Not beanId or class definition found {}", job.getClassName());
					throw new Exception("Not beanId or class definition found " + job.getClassName());
				}
			}
			if (runnableJob != null) {
				this.listenerDelegate.fireEvent(JOB_PROCESS, this, curQueue, job, null, null, null);
				if (isThreadNameChangingEnabled()) {
					renameThread("Processing " + curQueue + " since " + System.currentTimeMillis());
				}
				execute(job, curQueue, runnableJob);
			}
		} catch (Exception e) {
			failure(e, job, curQueue);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
