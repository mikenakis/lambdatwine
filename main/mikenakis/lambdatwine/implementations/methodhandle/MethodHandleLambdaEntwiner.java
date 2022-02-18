package mikenakis.lambdatwine.implementations.methodhandle;

import mikenakis.lambdatwine.AnyLambda;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

final class MethodHandleLambdaEntwiner<T>
{
	private final MethodHandleLambdatwine<T> lambdatwine;
	private final AnyLambda<T> exitPoint;
	final T entryPoint;

	MethodHandleLambdaEntwiner( MethodHandleLambdatwine<T> lambdatwine, AnyLambda<T> exitPoint )
	{
		assert lambdatwine != null;
		assert exitPoint != null;
		this.lambdatwine = lambdatwine;
		this.exitPoint = exitPoint;
		ClassLoader classLoader = lambdatwine.interfaceType().getClassLoader();
		Class<?>[] interfaceTypes = { lambdatwine.interfaceType() };
		Object proxy = Proxy.newProxyInstance( classLoader, interfaceTypes, this::invocationHandler );
		@SuppressWarnings( "unchecked" ) T temp = (T)proxy;
		entryPoint = temp;
	}

	private Object invocationHandler( Object proxy, Method method, Object[] arguments )
	{
		assert proxy == entryPoint;
		assert method.equals( lambdatwine.method );
		return exitPoint.anyLambda( arguments );
	}
}
