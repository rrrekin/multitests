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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Example usages of @Parallel annotation.
 *
 * @author Michal Rudewicz <michal.rudewicz@gmail.com>
 */
public class ParallelTest {

    private static final List<String> list1 = new ArrayList<String>();
    private static final List<String> list2 = new CopyOnWriteArrayList<String>();
    private static final List<String> list3 = new CopyOnWriteArrayList<String>();

    @Rule public MultiTestsRule multiTests = new MultiTestsRule();

    @Ignore("Fails with ConcurrentModificationException")
    @Test
    @Parallel(100)
    public void test1() throws Exception {
        final StringBuilder concatenation = new StringBuilder();
        for(final String str: list1){
            concatenation.append(str);
        }
        list1.add("abc");
    }

    @Test()
    @Parallel(100)
    public void test2() throws Exception {
        final StringBuilder concatenation = new StringBuilder();
        for(final String str: list2){
            concatenation.append(str);
        }
        list2.add("abc");
    }

    @Ignore("Will fail due to timeout")
    @Test
    @Parallel(timeout = 14)
    public void test3() throws Exception {
        final StringBuilder concatenation = new StringBuilder();
        for(final String str: list3){
            Thread.sleep(15);
            concatenation.append(str);
        }
        list3.add("abc");
    }
}