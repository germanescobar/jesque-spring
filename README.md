Jesque-Spring
=============

An extension for Jesque that allows interoperability with Spring the Java framework, the main objective of this extension is build an SpringWorker that allows the execution of jobs that are configured as Spring managed components. 

This extension provide the implemetation of a SpringWorker that performs a lookup within the Spring context to obtain jobs instances, the lookup strategy can found beans either by the bean ```id``` or ```Class``` type.


How do I use it?
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

#Configure jobs within Spring

The only requirements is that job classes must implement Runnable interface and be configured in Spring with ```scope="prototype"```, so a job configuration looks like this:

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

#Spring configuration

The Spring integration can be doing in two ways, depending of your needs you can:

* Define JesqueClient and JesqueWorker as Spring Beans, that executes Jobs that lives outside the Spring context, to accomplish that you need to use the following configuration:

```xml
<bean id="jesqueConfig" class="net.greghaines.jesque.Config">
	<constructor-arg value="localhost" />
	<constructor-arg value="6379" />
	<constructor-arg value="2000" />
	<constructor-arg value="" />
	<constructor-arg value="resque" />
	<constructor-arg value="0" />
</bean>

<bean id="jesqueClient" class="net.greghaines.jesque.client.ClientImpl">
	<constructor-arg name="config"  ref="jesqueConfig"/>
</bean>

<bean id="worker" class="net.lariverosc.jesquespring.SpringWorker" destroy-method="end">
	<constructor-arg name="config" ref="jesqueConfig"/>	
	<constructor-arg name="queues">
		<util:set set-class="java.util.HashSet">
			<value>JESQUE_QUEUE</value>
		</util:set>
	</constructor-arg> 
</bean>

<bean id="workerThread" class="java.lang.Thread" init-method="start" destroy-method="interrupt">
	<constructor-arg ref="worker"/>
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

