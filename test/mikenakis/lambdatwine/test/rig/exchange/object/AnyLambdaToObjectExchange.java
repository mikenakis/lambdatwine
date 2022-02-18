package mikenakis.lambdatwine.test.rig.exchange.object;

import mikenakis.lambdatwine.test.rig.exchange.ObjectExchange;
import mikenakis.lambdatwine.kit.Kit;
import mikenakis.lambdatwine.AnyLambda;

public class AnyLambdaToObjectExchange<T> implements AnyLambda<T>
{
	private final ObjectExchange<AnyLambdaResponse,AnyLambdaRequest> objectExchange;

	public AnyLambdaToObjectExchange( ObjectExchange<AnyLambdaResponse,AnyLambdaRequest> objectExchange )
	{
		this.objectExchange = objectExchange;
	}

	@Override public Object anyLambda( Object[] arguments )
	{
		AnyLambdaRequest anyLambdaRequest = new AnyLambdaRequest( arguments );
		AnyLambdaResponse anyLambdaResponse = objectExchange.doExchange( anyLambdaRequest );
		if( !anyLambdaResponse.success )
			throw Kit.sneakyException( anyLambdaResponse.throwable() );
		return anyLambdaResponse.payload();
	}
}
