# DelegatingSecurityContextExecutor NPE when using Spring cloud sleuth Greenwich.SR2

Using DelegatingSecurityContextSchedulingTaskExecutor for `@Async` handling
causing NPE, as the method `createAsyncTaskExecutorProxy` of
`org.springframework.cloud.sleuth.instrument.async.ExecutorBeanPostProcessor`
return proxied object that is missing the delegate Executor field of
`DelegatingSecurityContextExecutor`.

See [ExecutorBeanPostProcessor line 198](https://github.com/spring-cloud/spring-cloud-sleuth/blob/v2.1.2.RELEASE/spring-cloud-sleuth-core/src/main/java/org/springframework/cloud/sleuth/instrument/async/ExecutorBeanPostProcessor.java#L198)

This is similar to [#1212](https://github.com/spring-cloud/spring-cloud-sleuth/issues/1212)
