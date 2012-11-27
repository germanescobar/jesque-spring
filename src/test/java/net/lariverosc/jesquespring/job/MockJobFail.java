 
package net.lariverosc.jesquespring.job;

/**
 *
 * @author Alejandro Riveros Cruz <lariverosc@gmail.com>
 */
public class MockJobFail implements Runnable{

	@Override
	public void run() {
		throw new RuntimeException("MockJobFail");
	}
	
}
