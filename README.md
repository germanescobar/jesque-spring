Jesque-Spring
=============

An extension for Jesque that allows interoperability with Spring the Java framework, the integration was designed thingking in two objectives:

1. Build an implememtation of JesqueClient and JesqueWorker that will be managed by Spring, and available within the Spring context for dependecy injection.
2. Allow the JesqueWorker to execute jobs that are configured as Spring beans. 

Usage
----------------
Download the latest source at:

  https://github.com/lariverosc/jesque-spring
  
Or use it as a Maven dependency within your project:

```xml
<dependency>
  <groupId>net.lariverosc</groupId>
  <artifactId>jesque-spring</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

Spring configuration
----------------

The Spring integration can be doing in two ways, depending of your needs you can:

* Define JesqueClient and JesqueWorker as Spring Beans, that executes Jobs that lives outside the Spring context, to accomplish that you need to use the following configuration:

```xml
<bean id="jesqueConfig" class="net.greghaines.jesque.Config">
	<constructor-arg value="${redis.host}" />
	<constructor-arg value="${redis.port}" />
	<constructor-arg value="${redis.timeout}" />
	<constructor-arg value="${redis.password}" />
	<constructor-arg value="${redis.namespace:resque}" />
	<constructor-arg value="${redis.database:0}" />
</bean>

<bean id="jesqueClient" class="net.lariverosc.jesquespring.JesqueClient">
	<constructor-arg name="config"  ref="jesqueConfig"/>
</bean>

<bean id="workerImplFactory" class="net.greghaines.jesque.worker.WorkerImplFactory">
  <constructor-arg name="config" ref="jesqueConfig"/>  
	<constructor-arg name="queues">
    <util:set set-class="java.util.HashSet">
			<value>JESQUE_QUEUE</value>
		</util:set>
	</constructor-arg>
	<constructor-arg name="jobTypes">
		<util:map map-class="java.util.HashMap">
			<entry>
				<key >
					<value type="java.lang.String">testAction</value>
				</key>
				<value type="java.lang.Class">net.lariverosc.jesquespring.job.MockJob</value>
			</entry>
		</util:map>
	</constructor-arg>
</bean>

<bean id="jesqueWorker" class="net.lariverosc.jesquespring.JesqueWorker" init-method="start" destroy-method="stop">
	<property name="workerImpl" ref="workerImplFactory"/>
	<property name="numWorkers" value="1" />
</bean>
```

* Define JesqueClient and JesqueWorker as Spring Beans, that executes Jobs that lives within the Spring context, to accomplish that you need to change the ```workerImplFactory``` by the following, and also configure the Jobs wthin Spring:

```xml
<bean id="workerImplFactory" class="net.lariverosc.jesquespring.SpringWorkerImplFactory">
  <constructor-arg name="config" ref="jesqueConfig"/>	
	<constructor-arg name="queues">
    <util:set set-class="java.util.HashSet">
			<value>JESQUE_QUEUE</value>
		</util:set>
	</constructor-arg> 
</bean>
```

#Configure jobs within Spring

The only one requirement is that job classes must implement Runnable interface, and be configured in Spring with ```scope="prototype"```, so a job configuration looks like this:

```xml
<bean id="mockJob" class="net.lariverosc.jesquespring.job.MockJob" scope="prototype"/>
```

And the job implementation looks like this:

```java
public class MockJob implements Runnable {
	@Override
	public void run() {
     //do something
	}
}
```