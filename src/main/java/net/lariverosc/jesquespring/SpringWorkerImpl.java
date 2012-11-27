package net.lariverosc.jesquespring;

import java.util.Collection;
import java.util.Map;
import net.greghaines.jesque.Config;
import net.greghaines.jesque.Job;
import static net.greghaines.jesque.worker.WorkerEvent.JOB_PROCESS;
import net.greghaines.jesque.worker.WorkerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Alejandro Riveros Cruz <lariverosc@gmail.com>
 */
public class SpringWorkerImpl extends WorkerImpl {

	private Logger logger = LoggerFactory.getLogger(SpringWorkerImpl.class);
	private ApplicationContext applicationContext;

	/**
	 *
	 * @param config
	 * @param queues
	 * @param jobTypes
	 * @param applicationContext  
	 */
	public SpringWorkerImpl(final Config config, final Collection<String> queues, final Map<String, ? extends Class<?>> jobTypes, ApplicationContext applicationContext) {
		super(config, queues, jobTypes);
		this.applicationContext = applicationContext;
	}

	@Override
	protected void process(final Job job, final String curQueue) {
		try {
			Runnable runnableJob = null;
			Class clazz = Class.forName(job.getClassName());
			String[] beanNames = applicationContext.getBeanNamesForType(clazz, true, false);
			if (applicationContext.containsBeanDefinition(job.getClassName())) {//check bean id
				runnableJob = (Runnable) applicationContext.getBean(beanNames[0], job.getArgs());
			} else {
				if (beanNames != null && beanNames.length == 1) {
					runnableJob = (Runnable) applicationContext.getBean(beanNames[0], job.getArgs());
				} else {
					System.out.println("Ambigous bean configuration");
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
}
