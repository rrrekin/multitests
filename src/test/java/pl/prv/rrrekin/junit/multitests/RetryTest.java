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

package pl.prv.rrrekin.junit.multitests;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Example usages of @Retry annotation.
 *
 * @author Michal Rudewicz <michal.rudewicz@gmail.com>
 */
public class RetryTest {

    private static int counter1 = 0;
    private static int counter3 = 0;

    @Rule public MultiTestsRule multiTests = new MultiTestsRule();

    @Test
    @Retry()
    public void test1() throws Exception {
        assertTrue(counter1++ % 3 == 2);
    }

    @Ignore("Will fail anyway")
    @Test()
    @Retry(7)
    public void test2() throws Exception {
        assertTrue(false);
    }

    @Test
    @Retry(10)
    public void test3() throws Exception {
        assertTrue(counter3++ % 7 == 0);
    }
}