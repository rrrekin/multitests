# multitests
JUnit 4 rule and annotations to multiple test execution: repeat, retry and run in parallel: @Repeat, @Retry and @Parallel respectively.

## Usage

### Declaring dependency
#### Gradle
In `dependencies` section of _build.gradle_ file add test dependency on `junit4` and  `com.github.rrrekin:multitests-junit4:0.7.0`:

```gradle
dependencies {
    compile ......

    testCompile 'junit:junit:4.11'
    testCompile 'com.github.rrrekin:multitests-junit4:0.7.0'
}
```
#### Maven

in the `dependency` section of _pom.xml_ file add dependency on `junit4` and `multitests-junit4`:

```xml
    <dependencies>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.11</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.github.rrrekin</groupId>
            <artifactId>multitests-junit4</artifactId>
            <version>0.7.0</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
```

### Using in test class
Sample usage is included in tests files `RetryTest.java`, `RepeatTest.java` and `Parallel.java`.

To enable annotations following rule has to be added to JUnit4 test class:

```java
    @Rule public MultiTestsRule multiTests = new MultiTestsRule();
```

### @Retry annotation
This annotation can be used for tests that for some reason tend to fail sometimes for a reason outside our control - dependency on external system, network problems, etc.

By default method annotated with this annotation is attempted to execute maximum 3 times. This can be changed by the value provided as an annotation argument.

Examples of usage:

```java
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
```

* `test1` will succeed after retries
* `test2` cannot succeed, so it will fail anyway
* `test3` will succeed, as the number of retries is increased to 10

### @Repeat annotation
This annotation can be used for tests, that sometimes can succeed, and to increase chance of failure, the test should be repeated multiple times.

Examples of usage:
```java
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
```
Tests  1 & 2 above succeed, but will fail if the number of repetition will be increased. Test 3 will fail as the increased number of repetitions will lead to failure.
 
### @Parallel annotation
This annotation can be helpful for testing thread-safety. It causes to start multiple threads (default 10), that are synchronized on cyclic barrier to synchronously start test method code execution in all threads. Test execution is stopped and test is failed when timeout is reached. The default timeout value is 10000 milliseconds.

Examples of usage:
```java
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
```
 
* `test1` fails because `ConcurrentModificationException` is thrown, as ArrayList is not thread-safe
* `test2` succeeds as it uses thread-safe CopyOnWriteArrayList
* `test3` fails as the timeout is set to 14ms, but the test method contains sleep fot 15ms

## Interactions with test runner

It is important to understand how multiple executions are performed.
### Class setup and teardown (@BeforeClass & @AfterClass)
Executed as usual before and after all test methods.

### Test method setup and teardown (@Before & @After)
Executed before and after any repeated method execution, so e.g. for @Repeat(42), both setup and teardown methods will be executed 42 times.

### Static fields
Static fields are behaving as expected - they are initialized once at class startup, and keep changes from methods all the time.

### Non-static fields
Non static fields are behaving a little bit strange when they are not initialized in the @Before methods, but in field declaration. They are initialized once per method, so when they are e.g. set to 0, and then incremented in method annotated with @Repeat, they are incremented to 10 (or other number when non-default number of repetitions is used). But when the field is reinitialized in the @Before method, it is always incremented once.

## Compilation

Clone the source code repository:
```bash
git clone https://github.com/rrrekin/multitests.git
cd multitests
```

Execute gredle wrapper with build task:
```bash
./gradlew build
```

Complied jar file is located in _build/libs/_ directory.

## ChangeLog
[ChangeLog](Changelog.md)

## Compatibility tests
Compatibility test reports can be found [here](compatibilityTesting/reports).