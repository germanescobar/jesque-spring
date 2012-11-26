package net.lariverosc.jesquespring;

import java.util.Arrays;
import net.lariverosc.jesquespring.job.MockJob;
import net.lariverosc.jesquespring.job.MockJobArgs;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Alejandro Riveros Cruz <lariverosc@gmail.com>
 */
public class JesqueSpringTest {

	JesqueClient jesqueClient;

	@BeforeClass
	public void setUp() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:jesque-context.xml", "classpath:test-context.xml");
		jesqueClient = (JesqueClient) applicationContext.getBean("jesqueClient");
	}

	@Test
	public void shouldAddJob() {
		jesqueClient.execute(MockJob.class, new Object[]{});
		waitJob(5000);
	}

	@Test
	public void shouldAddJobWithArguments() {
		jesqueClient.execute(MockJobArgs.class, new Object[]{1, 2.3, true, "test", Arrays.asList("inner", 4.5)});
		waitJob(5000);
	}

	public void waitJob(long milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException ex) {
		}
	}
}
