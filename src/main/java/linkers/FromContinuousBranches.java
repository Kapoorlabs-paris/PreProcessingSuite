package linkers;



import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.OutputAlgorithm;
import utility.PreRoiobject;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class FromContinuousBranches implements OutputAlgorithm< SimpleWeightedGraph< PreRoiobject, DefaultWeightedEdge > >, Benchmark
{

	private static final String BASE_ERROR_MSG = "[FromContinuousBranches] ";

	private long processingTime;

	private final Collection< List< PreRoiobject >> branches;

	private final Collection< List< PreRoiobject >> links;

	private String errorMessage;

	private SimpleWeightedGraph< PreRoiobject, DefaultWeightedEdge > graph;

	public FromContinuousBranches( final Collection< List< PreRoiobject >> branches, final Collection< List< PreRoiobject >> links )
	{
		this.branches = branches;
		this.links = links;
	}

	@Override
	public long getProcessingTime()
	{
		return processingTime;
	}

	@Override
	public boolean checkInput()
	{
		final long start = System.currentTimeMillis();
		if ( null == branches )
		{
			errorMessage = BASE_ERROR_MSG + "branches are null.";
			return false;
		}
		if ( null == links )
		{
			errorMessage = BASE_ERROR_MSG + "links are null.";
			return false;
		}
		for ( final List< PreRoiobject > link : links )
		{
			if ( link.size() != 2 )
			{
				errorMessage = BASE_ERROR_MSG + "A link is not made of two PreRoiobjects.";
				return false;
			}
			if ( !checkIfInBranches( link.get( 0 ) ) )
			{
				errorMessage = BASE_ERROR_MSG + "A PreRoiobject in a link is not present in the branch collection: " + link.get( 0 ) + " in the link " + link.get( 0 ) + "-" + link.get( 1 ) + ".";
				return false;
			}
			if ( !checkIfInBranches( link.get( 1 ) ) )
			{
				errorMessage = BASE_ERROR_MSG + "A PreRoiobject in a link is not present in the branch collection: " + link.get( 1 ) + " in the link " + link.get( 0 ) + "-" + link.get( 1 ) + ".";
				return false;
			}
		}
		final long end = System.currentTimeMillis();
		processingTime = end - start;
		return true;
	}

	@Override
	public boolean process()
	{
		final long start = System.currentTimeMillis();

		graph = new SimpleWeightedGraph< PreRoiobject, DefaultWeightedEdge >( DefaultWeightedEdge.class );
		for ( final List< PreRoiobject > branch : branches )
		{
			for ( final PreRoiobject PreRoiobject : branch )
			{
				graph.addVertex( PreRoiobject );
			}
		}

		for ( final List< PreRoiobject > branch : branches )
		{
			final Iterator< PreRoiobject > it = branch.iterator();
			PreRoiobject previous = it.next();
			while ( it.hasNext() )
			{
				final PreRoiobject PreRoiobject = it.next();
				graph.addEdge( previous, PreRoiobject );
				previous = PreRoiobject;
			}
		}

		for ( final List< PreRoiobject > link : links )
		{
			graph.addEdge( link.get( 0 ), link.get( 1 ) );
		}

		final long end = System.currentTimeMillis();
		processingTime = end - start;
		return true;
	}

	@Override
	public String getErrorMessage()
	{
		return errorMessage;
	}

	@Override
	public SimpleWeightedGraph< PreRoiobject, DefaultWeightedEdge > getResult()
	{
		return graph;
	}

	private final boolean checkIfInBranches( final PreRoiobject PreRoiobject )
	{
		for ( final List< PreRoiobject > branch : branches )
		{
			if ( branch.contains( PreRoiobject ) ) { return true; }
		}
		return false;
	}

}
