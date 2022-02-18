# mikenakis/lambdatwine

A framework for converting back and forth between any single-method-interface (henceforth also called 'lambda' for brevity) and the _Normal Form of Lambdas_, which is a single-method interface of the form `Object anylambda( Object[] arguments )`.

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

Writing entwiners and untwiners by hand is a tedious and error-prone task; *mikenakis/lambdatwine* is a framework that uses bytecode generation to automatically create _Entwiners_ and _Untwiners_ for any single-method interface.

## State of the project
                       
This project works, but it suffers from one notable drawback:

When an entwiner or untwiner invokes client code, and the client code throws, the exception will be caught and wrapped in an 'InvocationTargetException', thus preventing the debugger from stopping at the original `throw` statement.  This extremely undesirable and utterly retarded behavior is not the fault of mikenakis/lambdatwine, it exists courtesy of the "MethodHandle" facility which is part of the JVM.

This project is of very limited usefulness since there exists mikenakis/intertwine, which:
- Works with any interface, not just single-method interfaces
- Does not mess with exceptions.

## License

This creative work is explicitly published under ***No License***. This means that I remain the exclusive copyright holder of this creative work, and you may not do anything with it other than view its source code and admire it. More information here: [michael.gr - Open Source but No License.](https://blog.michael.gr/2018/04/open-source-but-no-license.html)

If you would like to do anything more with this creative work, contact me.

## Coding style

When I write code as part of a team of developers, I use the teams' coding style.  
However, when I write code for myself, I use _**my very own™**_ coding style.

More information: [michael.gr - On Coding Style](https://blog.michael.gr/2018/04/on-coding-style.html)
