package mikenakis.lambdatwine.test;

import mikenakis.lambdatwine.testkit.TestKit;
import mikenakis.lambdatwine.test.rig.FooInterface;
import mikenakis.lambdatwine.test.rig.FooServer;
import mikenakis.lambdatwine.kit.Kit;
import org.junit.Test;

import java.util.NoSuchElementException;

/**
 * A few preliminary tests to ensure that direct invocations between caller and callee work,
 * before we move on to other tests that introduce lambdatwine between them.
 */
public final class T00_Direct
{
	public T00_Direct()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	@Test
	public void Happy_Path_via_Direct_Call_Works()
	{
		FooInterface fooServer = new FooServer();
		ClientHelpers.runHappyPath( fooServer );
	}

	@Test
	public void Failure_via_Direct_Call_Works()
	{
		FooInterface fooServer = new FooServer();
		TestKit.expect( NoSuchElementException.class, () -> fooServer.theMethod( -1, null ) );
	}
}
