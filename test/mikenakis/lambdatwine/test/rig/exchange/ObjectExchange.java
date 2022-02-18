package mikenakis.lambdatwine.test.rig.exchange;

public interface ObjectExchange<P, Q>
{
	P doExchange( Q request );
}
