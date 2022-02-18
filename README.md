# mikenakis/lambdatwine

A facility for converting back and forth between any single-method-interface (henceforth also called 'lambda' for brevity) and the _Normal Form of Lambdas_, which is a single-method interface of the form `Object anylambda( Object[] arguments )`.

## Description
                                                                                                                   
Any conceivable single-method interface (lambda) can be described using the following universal single-method interface:

    interface Anylambda
    {
        Object anylambda( Object[] arguments );
    }

We call this the _Normal Form_ of lambdas.
- The arguments of the lambda are represented as an array of `Object`.
- The return value is represented as an `Object`. If the method is of `void` return type then the value returned by `anylambda` is unspecified. (It will in all likelihood be `null`, but nobody should rely on this.)   

For any lambda `T` it is possible to have an **_Entwiner_** and an **_Untwiner_**.
- The entwiner of single-method interface `T` is an object that implements `T` by delegating to an instance of `Anylambda`.
- The untwiner of single-method interface `T` is an object that implements `Anylambda` by delegating to an instance of `T`.

More specifically, the entwiner of `T` does the following:
- Accepts an instance of `Anylambda` as a constructor parameter and stores it in `final` field `exitPoint`.
- Implements the single method of `T` as follows:
  - Packs the parameters that were passed to the method into an array of `Object`, performing any boxing necessary.
  - Invokes `exitPoint`, passing it the array of parameters.
  - Returns, possibly after unboxing, whatever was returned by the invocation of `exitPoint`.

The untwiner of `T` performs the opposite and complementary operation of the entwiner, namely:
- Accepts an instance of `T` as a constructor parameter and stores it in `final` field `exitPoint`.
- Implements the `anylambda` method of the `Anylambda` interface as follows:
  - Unpacks the parameters from the array of `Object`, performing any unboxing necessary.
  - Invokes `exitPoint`, passing it the unpacked parameters.
  - Returns, possibly after boxing, whatever was returned by the method, or `null` if the method was of `void` return type.

Writing entwiners and untwiners by hand is a tedious and error-prone task; ***mikenakis/lambdatwine*** is a facility that automates the creation of _Entwiners_ and _Untwiners_ for any single-method interface.

## How it works

- The entry-point is the `LambdatwineFactory` interface.

- You give `LambdatwineFactory` the class of a single-method interface, and it returns a `Lambdatwine` for that single-method interface.

- Once you have a `Lambdatwine` for an interface, you can call it to create entwiners and untwiners for that interface.

- To create an entwiner of `T`, you supply an instance of `Anylambda` and you receive an instane of `T`. From that moment on, when you invoke that `T`, it will result in your supplied `Anylambda` being invoked.

- To create an untwiner of `T`, you supply an instance of `T` and you receive an instance of `Anylambda`. From that moment on, when you invoke that `Anylambda`, it will result in your supplied `T` being invoked.


## Implementations

3 implementations of the `LambdatwineFactory` interface are provided:

1. Reflecting
   - Uses reflection to accomplish their job.
2. Methodhandle
   - Uses JVM method handles to accomplish the job.
3. Caching
   - This is a decorator of the `LambdatwineFactory` interface that caches the `Lambdatwine` of each single-method interface that it sees, so as to avoid ever having to recreate the `Lambdatwine` for that interface again.

## Benchmark

A benchmark is included in the project, in the `test/mikenakis/lambdatwine/benchmark/LambdatwineBenchmark` class, which is runnable.

This benchmark compares the performance of:
1. Direct method invocation
2. The reflecting implementation of lambdatwine
3. The methodhandle-based implementation of lambdatwine

On my machine it produces the following output:

(3 runs are performed to showcase the repeatability of the measurements)

```
Direct                    : 100000 iterations   0.1506s
Reflecting Lambdatwine    : 100000 iterations   1.6382s
MethodHandle Lambdatwine  : 100000 iterations   1.3696s

Direct                    : 100000 iterations   0.1425s
Reflecting Lambdatwine    : 100000 iterations   1.6260s
MethodHandle Lambdatwine  : 100000 iterations   1.3619s

Direct                    : 100000 iterations   0.1401s
Reflecting Lambdatwine    : 100000 iterations   1.5716s
MethodHandle Lambdatwine  : 100000 iterations   1.3654s
```

Note that:
- The methodhandle implementation is just slightly faster than the reflecting implementation.
- Of course, both implementations are about one order of mangitude slower than direct invocation.

## State of the project
                       
This project works, but it is of very limited usefulness due to the following reasons:
1. When an entwiner or untwiner invokes client code, and the client code throws, the exception will be caught and wrapped in an `InvocationTargetException`, thus preventing the debugger from stopping at the original `throw` statement.  This extremely undesirable and utterly retarded behavior is courtesy of the JVM, it is not the fault of **mikenakis/lambdatwine**.
2. It is always possible to achieve the same functionality (and without the JVM messing with your exceptions) using general purpose generic functions as follows:
   - `Function0<R>`
   - `Function1<R,P0>`
   - `Function2<R,P0,P1>`
   - `Function3<R,P0,P1,P2>`

     ...etc
2. **mikenakis/lambdatwine** has been supreceded by ***mikenakis/intertwine***, which:
    - Works with any interface, not just single-method interfaces.
    - Does not mess with your exceptions.

## License

This creative work is explicitly published under ***No License***. This means that I remain the exclusive copyright holder of this creative work, and you may not do anything with it other than view its source code and admire it. More information here: [michael.gr - Open Source but No License.](https://blog.michael.gr/2018/04/open-source-but-no-license.html)

If you would like to do anything more with this creative work, contact me.

## Coding style

When I write code as part of a team of developers, I use the teams' coding style.  
However, when I write code for myself, I use _**my very own™**_ coding style.

More information: [michael.gr - On Coding Style](https://blog.michael.gr/2018/04/on-coding-style.html)
