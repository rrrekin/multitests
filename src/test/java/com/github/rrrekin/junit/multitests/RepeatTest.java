/*
 * Apache License, Version 2.0
 *
 * You may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*
 * Apache License, Version 2.0
 *
 * You may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.github.rrrekin.junit.multitests;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example usages of @Repeat annotation.
 *
 * @author Michal Rudewicz <michal.rudewicz@gmail.com>
 */
public class RepeatTest {

    private static int counter1 = 0;
    private static int counter2 = 0;
    private static int counter3 = 0;

    @Rule public MultiTestsRule multiTests = new MultiTestsRule();

    @Test
    @Repeat()
    public void test1() throws Exception {
        counter1++;
        assertTrue("Fails for " + counter1, counter1 < 11);
    }

    @Test()
    @Repeat(6) // Will fail for 7th time
    public void test2() throws Exception {
        counter2++;
        assertTrue("Fails for " + counter2, counter2 < 7);
    }

    @Ignore("Will fail")
    @Test
    @Repeat(25)
    public void test3() throws Exception {
        assertFalse("Fails for " + counter3, counter3++ % 7 == 6);
    }
}