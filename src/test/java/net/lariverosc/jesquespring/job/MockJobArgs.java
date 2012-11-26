package net.lariverosc.jesquespring.job;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Alejandro Riveros Cruz <lariverosc@gmail.com>
 */
public class MockJobArgs implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(MockJobArgs.class);
	private final Integer i;
	private final Double d;
	private final Boolean b;
	private final String s;
	private final List<Object> l;

	public MockJobArgs(final Integer i, final Double d, final Boolean b, final String s, final List<Object> l) {
		this.i = i;
		this.d = d;
		this.b = b;
		this.s = s;
		this.l = l;
	}

	@Override
	public void run() {
		log.info("TestAction.run() {} {} {} {} {}", new Object[]{this.i, this.d, this.b, this.s, this.l});
	}
}
