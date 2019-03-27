# Micro benchmark of caching Statement values

## Summary

We are trying to assess whether caching the statement values has a significant
performance impact.

The simplified scenario is:

1. create a StatementImpl instance (precondition, not part of the throughput
   measurement)
2. access subject, object and predicate 15 times (rough estimation of the number
   of times those are accessed in `Munger.munge()`)

Three scenarios are tested:

1. full calls to `statement.getSubject().stringValue()`
2. caching the different values in a DTO object
3. caching the different values on the stack

The assumption is that the `Munger.munge()` method is called millions of times
over the life of the application, or thousands of times per second in the
updater.

### Notes
* `StatementHelper.statement()` is used to create the statement under test. In
  a real scenario, different subclasses of `Statement` might be used, maybe
  with different performance characteristics.
* The benchmark is somewhat naive, there might be some optimizations done by
  the JVM which would skew the results. Hopefully the relative throughput
  between the different scenarios are still somewhat valid.

## Running the benchmark

    ./mvnw clean package
    java -jar target/benchmarks.jar

The benchmark takes ~25 minutes to run on my machine.

## Results
    Benchmark                          Mode  Cnt    Score   Error  Units
    MyBenchmark.noCacheStatement       avgt   25  190.116 ± 2.796  ns/op
    MyBenchmark.objectCachedStatement  avgt   25  136.234 ± 8.316  ns/op
    MyBenchmark.stringCachedStatement  avgt   25  133.451 ± 2.966  ns/op

## Conclusions

Caching values on the stack or on the heap provides roughly the same ~30%
improvement (or 60ns per operation). While this looks like a generous
improvement, under the initial assumption of 1000 operations per second, this
means 60 microseconds per second of maximum gain, or 0.006%. Over a million
operations, the maximum gain is ~60 milliseconds.

So in context, this optimization does not make any significant difference.

## Logs

Full log of the benchmark run:

