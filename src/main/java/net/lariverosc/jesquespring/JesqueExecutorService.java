package net.lariverosc.jesquespring;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.greghaines.jesque.Config;
import static net.greghaines.jesque.utils.JesqueUtils.map;
import net.greghaines.jesque.worker.Worker;
import net.greghaines.jesque.worker.WorkerPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author Alejandro Riveros Cruz <lariverosc@gmail.com>
 */
public class JesqueExecutorService implements ApplicationContextAware {

	private static final Logger logger = LoggerFactory.getLogger(JesqueExecutorService.class);
	private Set<String> queues;
	private Map<String, ? extends Class<? extends Runnable>> jobTypes;
	private int coresToUse;
	private int workersPerCore;
	private Worker worker;
	private Config config;
	private ApplicationContext applicationContext;

	/**
	 *
	 */
	public JesqueExecutorService(Config config) {
		this.config = config;
		workersPerCore = 1;
	}

	public void start() {
		logger.info("Starting Jesque executor service");
		queues = new HashSet<String>();
		queues.add("JESQUE_QUEUE");
		jobTypes = map();

		coresToUse = coresToUse == 0 ? Runtime.getRuntime().availableProcessors() : coresToUse;
		
		SpringWorkerImplFactory workerImpl = new SpringWorkerImplFactory(config, queues, jobTypes);
		workerImpl.setApplicationContext(applicationContext);
		worker = new WorkerPool(workerImpl, coresToUse * workersPerCore);
		new Thread(worker).start();
	}

	public void stop() {
		logger.info("Stoping Jesque executor service");
		worker.end(true);
	}

	public int getCoresToUse() {
		return coresToUse;
	}

	public void setCoresToUse(int coresToUse) {
		this.coresToUse = coresToUse;
	}

	public int getWorkersPerCore() {
		return workersPerCore;
	}

	public void setWorkersPerCore(int workersPerCore) {
		this.workersPerCore = workersPerCore;
	}

	public Worker getWorker() {
		return worker;
	}

	public void setWorker(Worker worker) {
		this.worker = worker;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
