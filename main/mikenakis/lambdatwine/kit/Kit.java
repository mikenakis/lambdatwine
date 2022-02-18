package mikenakis.lambdatwine.kit;

import mikenakis.lambdatwine.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.lambdatwine.kit.functional.Function1;
import mikenakis.lambdatwine.kit.functional.ThrowingFunction0;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@SuppressWarnings( { "unused", "NewClassNamingConvention" } )
public final class Kit
{
	public static boolean inTry;

	private Kit() { }

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Assertions

	//PEARL: this has been observed to return false even though assertions are enabled, when invoked from a static context, e.g. main()
	public static boolean areAssertionsEnabled()
	{
		boolean b = false;
		//noinspection AssertWithSideEffects
		assert b = true;
		//noinspection ConstantConditions
		return b;
	}

	@ExcludeFromJacocoGeneratedReport public static <T> T fail()
	{
		assert false;
		return null;
	}

	//	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Stacks, Stack Traces, Source code locations

	private static final StackWalker stackWalker = StackWalker.getInstance( EnumSet.noneOf( StackWalker.Option.class ) ); // StackWalker.Option.RETAIN_CLASS_REFERENCE );

	public static StackWalker.StackFrame getStackFrame( int numberOfFramesToSkip )
	{
		assert numberOfFramesToSkip > 0;
		return stackWalker.walk( s -> s.skip( numberOfFramesToSkip + 1 ).limit( 1 ).reduce( null, ( a, b ) -> b ) ); //TODO: simplify
	}

