package test;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

class Vertex implements Comparable<Vertex>
{
    public final String name;
    public Edge[] adjacencies;
    public double minDistance = Double.POSITIVE_INFINITY;
    public Vertex previous;
    public Vertex(String argName) { name = argName; }
    public String toString() { return name; }
    public int compareTo(Vertex other)
    {
        return Double.compare(minDistance, other.minDistance);
    }
}

class Edge
{
    public final Vertex target;
    public final int delay;
  //  public final int cost;
    public Edge(Vertex argTarget, int argDelay)
    { target = argTarget; delay = argDelay;}
}


public class Dij1 {

	
	
	 public static void computePaths(Vertex source)
	    {
	        source.minDistance = 0.;
	        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
	      	vertexQueue.add(source);

		while (!vertexQueue.isEmpty()) {
		    Vertex u = vertexQueue.poll();

	            // Visit each edge exiting u
	            for (Edge e : u.adjacencies)
	            {
	                Vertex v = e.target;
	                double delay = e.delay;
	                double distanceThroughU = u.minDistance + delay;
			if (distanceThroughU < v.minDistance) {
			    vertexQueue.remove(v);
			    v.minDistance = distanceThroughU ;
			    v.previous = u;
			    vertexQueue.add(v);
			}
	            }
	        }
	    }
	
	 public static List<Vertex> getShortestPathTo(Vertex target)
	    {
	        List<Vertex> path = new ArrayList<Vertex>();
	        for (Vertex vertex = target; vertex != null; vertex = vertex.previous)
	            path.add(vertex);
	        Collections.reverse(path);
	        return path;
	    }
	 
	
	
	public static ArrayList<Integer> ShortestDelayPaths(int n, int m, int source, int sink, int [][] costMatrix, double [][]delayMatrix)
	{
	
		Vertex[] vertices= new Vertex[n];
		
		for(int i=0;i<n; i++)
		{
			vertices[i] = new Vertex(Integer.toString(i));
		}	
			
		
		
		for(int i=0; i< n; i++)
		{
			ArrayList<Edge> edgelist= new ArrayList<Edge>(); 
			
		for(int j=0;j<n; j++)
		{   
			if(delayMatrix[i][j]!= -1)
			{
				edgelist.add(new Edge(vertices[j], (int) delayMatrix[i][j]));
			//	System.out.println("edge weight: " + edgelist.get(j) );
			}
		}
		Edge[] edge1=(Edge[]) edgelist.toArray(new Edge[edgelist.size()]);
		vertices[i].adjacencies=edge1; 
		
		
		}	
			
	/*	v0.adjacencies = new Edge[]{ new Edge(v1, 5),
                new Edge(v2, 10),
              new Edge(v3, 8) };
		*/
		
		
        

computePaths(vertices[source]);

//System.out.println("Distance to " + vertices[sink] + ": " + vertices[sink].minDistance);
List<Vertex> path = getShortestPathTo(vertices[sink]);
//System.out.println("Path: " + path);
 
Vertex[] textfile = new Vertex[path.size()];
textfile = path.toArray(textfile);

int a[]= new int[path.size()];


ArrayList<Integer> shortestpath = new ArrayList<Integer>();

for(int i=0; i<path.size(); i++)
{  
	String text= textfile[i].toString();
	 a[i]= Integer.parseInt(text);
       shortestpath.add(a[i]);
	

}

return shortestpath;
		
	
	}
	
}
