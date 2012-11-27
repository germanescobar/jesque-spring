package net.lariverosc.jesquespring;

import java.util.Arrays;
import junit.framework.Assert;
import net.greghaines.jesque.meta.dao.FailureDAO;
import net.greghaines.jesque.meta.dao.KeysDAO;
import net.greghaines.jesque.meta.dao.QueueInfoDAO;
import net.greghaines.jesque.meta.dao.WorkerInfoDAO;
import net.lariverosc.jesquespring.job.MockJob;
import net.lariverosc.jesquespring.job.MockJobArgs;
import net.lariverosc.jesquespring.job.MockJobFail;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 *
 * @author Alejandro Riveros Cruz <lariverosc@gmail.com>
 */
public class JesqueSpringTest {

	private JesqueClient jesqueClient;
	private JesqueExecutorService jesqueExecutorService;
	private FailureDAO failureDAO;
	private KeysDAO keysDAO;
	private QueueInfoDAO queueInfoDAO;
	private WorkerInfoDAO workerInfoDAO;
	private JedisPool jedisPool;

	/**
	 *
	 */
	@BeforeClass
	public void setUp() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:jesque-context.xml", "classpath:test-context.xml");
		jesqueClient = (JesqueClient) applicationContext.getBean("jesqueClient");
		jesqueExecutorService = (JesqueExecutorService) applicationContext.getBean("jesqueExecutorService");
		failureDAO = (FailureDAO) applicationContext.getBean("failureDAO");
		keysDAO = (KeysDAO) applicationContext.getBean("keysDAO");
		queueInfoDAO = (QueueInfoDAO) applicationContext.getBean("queueInfoDAO");
		workerInfoDAO = (WorkerInfoDAO) applicationContext.getBean("workerInfoDAO");
		jedisPool = (JedisPool) applicationContext.getBean("jedisPool");
	}

	/**
	 *
	 */
	@BeforeMethod
	public void cleanUpRedis() {
		Jedis jedis = jedisPool.getResource();
		jedis.flushDB();
		jedisPool.returnResource(jedis);
	}

	/**
	 *
	 */
	@Test
	public void shouldAddJob() {
		jesqueExecutorService.getWorker().togglePause(true);
		Assert.assertEquals(0, queueInfoDAO.getPendingCount());
		MockJob.JOB_COUNT = 0;
		for (int i = 1; i <= 10; i++) {
			jesqueClient.execute(MockJob.class, new Object[]{});
			Assert.assertEquals(i, queueInfoDAO.getPendingCount());
		}
		jesqueExecutorService.getWorker().togglePause(false);
		waitJob(5000);
	}

	/**
	 *
	 */
	@Test(timeOut = 5000)
	public void shouldProcessJobs() {
		jesqueExecutorService.getWorker().togglePause(true);
		Assert.assertEquals(0, queueInfoDAO.getPendingCount());
		MockJob.JOB_COUNT = 0;
		for (int i = 1; i <= 10; i++) {
			jesqueClient.execute(MockJob.class, new Object[]{});
		}
		jesqueExecutorService.getWorker().togglePause(false);
		waitJob(3000);
		Assert.assertEquals(10,MockJob.JOB_COUNT);
	}

	/**
	 *
	 */
	@Test
	public void shouldAddJobWithArguments() {
		jesqueExecutorService.getWorker().togglePause(true);
		Object[] args = new Object[]{1, 2.3, true, "test", Arrays.asList("inner", 4.5)};
		jesqueClient.execute(MockJobArgs.class, args);
		
	}

	/**
	 *
	 */
	@Test
	public void shouldFailJob() {
		jesqueClient.execute(MockJobFail.class, new Object[]{});
		waitJob(1000);
		Assert.assertEquals(1,failureDAO.getCount());
	}

	/**
	 *
	 * @param milliseconds
	 */
	public void waitJob(long milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException ex) {
		}
	}
}
