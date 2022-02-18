package mikenakis.lambdatwine.kit;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Base class for unchecked exceptions. (Which are the only kind of exceptions that I use by my own free will.)
 * Disallows the setting of a human-readable message, builds the human-readable message using reflection.
 * Disallows overriding getMessage() and toString() to prevent misuse.
 * <p>
 * TODO: make more use of!
 *
 * @author michael.gr
 */
public class UncheckedException extends RuntimeException
{
	public static UncheckedException create()
	{
		return new UncheckedException();
	}

	protected UncheckedException()
	{
	}

	protected UncheckedException( Optional<? extends Throwable> cause )
	{
		super( cause.orElse( null ) );
	}

	protected UncheckedException( Throwable cause )
	{
		super( cause );
	}

	@Override public final String getMessage()
	{
		//NOTE: we are not adding the message of the base class because we have not set a value to it, so super.getMessage() would return the message of the
		// cause exception, which is useless, since there will be a "caused by" message anyway.
		return Arrays.stream( getClass().getFields() ) //
			.filter( field -> UncheckedException.class.isAssignableFrom( field.getDeclaringClass() ) ) //
			.map( field -> field.getName() + "=" + fieldValueToString( field ) ) //
			.collect( Collectors.joining( "; " ) );
	}

	private String fieldValueToString( Field field )
	{
		try
		{
			Object value = field.get( this );
			return Kit.string.from( value );
		}
		catch( IllegalAccessException ignore )
		{
			return "<inaccessible>";
		}
	}

	@SuppressWarnings( "EmptyMethod" ) @Override public final String toString()
	{
		return super.toString();
	}
}
