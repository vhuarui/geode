/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gemstone.gemfire.security;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.execute.Function;
import com.gemstone.gemfire.cache.execute.FunctionService;
import com.gemstone.gemfire.cache.execute.ResultCollector;
import com.gemstone.gemfire.internal.cache.functions.TestFunction;
import com.gemstone.gemfire.test.junit.categories.DistributedTest;

@Category(DistributedTest.class)
public class IntegratedClientExecuteRegionFunctionAuthDistributedTest extends AbstractIntegratedClientAuthDistributedTest {

  private final static Function function = new TestFunction(true, TestFunction.TEST_FUNCTION1);

  @Test
  public void testExecuteRegionFunction() {

    FunctionService.registerFunction(function);

    client1.invoke("logging in with dataReader", () -> {
      ClientCache cache = createClientCache("dataReader", "1234567", serverPort);

      FunctionService.registerFunction(function);
      assertNotAuthorized(() -> FunctionService.onRegion(cache.getRegion(REGION_NAME))
                                               .withArgs(Boolean.TRUE)
                                               .execute(function.getId()), "DATA:WRITE");
    });

    client2.invoke("logging in with super-user", () -> {
      ClientCache cache = createClientCache("super-user", "1234567", serverPort);

      FunctionService.registerFunction(function);
      ResultCollector rc = FunctionService.onRegion(cache.getRegion(REGION_NAME))
                                          .withArgs(Boolean.TRUE)
                                          .execute(function.getId());
      rc.getResult();
    });
  }
}


