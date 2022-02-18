package mikenakis.lambdatwine.testkit;

import mikenakis.lambdatwine.kit.UncheckedException;

/**
 * "Exception Expected" {@link UncheckedException}.
 *
 * @author michael.gr
 */
public class ExceptionExpectedException extends UncheckedException
{
	public final Class<? extends Throwable> expectedExceptionClass;

	public ExceptionExpectedException( Class<? extends Throwable> expectedExceptionClass )
	{
		this.expectedExceptionClass = expectedExceptionClass;
	}
}
