package net.lariverosc.jesquespring;

import java.net.URI;
import java.net.URISyntaxException;
import net.greghaines.jesque.Config;
import net.greghaines.jesque.ConfigBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Protocol;

/**
 * A custom factory of Jesque Config used from the Spring configuration file.
 *
 * @author Alejandro Riveros Cruz <lariverosc@gmail.com>
 */
public class JesqueConfigFactory {

	private Logger logger = LoggerFactory.getLogger(JesqueConfigFactory.class);
	private String uri;

	public Config create() throws URISyntaxException {
		if ("localhost".equalsIgnoreCase(uri)) {
			return ConfigBuilder.getDefaultConfig();
		} else {
			URI redisUri = new URI(uri);
			return new Config(redisUri.getHost(), redisUri.getPort(), Protocol.DEFAULT_TIMEOUT, redisUri.getUserInfo().split(":")[1], "resque", 0);
		}
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
}
