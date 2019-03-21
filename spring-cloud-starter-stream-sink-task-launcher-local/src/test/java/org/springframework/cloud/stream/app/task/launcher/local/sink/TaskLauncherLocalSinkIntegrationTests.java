/*
 * Copyright 2017-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.app.task.launcher.local.sink;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.deployer.spi.task.LaunchState;
import org.springframework.cloud.stream.app.task.launcher.local.sink.configuration.TaskSinkConfiguration;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.task.launcher.TaskLaunchRequest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author Glenn Renfro
 * @author Thomas Risberg
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext
public class TaskLauncherLocalSinkIntegrationTests {

	@Autowired
	ConfigurableApplicationContext applicationContext;


	@Autowired
	private Sink sink;


	@Test(expected = MessageHandlingException.class)
	public void sendBadRequest() throws IOException {
		TaskLaunchRequest request = new TaskLaunchRequest("maven://foo", null, null, null, "test");
		sink.input().send(new GenericMessage<>(request));
	}

	@Test
	public void sendRequest() throws IOException {
		TaskSinkConfiguration.TestTaskLauncher testTaskLauncher =
				applicationContext.getBean(TaskSinkConfiguration.TestTaskLauncher.class);

		TaskLaunchRequest request = new TaskLaunchRequest("maven://org.springframework.cloud.task.app:timestamp-task:jar:1.2.0.RELEASE", null, null, null, "test");
		sink.input().send(new GenericMessage<>(request));
		assertThat(testTaskLauncher.status("TESTSTATUS").getState()).isEqualTo(LaunchState.complete);
	}

	@SpringBootApplication
	static class TaskLauncherSinkApplication {
		public static void main(String[] args) {
			SpringApplication.run(TaskLauncherSinkApplication.class, args);
		}
	}
}
