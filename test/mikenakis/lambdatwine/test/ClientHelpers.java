package mikenakis.lambdatwine.test;

import mikenakis.lambdatwine.test.rig.Alpha;
import mikenakis.lambdatwine.test.rig.FooInterface;

import java.util.Objects;

class ClientHelpers
{
	private ClientHelpers()
	{
	}

	static void runHappyPath( FooInterface fooInterface )
	{
		Alpha alpha1 = new Alpha( "alpha1" );
		Alpha result = fooInterface.theMethod( 1, alpha1 );
		assert result == null;
		Alpha alpha2 = new Alpha( "alpha2" );
		result = fooInterface.theMethod( 1, alpha2 );
		assert Objects.equals( result, alpha1 );
	}
}