	public static String stringFromThrowable( Throwable throwable )
	{
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter( stringWriter );
		throwable.printStackTrace( printWriter );
		return stringWriter.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// StringBuilder stuff

	public static class stringBuilder
	{
		/**
		 * Optionally appends a delimiter to a {@link StringBuilder}.
		 *
		 * @param builder   the {@link StringBuilder} to append to.
		 * @param first     a {@link boolean} indicating whether this is the first append, and therefore the delimiter should be skipped.
		 * @param delimiter the delimiter to append.
		 *
		 * @return always {@link false}, so that you can do {@code first = appendDelimiter( builder, first, delimiter )}.
		 */
		public static boolean appendDelimiter( StringBuilder builder, boolean first, char delimiter )
		{
			if( !first )
				builder.append( delimiter );
			return false;
		}

		/**
		 * Optionally appends a delimiter to a {@link StringBuilder}.
		 *
		 * @param builder   the {@link StringBuilder} to append to.
		 * @param first     a {@link boolean} indicating whether this is the first append, and therefore the delimiter should be skipped.
		 * @param delimiter the delimiter to append.
		 *
		 * @return always {@link false}, so that you can do {@code first = appendDelimiter( builder, first, delimiter )}.
		 */
		public static boolean appendDelimiter( StringBuilder builder, boolean first, String delimiter )
		{
			if( !first )
				builder.append( delimiter );
			return false;
		}

		public static void appendEscapedForJava( StringBuilder stringBuilder, String s, char quote )
		{
			if( s == null )
			{
				stringBuilder.append( "null" );
				return;
			}
			stringBuilder.append( quote );
			for( char c : s.toCharArray() )
			{
				if( c == '"' )
					stringBuilder.append( "\\\"" );
				else if( c == '\r' )
					stringBuilder.append( "\\r" );
				else if( c == '\n' )
					stringBuilder.append( "\\n" );
				else if( c == '\t' )
					stringBuilder.append( "\\t" );
				else if( c < 32 )
					stringBuilder.append( String.format( "\\x%02x", (int)c ) );
				else if( !Character.isDefined( c ) )
					stringBuilder.append( String.format( "\\u%04x", (int)c ) );
				else
					stringBuilder.append( c );
			}
			stringBuilder.append( quote );
		}

		/**
		 * Appends the string representation of an {@link Object} to a {@link StringBuilder}.
		 * The difference between this function and {@link StringBuilder#append(Object)} is that this function treats {@link String}
		 * differently: strings are output escaped and surrounded with quotes.
		 *
		 * @param stringBuilder the StringBuilder to append to.
		 * @param object        the object whose string representation is to be appended to the StringBuilder.
		 */
		public static void append( StringBuilder stringBuilder, Object object )
		{
			String s = string.of( object );
			stringBuilder.append( s );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// String stuff

	public static class string
	{
		public static String of( Object object )
		{
			if( object == null )
				return "null";
			if( object instanceof String s )
				return escapeForJava( s );
			return object.toString();
		}

		public static String of( String s )
		{
			if( s == null )
				return "null";
			return escapeForJava( s );
		}

		public static String escapeForJava( String s )
		{
			var builder = new StringBuilder();
			stringBuilder.appendEscapedForJava( builder, s, '"' );
			return builder.toString();
		}

		/**
		 * Gets the string representation of an {@link Object}.
		 * see {@link stringBuilder#append(StringBuilder, Object)}
		 *
		 * @param object the object whose string representation is requested.
		 */
		public static String from( Object object )
		{
			StringBuilder stringBuilder = new StringBuilder();
			//noinspection UnnecessarilyQualifiedInnerClassAccess
			Kit.stringBuilder.append( stringBuilder, object );
			return stringBuilder.toString();
		}
	}
//
//	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	// Files and paths

	public static final class path
	{
		// NOTE:  When maven is running tests, the "user.dir" system property contains the root directory of the current module being tested.
		//        When testana is running tests, it sets the "user.dir" property accordingly.
		//        Thus, when running tests either via maven or via testana, we can obtain the path to the root directory of the current module.
		// PEARL: Windows very stupidly has a notion of a "current directory", which is a mutable global variable of process-wide scope.
		//        This means that any thread can modify it, and all other threads will be affected by the modification.
		//        (And if you are in a DotNet process, any AppDomain can modify it, and all other AppDomains will be affected! So much for isolation!)
		//        Java does not exactly have such a notion, but the "user.dir" system property (which you can get and set) is effectively the same.
		public static Path getWorkingDirectory()
		{
			return Paths.get( System.getProperty( "user.dir" ) ).toAbsolutePath().normalize();
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Java Iterable<T>

	public static final class iterable
	{
		public static <T> Iterable<T> fromStream( Stream<T> stream )
		{
			return stream::iterator;
		}

		public static <K, V, T> Map<K,V> toMap( Iterable<T> iterable, Function1<K,T> keyExtractor, Function1<V,T> valueExtractor )
		{
			return collection.stream.fromIterable( iterable ).collect( Collectors.toMap( keyExtractor::invoke, valueExtractor::invoke, Kit::dummyMergeFunction, LinkedHashMap::new ) );
		}
	}

	private static <T> T dummyMergeFunction( T a, T b )
	{
		assert false;
		return a;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Java Collection<T>

	public static final class collection
	{
		public static final class stream
		{
			/***
			 * Obtains a {@link Stream} from an {@link Iterable}.
			 * Because Java makes it awfully difficult, whereas it should have been so easy as to not even require a cast. (Ideally, Stream would extend
			 * Iterable. I know, it can't. But ideally, it would.)
			 */
			public static <T> Stream<T> fromIterable( Iterable<T> iterable )
			{
				return StreamSupport.stream( iterable.spliterator(), false );
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Java Map<K,V>

	public static final class map
	{
		/**
		 * Gets a value by key from a {@link Map}. The key must exist.
		 * Corresponds to Java's {@link Map#get(K)}, except that it corrects Java's deplorable dumbfuckery of not throwing an exception when the key does not
		 * exist.
		 */
		/*@SuppressWarnings( "deprecation" )*/
		public static <K, V> V get( Map<K,V> map, K key )
		{
			assert key != null;
			V value = map.get( key ); //delegation
			assert value != null;
			return value;
		}

		/**
		 * Tries to get a value by key from a {@link Map}. The key may and may not exist.
		 * Corresponds to Java's {@link Map#get(K)}, the difference being that by using this method we are documenting the fact that we are intentionally
		 * allowing the key to potentially not exist and that {@code null} may be returned.
		 */
		/*@SuppressWarnings( "deprecation" )*/
		public static <K, V> V tryGet( Map<K,V> map, K key )
		{
			assert key != null;
			return map.get( key ); //delegation
		}

		/**
		 * Adds a key-value pair to a {@link Map}. The key must not already exist.
		 * Corresponds to Java's {@link Map#put(K, V)}, except that it corrects Java's deplorable dumbfuckery of not throwing an exception when the key already
		 * exists.
		 */
		/*@SuppressWarnings( "deprecation" )*/
		public static <K, V> void add( Map<K,V> map, K key, V value )
		{
			assert key != null;
			assert value != null;
			Object previous = map.put( key, value );
			assert previous == null : key + " " + value;
		}

		/**
		 * Adds or replaces a key-value pair in a {@link Map}. The key may and may not already exist.
		 * Corresponds to Java's {@link Map#put}, the difference being that by using this method we are documenting the fact that we are intentionally
		 * performing this very odd and rarely used operation, allowing the key to either exist or not exist.
		 */
		/*@SuppressWarnings( "deprecation" )*/
		public static <K, V> V addOrReplace( Map<K,V> map, K key, V value )
		{
			assert key != null;
			assert value != null;
			return map.put( key, value );
		}
	}

	/**
	 * <p>Invokes a given {@link ThrowingFunction0} and returns the result, converting the checked exception to unchecked.</p>
	 * <p>The conversion occurs at compilation time, so that:
	 * <ul>
	 *     <li>It does not incur any runtime overhead.</li>
	 *     <li>In the event that an exception is thrown, it does not prevent the debugger from stopping at the throwing statement.</li>
	 * </ul></p>
	 *
	 * @param throwingFunction the {@link ThrowingFunction0} to invoke.
	 * @param <E>              the type of checked exception declared by the {@link ThrowingFunction0}.
	 * @param <R>              the type of the result.
	 */
	public static <R, E extends Exception> R unchecked( ThrowingFunction0<R,E> throwingFunction )
	{
		@SuppressWarnings( "unchecked" ) ThrowingFunction0<R,RuntimeException> f = (ThrowingFunction0<R,RuntimeException>)throwingFunction;
		return f.invoke();
	}

	public interface ThrowableThrowingFunction<R, E extends Throwable>
	{
		R invoke() throws E;
	}

	/**
	 * Invokes a given function declared with {@code throws Throwable}.
	 * <p>
	 * PEARL: just to keep things interesting, Java does not only support checked exceptions, it even allows a method to be declared with
	 * {@code throws Throwable}, in which case the caller is forced to somehow do something about the declared {@code Throwable}, despite the fact
	 * that {@link Throwable} is not a checked exception.
	 * And sure enough, the JDK makes use of this weird feature in at least a few places that I am aware of, e.g. in {@code MethodHandle.invoke()}
	 * and {@code MethodHandle.invokeExact()}.
	 * The following method allows us to invoke methods declared with {@code throws Throwable} without having to handle or in any other way deal
	 * with the {@code Throwable}.
	 *
	 * @param function the {@link ThrowableThrowingFunction} to invoke.
	 * @param <R>      the type of result returned by the function.
	 * @param <E>      the type of throwable declared by the {@link ThrowableThrowingFunction}.
	 *
	 * @return the result of the function.
	 */
	public static <R, E extends Throwable> R invokeThrowableThrowingFunction( ThrowableThrowingFunction<R,E> function )
	{
		@SuppressWarnings( "unchecked" ) ThrowableThrowingFunction<R,RuntimeException> f = (ThrowableThrowingFunction<R,RuntimeException>)function;
		return f.invoke();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * For information about this method, google "Sneaky throw".
	 * <p>
	 * Note: even though the method throws an exception, it is declared to also return an exception.
	 * This allows the caller to use one more `throw` statement, which, although unreachable,
	 * lets the compiler know that that execution will never proceed past that point.
	 */
	@SuppressWarnings( "unchecked" ) public static <T extends Throwable> RuntimeException sneakyException( Throwable t ) throws T
	{
		throw (T)t;
	}

	public static final class classLoading
	{
//		public static Path getPathFromClassLoaderAndTypeName( ClassLoader classLoader, String typeName )
//		{
//			URL url = classLoader.getResource( typeName.replace( '.', '/' ) + ".class" );
//			assert url != null;
//			return getPathFromUrl( url );
//		}

		public static Path getPathFromUrl( URL url )
		{
			URI uri = unchecked( url::toURI );
			String scheme = uri.getScheme();
			if( scheme.equals( "jar" ) )
			{
				FileSystem fileSystem = getOrCreateFileSystem( uri );
				String jarPath = uri.getSchemeSpecificPart();
				int i = jarPath.indexOf( '!' );
				assert i >= 0;
				String subPath = jarPath.substring( i + 1 );
				return fileSystem.getPath( subPath );
			}
			if( scheme.equals( "jrt" ) )
			{
				FileSystem fileSystem = FileSystems.getFileSystem( URI.create( "jrt:/" ) );
				return fileSystem.getPath( "modules", uri.getPath() );
			}
			return Paths.get( uri );
		}

		private static FileSystem getOrCreateFileSystem( URI uri )
		{
			Optional<FileSystem> fileSystem = tryGetFileSystem( uri );
			return fileSystem.orElseGet( () -> newFileSystem( uri ) );
		}

		private static FileSystem newFileSystem( URI uri )
		{
			return unchecked( () -> FileSystems.newFileSystem( uri, Collections.emptyMap() ) );
		}

		private static Optional<FileSystem> tryGetFileSystem( URI uri )
		{
			try
			{
				/**
				 * NOTE: if you see this failing with a {@link FileSystemNotFoundException}
				 * it is probably because you have an exception breakpoint;
				 * just let it run, it will probably recover.
				 */
				FileSystem fileSystem = FileSystems.getFileSystem( uri );
				return Optional.of( fileSystem );
			}
			catch( FileSystemNotFoundException ignore )
			{
				return Optional.empty();
			}
		}
	}
}