```
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by org.openjdk.jmh.util.Utils (file:/home/gehel/wikimedia/query/rdf-jmh/target/benchmarks.jar) to field java.io.Console.cs
WARNING: Please consider reporting this to the maintainers of org.openjdk.jmh.util.Utils
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
# JMH version: 1.21
# VM version: JDK 11.0.1, OpenJDK 64-Bit Server VM, 11.0.1+13-Ubuntu-3ubuntu3.18.10.1
# VM invoker: /usr/lib/jvm/java-11-openjdk-amd64/bin/java
# VM options: <none>
# Warmup: 5 iterations, 10 s each
# Measurement: 5 iterations, 10 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: org.wikimedia.query.rdf.MyBenchmark.noCacheStatement

# Run progress: 0.00% complete, ETA 00:25:00
# Fork: 1 of 5
# Warmup Iteration   1: 201.484 ns/op
# Warmup Iteration   2: 198.075 ns/op
# Warmup Iteration   3: 186.303 ns/op
# Warmup Iteration   4: 191.023 ns/op
# Warmup Iteration   5: 187.725 ns/op
Iteration   1: 186.779 ns/op
Iteration   2: 193.934 ns/op
Iteration   3: 194.913 ns/op
Iteration   4: 190.808 ns/op
Iteration   5: 190.099 ns/op

# Run progress: 6.67% complete, ETA 00:23:25
# Fork: 2 of 5
# Warmup Iteration   1: 200.251 ns/op
# Warmup Iteration   2: 201.135 ns/op
# Warmup Iteration   3: 190.309 ns/op
# Warmup Iteration   4: 199.043 ns/op
# Warmup Iteration   5: 193.386 ns/op
Iteration   1: 184.599 ns/op
Iteration   2: 194.389 ns/op
Iteration   3: 193.269 ns/op
Iteration   4: 191.213 ns/op
Iteration   5: 186.512 ns/op

# Run progress: 13.33% complete, ETA 00:21:44
# Fork: 3 of 5
# Warmup Iteration   1: 198.725 ns/op
# Warmup Iteration   2: 196.153 ns/op
# Warmup Iteration   3: 188.145 ns/op
# Warmup Iteration   4: 185.166 ns/op
# Warmup Iteration   5: 187.755 ns/op
Iteration   1: 189.199 ns/op
Iteration   2: 188.135 ns/op
Iteration   3: 187.436 ns/op
Iteration   4: 186.333 ns/op
Iteration   5: 184.349 ns/op

# Run progress: 20.00% complete, ETA 00:20:04
# Fork: 4 of 5
# Warmup Iteration   1: 199.099 ns/op
# Warmup Iteration   2: 199.986 ns/op
# Warmup Iteration   3: 197.181 ns/op
# Warmup Iteration   4: 193.740 ns/op
# Warmup Iteration   5: 192.557 ns/op
Iteration   1: 191.780 ns/op
Iteration   2: 194.551 ns/op
Iteration   3: 195.791 ns/op
Iteration   4: 192.162 ns/op
Iteration   5: 190.459 ns/op

# Run progress: 26.67% complete, ETA 00:18:23
# Fork: 5 of 5
# Warmup Iteration   1: 194.891 ns/op
# Warmup Iteration   2: 195.328 ns/op
# Warmup Iteration   3: 188.097 ns/op
# Warmup Iteration   4: 186.060 ns/op
# Warmup Iteration   5: 186.411 ns/op
Iteration   1: 187.296 ns/op
Iteration   2: 196.257 ns/op
Iteration   3: 191.802 ns/op
Iteration   4: 184.625 ns/op
Iteration   5: 186.204 ns/op


Result "org.wikimedia.query.rdf.MyBenchmark.noCacheStatement":
  190.116 ±(99.9%) 2.796 ns/op [Average]
  (min, avg, max) = (184.349, 190.116, 196.257), stdev = 3.733
  CI (99.9%): [187.319, 192.912] (assumes normal distribution)


# JMH version: 1.21
# VM version: JDK 11.0.1, OpenJDK 64-Bit Server VM, 11.0.1+13-Ubuntu-3ubuntu3.18.10.1
# VM invoker: /usr/lib/jvm/java-11-openjdk-amd64/bin/java
# VM options: <none>
# Warmup: 5 iterations, 10 s each
# Measurement: 5 iterations, 10 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: org.wikimedia.query.rdf.MyBenchmark.objectCachedStatement

# Run progress: 33.33% complete, ETA 00:16:43
# Fork: 1 of 5
# Warmup Iteration   1: 156.458 ns/op
# Warmup Iteration   2: 162.210 ns/op
# Warmup Iteration   3: 154.155 ns/op
# Warmup Iteration   4: 151.771 ns/op
# Warmup Iteration   5: 164.234 ns/op
Iteration   1: 163.366 ns/op
Iteration   2: 163.827 ns/op
Iteration   3: 158.071 ns/op
Iteration   4: 151.452 ns/op
Iteration   5: 146.788 ns/op

# Run progress: 40.00% complete, ETA 00:15:03
# Fork: 2 of 5
# Warmup Iteration   1: 148.199 ns/op
# Warmup Iteration   2: 136.181 ns/op
# Warmup Iteration   3: 131.082 ns/op
# Warmup Iteration   4: 129.804 ns/op
# Warmup Iteration   5: 129.830 ns/op
Iteration   1: 130.148 ns/op
Iteration   2: 129.972 ns/op
Iteration   3: 129.518 ns/op
Iteration   4: 130.101 ns/op
Iteration   5: 130.205 ns/op

# Run progress: 46.67% complete, ETA 00:13:22
# Fork: 3 of 5
# Warmup Iteration   1: 136.188 ns/op
# Warmup Iteration   2: 135.737 ns/op
# Warmup Iteration   3: 130.135 ns/op
# Warmup Iteration   4: 129.978 ns/op
# Warmup Iteration   5: 130.297 ns/op
Iteration   1: 129.215 ns/op
Iteration   2: 130.858 ns/op
Iteration   3: 129.209 ns/op
Iteration   4: 130.181 ns/op
Iteration   5: 130.202 ns/op

# Run progress: 53.33% complete, ETA 00:11:42
# Fork: 4 of 5
# Warmup Iteration   1: 135.390 ns/op
# Warmup Iteration   2: 135.894 ns/op
# Warmup Iteration   3: 130.346 ns/op
# Warmup Iteration   4: 129.187 ns/op
# Warmup Iteration   5: 130.889 ns/op
Iteration   1: 130.563 ns/op
Iteration   2: 129.778 ns/op
Iteration   3: 129.332 ns/op
Iteration   4: 130.192 ns/op
Iteration   5: 129.923 ns/op

# Run progress: 60.00% complete, ETA 00:10:01
# Fork: 5 of 5
# Warmup Iteration   1: 136.918 ns/op
# Warmup Iteration   2: 137.530 ns/op
# Warmup Iteration   3: 130.628 ns/op
# Warmup Iteration   4: 130.421 ns/op
# Warmup Iteration   5: 130.597 ns/op
Iteration   1: 131.254 ns/op
Iteration   2: 133.387 ns/op
Iteration   3: 133.488 ns/op
Iteration   4: 138.716 ns/op
Iteration   5: 136.096 ns/op


Result "org.wikimedia.query.rdf.MyBenchmark.objectCachedStatement":
  136.234 ±(99.9%) 8.316 ns/op [Average]
  (min, avg, max) = (129.209, 136.234, 163.827), stdev = 11.101
  CI (99.9%): [127.918, 144.549] (assumes normal distribution)


# JMH version: 1.21
# VM version: JDK 11.0.1, OpenJDK 64-Bit Server VM, 11.0.1+13-Ubuntu-3ubuntu3.18.10.1
# VM invoker: /usr/lib/jvm/java-11-openjdk-amd64/bin/java
# VM options: <none>
# Warmup: 5 iterations, 10 s each
# Measurement: 5 iterations, 10 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: org.wikimedia.query.rdf.MyBenchmark.stringCachedStatement

# Run progress: 66.67% complete, ETA 00:08:21
# Fork: 1 of 5
# Warmup Iteration   1: 144.385 ns/op
# Warmup Iteration   2: 143.904 ns/op
# Warmup Iteration   3: 135.712 ns/op
# Warmup Iteration   4: 137.358 ns/op
# Warmup Iteration   5: 134.562 ns/op
Iteration   1: 132.226 ns/op
Iteration   2: 131.630 ns/op
Iteration   3: 132.178 ns/op
Iteration   4: 135.942 ns/op
Iteration   5: 137.623 ns/op

# Run progress: 73.33% complete, ETA 00:06:41
# Fork: 2 of 5
# Warmup Iteration   1: 142.912 ns/op
# Warmup Iteration   2: 138.336 ns/op
# Warmup Iteration   3: 136.047 ns/op
# Warmup Iteration   4: 132.253 ns/op
# Warmup Iteration   5: 131.497 ns/op
Iteration   1: 131.979 ns/op
Iteration   2: 131.413 ns/op
Iteration   3: 131.285 ns/op
Iteration   4: 142.677 ns/op
Iteration   5: 142.844 ns/op

# Run progress: 80.00% complete, ETA 00:05:00
# Fork: 3 of 5
# Warmup Iteration   1: 140.330 ns/op
# Warmup Iteration   2: 142.897 ns/op
# Warmup Iteration   3: 133.866 ns/op
# Warmup Iteration   4: 135.779 ns/op
# Warmup Iteration   5: 134.866 ns/op
Iteration   1: 135.608 ns/op
Iteration   2: 136.824 ns/op
Iteration   3: 134.363 ns/op
Iteration   4: 137.811 ns/op
Iteration   5: 138.398 ns/op

# Run progress: 86.67% complete, ETA 00:03:20
# Fork: 4 of 5
# Warmup Iteration   1: 142.608 ns/op
# Warmup Iteration   2: 139.939 ns/op
# Warmup Iteration   3: 131.470 ns/op
# Warmup Iteration   4: 130.997 ns/op
# Warmup Iteration   5: 130.453 ns/op
Iteration   1: 130.794 ns/op
Iteration   2: 130.816 ns/op
Iteration   3: 131.007 ns/op
Iteration   4: 131.022 ns/op
Iteration   5: 131.267 ns/op

# Run progress: 93.33% complete, ETA 00:01:40
# Fork: 5 of 5
# Warmup Iteration   1: 135.295 ns/op
# Warmup Iteration   2: 135.781 ns/op
# Warmup Iteration   3: 129.748 ns/op
# Warmup Iteration   4: 129.584 ns/op
# Warmup Iteration   5: 129.275 ns/op
Iteration   1: 129.594 ns/op
Iteration   2: 129.750 ns/op
Iteration   3: 130.360 ns/op
Iteration   4: 129.851 ns/op
Iteration   5: 129.014 ns/op


Result "org.wikimedia.query.rdf.MyBenchmark.stringCachedStatement":
  133.451 ±(99.9%) 2.966 ns/op [Average]
  (min, avg, max) = (129.014, 133.451, 142.844), stdev = 3.959
  CI (99.9%): [130.485, 136.417] (assumes normal distribution)


# Run complete. Total time: 00:25:04

REMEMBER: The numbers below are just data. To gain reusable insights, you need to follow up on
why the numbers are the way they are. Use profilers (see -prof, -lprof), design factorial
experiments, perform baseline and negative tests that provide experimental control, make sure
the benchmarking environment is safe on JVM/OS/HW level, ask for reviews from the domain experts.
Do not assume the numbers tell you what you want them to tell.

Benchmark                          Mode  Cnt    Score   Error  Units
MyBenchmark.noCacheStatement       avgt   25  190.116 ± 2.796  ns/op
MyBenchmark.objectCachedStatement  avgt   25  136.234 ± 8.316  ns/op
MyBenchmark.stringCachedStatement  avgt   25  133.451 ± 2.966  ns/op
```

## Links

* [JMH](https://openjdk.java.net/projects/code-tools/jmh/): the micro
  benchmarking tool used for the test
* [Tutorial on JMH](http://tutorials.jenkov.com/java-performance/jmh.html)
