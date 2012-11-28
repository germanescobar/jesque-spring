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
##Define a bean for Jesque general config:

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
<bean id="worker" class="net.lariverosc.jesquespring.SpringWorker" destroy-method="end">
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

<bean id="worker" class="net.greghaines.jesque.worker.WorkerPool" destroy-method="end">
	<constructor-arg name="workerFactory" ref="workerFactory"/>
	<constructor-arg name="numWorkers" value="1" />			
</bean>

```

##Start the workerThread

You can start the ```workerThread``` within the Spring context adding the following bean definition::

```xml
<bean id="workerThread" class="java.lang.Thread" init-method="start" destroy-method="interrupt">
	<constructor-arg ref="worker"/>
</bean>
```

Or you can start the Thread programmatically making something like:

```java
Worker worker = (Worker) springApplicationContext.getBean("worker");
Thread thread = new Thread(worker);
thread.start();
```
##Optional Spring configuration

###Define a JesqueClient within the Spring context
```xml
<bean id="jesqueClient" class="net.greghaines.jesque.client.ClientImpl">
	<constructor-arg name="config"  ref="jesqueConfig"/>
</bean>
```



