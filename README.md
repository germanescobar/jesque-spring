Jesque-Spring
=============
[![Build Status](https://lariverosc.ci.cloudbees.com/job/jesque-spring/badge/icon)](https://lariverosc.ci.cloudbees.com/job/jesque-spring/)

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

##Configure jobs within Spring

The only requirements is that job classes must implement Runnable interface and must be configured in Spring with ```scope="prototype"```, so a job configuration looks like this:

```xml
<bean id="mockJobId" class="net.lariverosc.jesquespring.job.MockJob" scope="prototype"/>
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

##Enqueue Jobs

Using the job ```Class``` type:

```java
Job job = new Job(MockJob.class.getName(), new Object[]{});
jesqueClient.enqueue("QUEUE_NAME", job);

```

Using the job bean ```id``` type:

```java
Job job = new Job("mockJobId", new Object[]{});
jesqueClient.enqueue("QUEUE_NAME", job);

```

##Define a bean for Jesque general configuration:

You need to configure your own parameters:

```xml
<bean id="jesqueConfig" class="net.greghaines.jesque.Config">
	<constructor-arg value="localhost" />
	<constructor-arg value="6379" />
	<constructor-arg value="2000" />
	<constructor-arg value="" />
	<constructor-arg value="resque" />
	<constructor-arg value="0" />
</bean>
```

##Configure the worker within Spring

The Spring integration can be doing in two ways, depending of your needs you can:

###Using the SpringWorker for a single non-pooled thread
You can add more than one ```QUEUE_NAME``` within the set:

```xml
<bean id="worker" class="net.lariverosc.jesquespring.SpringWorker" init-method="init" destroy-method="destroy">
	<constructor-arg name="config" ref="jesqueConfig"/>	
	<constructor-arg name="queues">
		<util:set set-class="java.util.HashSet">
			<value>QUEUE_NAME</value>
		</util:set>
	</constructor-arg> 
</bean>
```

###Using the SpringWorkerFactory for a multi-threading pooled worker
You can add more than one ```QUEUE_NAME``` within the set, and also define the number of workers using the ```numWorkers``` parameter:
```xml
<bean id="workerFactory" class="net.lariverosc.jesquespring.SpringWorkerFactory">
	<constructor-arg name="config" ref="jesqueConfig"/>	
	<constructor-arg name="queues">
		<util:set set-class="java.util.HashSet">
			<value>QUEUE_NAME</value>
		</util:set>
	</constructor-arg> 
</bean>

<bean id="worker" class="net.lariverosc.jesquespring.SpringWorkerPool" init-method="init" destroy-method="destroy">
	<constructor-arg name="workerFactory" ref="workerFactory"/>
	<constructor-arg name="numWorkers" value="1" />			
</bean>

```