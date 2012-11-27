package net.lariverosc.jesquespring;

import java.util.concurrent.Callable;
import net.greghaines.jesque.worker.Worker;
import net.greghaines.jesque.worker.WorkerImpl;
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
public class JesqueWorker implements ApplicationContextAware {

	private static final Logger logger = LoggerFactory.getLogger(JesqueWorker.class);
	private int numWorkers = 1;
	private Worker worker;
	private Callable<WorkerImpl> workerImpl;
	private ApplicationContext applicationContext;

	/**
	 *
	 */
	public void start() {
		logger.info("Starting Jesque executor service");
		if (workerImpl instanceof SpringWorkerImplFactory) {
			((SpringWorkerImplFactory) workerImpl).setApplicationContext(applicationContext);
		}
		worker = new WorkerPool(workerImpl, numWorkers);
		new Thread(worker).start();
	}

	/**
	 *
	 */
	public void stop() {
		logger.info("Stoping Jesque executor service");
		worker.end(true);
	}

	/**
	 *
	 * @return
	 */
	public int getNumWorkers() {
		return numWorkers;
	}

	/**
	 *
	 * @param numWorkers
	 */
	public void setNumWorkers(int numWorkers) {
		this.numWorkers = numWorkers;
	}

	/**
	 *
	 * @return
	 */
	public Worker getWorker() {
		return worker;
	}

	/**
	 *
	 * @param worker
	 */
	public void setWorker(Worker worker) {
		this.worker = worker;
	}

	public Callable<WorkerImpl> getWorkerImpl() {
		return workerImpl;
	}

	public void setWorkerImpl(Callable<WorkerImpl> workerImpl) {
		this.workerImpl = workerImpl;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
