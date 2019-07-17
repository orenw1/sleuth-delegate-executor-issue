/*
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.springframework.cloud.sleuth.instrument.async.issues.issue1212;

import java.util.concurrent.CompletableFuture;

import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.scheduling.DelegatingSecurityContextSchedulingTaskExecutor;

import static org.assertj.core.api.Assertions.assertThat;

public class AsyncExecutorIssueTests {

	@Test
	public void delegateSecurityContextSchedulingTaskExecutorConfig() throws Exception {

		try(ConfigurableApplicationContext ctx = new SpringApplicationBuilder(App.class, DelegateSecurityContextSchedulingTaskExecutorConfig.class)
			.run()) {
			assertThat(ctx.getBean(AsyncComponent.class)
				.asyncMethod()
				.get()).startsWith("delegatingSecurityContextExecutor");
		}
	}

	@SpringBootConfiguration
	@EnableAutoConfiguration
	@EnableAsync
	static class App {

		@Bean
		AsyncComponent asyncComponent() {

			return new AsyncComponent();
		}
	}

	static class AsyncComponent {

		@Async("delegatingSecurityContextSchedulingTaskExecutor")
		public CompletableFuture<String> asyncMethod() {

			LoggerFactory.getLogger(AsyncComponent.class)
				.info("asyncMethod invoked");
			return CompletableFuture.completedFuture(Thread.currentThread()
				.getName());
		}
	}

	/*
	 * Configuration with a single DelegatingSecurityContextSchedulingTaskExecutor
	 */
	@Configuration
	static class DelegateSecurityContextSchedulingTaskExecutorConfig {
		@Bean(name = "delegatingSecurityContextSchedulingTaskExecutor")
		public AsyncTaskExecutor asyncTaskExecutor() {

			ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
			executor.setThreadNamePrefix("delegatingSecurityContextExecutor");
			executor.initialize();
			return new DelegatingSecurityContextSchedulingTaskExecutor(executor);
		}
	}
}
