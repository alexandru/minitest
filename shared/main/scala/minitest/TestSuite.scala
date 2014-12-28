/*
 * Copyright (c) 2014 by Alexandru Nedelcu. Some rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package minitest

import minitest.api.{Asserts, AbstractTestSuite, Properties, Property}

trait TestSuite[Env] extends AbstractTestSuite with Asserts {
  def setup(): Env
  def tearDown(env: Env): Unit

  def test(name: String)(f: Env => Unit): Unit =
    synchronized {
      if (isInitialized) throw new AssertionError(
        "Cannot define new tests after TestSuite was initialized")
      propertiesSeq = propertiesSeq :+ Property.from(name, f)
    }

  lazy val properties: Properties[_] =
    synchronized {
      if (!isInitialized) isInitialized = true
      Properties(setup, tearDown, propertiesSeq)
    }

  private[this] var propertiesSeq = Seq.empty[Property[Env, Unit]]
  private[this] var isInitialized = false
}
