package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;


 
class edge {
	int src = 0;
	int dst = 0;
	int cap = 0;
	int delay = 0;
	double flow = 0;
	int t = 0;
	int id;
	double priceOnOrig = 0;
	double FlowOnOrig = 0;
	Vector<path> pathsUsingEdge = new Vector<path>();

	public edge(int sr, int des, int cp, int ti, int idd) {
		src = sr;
		dst = des;
		cap = cp;
		t = ti;
		id = idd;

	}
	
	

	double FlowOnEdge() {
		double flow = 0;
		for (path element : pathsUsingEdge) {
			flow = flow + element.flow;

		}
		return flow;
	}

	double priceOnOrigEdge() {
		double price = 0;
		double epsilon = 0.5;
		double delta = 2;
		price = (FlowOnOrig - cap + epsilon) / (Math.pow(epsilon, delta));
	//	System.out.println("In edge="+price+"\t FlowonOrig="+FlowOnOrig+"\t Cap="+cap);
		if (price < 0)
			price = 0;
		return price;
	}

}

class path {
	double flow = 0;
	Vector<edge> seq = new Vector<edge>();
	int source;
	int dest;

	public static path convertSPathtofpath(ArrayList PathfromBF, graph G) {
		path p = new path();
		if(PathfromBF!=null)
		for (int i = 0; i < PathfromBF.size() - 1; i++) {
			int src = (Integer) PathfromBF.get(i);
			int dest = (Integer) PathfromBF.get(i + 1);

			p.seq.add(G.tGraph[src][dest]);
		}
		return p;

	}
	
	public static boolean pathcheck(Vector<path> fpath,path npath){
	//return false if path not found
	
		boolean found1=true;
	for (int i = 0; i < fpath.size(); i++) {
		path ipath = fpath.get(i);
		if (ipath.seq.size() == npath.seq.size())
			for (int j = 0; j < ipath.seq.size(); j++) {
				if (ipath.seq.get(j).id != npath.seq.get(j).id)
					found1 = false;
				break;
			}
		
	}
	return found1;

}
}

class flowC {

	Vector<path> fpath;
	double req = 0;
	int src;
	int dest;
	double totalflow;

	public flowC(int r, int sc, int des) {
		totalflow = 0;
		src = sc;
		dest = des;
		req = r;
		fpath = new Vector<path>();
	}

	double flowValue() {
		double fval = 0;
		if (fpath.size() > 0) {
			for (int i = 0; i < fpath.size(); i++) {
				fval = fval + fpath.get(i).flow;
			}
		}
		return fval;
	}

	int addPath(path npath) {
		path ipath = new path();
		int pathId = fpath.size();
		// edge e5;
		boolean found = false;
		boolean found1 = true;
		for (int i = 0; i < fpath.size(); i++) {
			// if (fpath.get(i).equals(npath))
			ipath = fpath.get(i);
			if (ipath.seq.size() == npath.seq.size())
				for (int j = 0; j < ipath.seq.size(); j++) {
					if (ipath.seq.get(j).id != npath.seq.get(j).id)
						found1 = false;
				}
			if (found1 == true) {
				found = true;
				pathId = i;
				// System.out.println("pathid==" + pathId);
			}
		}
		if (!found)
			fpath.add(npath);
		return pathId;
	}

	double UpdateFlowOnPath(int id) throws IOException {
		double kappa = 0.1;
		double priceOfPath = 0.0;		
		path p = fpath.get(id);
		// findprice of path
	for (edge e : p.seq) {
			priceOfPath = priceOfPath + e.priceOnOrig;
		
		}
	
		double flowUp = kappa * (1.0 - priceOfPath);
		if (flowUp < 0)
			flowUp = 0;
		totalflow = totalflow + flowUp;
		if(totalflow<0)
			totalflow=0;
		
			
		p.flow = p.flow + flowUp;
		
		for (edge e : p.seq) {
			e.flow = e.flow + flowUp;
			if(e.pathsUsingEdge.size()!=0)
				{if(!path.pathcheck(e.pathsUsingEdge,p)){
				//	System.out.println("Added path");
					e.pathsUsingEdge.add(p);
				}}
				else{
					e.pathsUsingEdge.add(p);
				}
			
		}
		return flowUp;
	}
}

class graph {
	int NumCopies = 0;
	int NumVert = 0;
	int NumEdges = 0;
	edge[][] tGraph = new edge[4500][4500]; 
	edge[][] Ograph = new edge[150][150]; 
	static int bufmax=10;
	static int energymax=4;
	
	public graph(String filename, int T) throws IOException {
		NumCopies = T;
		String tmp;
		int a, b, c;
		String[] res = null;
		 
		
		// tGraph = null; //change
		FileReader input1 = new FileReader(filename);
		BufferedReader bufRead1 = new BufferedReader(input1);
		tmp = bufRead1.readLine();
		int e = 0;
		res = tmp.split("\\s");
		a = Integer.parseInt(res[0]);
		b = Integer.parseInt(res[1]);
		if (tmp != null) {
			NumVert = a;
			NumEdges = b;
		}
		;
		tmp = bufRead1.readLine();
		while (tmp != null) {
			if (tmp != null) {

				res = tmp.split("\\s");
				a = Integer.parseInt(res[0]);
				b = Integer.parseInt(res[1]);
				c = Integer.parseInt(res[2]);
				edge edges1 = new edge(a, b, c, -1, e);
				Ograph[a][b] = edges1;
				
				int delay = 60/c;
				
				for (int i = 0; i < T; i++) {
					
					boolean bufferedge=false;
					boolean energyedge= false;
					boolean normaledge= false;
					if(a==b){bufferedge=true;}
					else {
						if((a+1 == b) && (Math.abs(a%2) == 0))
					 energyedge=true;
					else if(Math.abs(a%2) == 1)
						normaledge=true;
					}
					
					if (bufferedge && i < T -  1) {
						
					//	System.out.println(c);
							edge edges = new edge(a * T + i, b * T + i + 1, c, i, e);
						tGraph[a * T + i][b * T + i + 1] = edges;
						
					} else//Consider energy edges 
						if (energyedge) {
						edge edges = new edge(a * T + i, b * T + i , c, i, e);
						tGraph[a * T + i][b * T + i] = edges;
					} else 
					if(normaledge && delay+i < T){
						//c=60;
						edge edges = new edge(a * T + i, b * T + i + (60 / c),
								c, i, e);
						tGraph[a * T + i][b * T + i + (60 / c)] = edges;
					}
				e++;
				}
				// to create vertex edges

			}

			tmp = bufRead1.readLine();

		}
		bufRead1.close();
	}

	void PriceEdges() {

		for (int i = 0; i < NumVert; i++)
			for (int j = 0; j < NumVert; j++) {
				if (Ograph[i][j] != null) {// edge e=new edge;
					double flow;
					flow = 0;
					boolean bufferedge=false;
					boolean energyedge= false;
					boolean normaledge= false;
					if(i==j){bufferedge=true;}
					else {
						if((i+1 == j) && (Math.abs(i%2) == 0))
					 energyedge=true;
					else normaledge=true;
					}
					int delay=60 / Ograph[i][j].cap;
					for (int t = 0; t < NumCopies; t++) {
						if (bufferedge && t < NumCopies - 1)
						{
							
							flow = flow
									+ tGraph[i * NumCopies + t][j * NumCopies
											+ t + 1].FlowOnEdge();}
						else if (energyedge){
							flow = flow
									+ tGraph[i * NumCopies + t][j * NumCopies
											+ t].FlowOnEdge();
						}else
							if(normaledge && delay+t < NumCopies){
							flow = flow
									+ tGraph[i * NumCopies + t][j * NumCopies
											+ t + (60 / Ograph[i][j].cap)]
											.FlowOnEdge();
							}
								
						
					}
					Ograph[i][j].flow = flow;
					// Ograph[i][i].flow=flow1;
					Ograph[i][j].FlowOnOrig = flow;
					// Ograph[i][i].FlowOnOrig=flow1;
					Ograph[i][j].priceOnOrig = Ograph[i][j].priceOnOrigEdge();
					// Ograph[i][i].priceOnOrig=Ograph[i][i].priceOnOrigEdge();
				}
			}

		for (int i = 0; i < NumVert; i++)
			for (int j = 0; j < NumVert; j++) {
				if (Ograph[i][j] != null) {
					//int ener0=Math.abs(i % 4);
					//int ener1=Math.abs(j % 4);
					boolean bufferedge=false;
					boolean energyedge= false;
					boolean normaledge= false;
					if(i==j){bufferedge=true;}
					else {
						if((i+1 == j) && (Math.abs(i%2) == 0))
					 energyedge=true;
					else normaledge=true;
					}
					int delay= 60 / Ograph[i][j].cap;
					for (int t = 0; t < NumCopies; t++) {
						if (bufferedge && t < NumCopies-1) {
							tGraph[i * NumCopies + t][j * NumCopies + t + 1].FlowOnOrig = Ograph[i][j].FlowOnOrig;
							// tGraph[i*t][i*t+1].FlowOnOrig=Ograph[i][i].FlowOnOrig;
							tGraph[i * NumCopies + t][j * NumCopies + t + 1].priceOnOrig = Ograph[i][j].priceOnOrig;
						} else if  (energyedge) {
							tGraph[i * NumCopies + t][j * NumCopies + t ].FlowOnOrig = Ograph[i][j].FlowOnOrig;
					//		System.out.println("energeyprice="+ Ograph[i][j].priceOnOrig+"\t Flow="+Ograph[i][j].FlowOnOrig);
							// tGraph[i*t][i*t+1].FlowOnOrig=Ograph[i][i].FlowOnOrig;
							tGraph[i * NumCopies + t][j * NumCopies + t ].priceOnOrig = Ograph[i][j].priceOnOrig;
						} else if(normaledge && delay+t < NumCopies){
							tGraph[i * NumCopies + t][j * NumCopies + t
									+ (60 / Ograph[i][j].cap)].FlowOnOrig = Ograph[i][j].FlowOnOrig;
							// tGraph[i*t][i*t+1].FlowOnOrig=Ograph[i][i].FlowOnOrig;
							tGraph[i * NumCopies + t][j * NumCopies + t
									+ (60 / Ograph[i][j].cap)].priceOnOrig = Ograph[i][j].priceOnOrig;
							// tGraph[i*t][i*t+1].priceOnOrig=Ograph[i][i].priceOnOrig;
						}
						
						
					}
				}

			}

	}

}

public class test {


public static int BFSIZE=4500; 
	static int z, T, E;
	static int numberofcommodities = 15;
	static int Numberofruns=1;
	static int timeofT=10;
    static int rangeofT=0;
    
	public static void main(String[] args) throws IOException {
		int src[]= new int [numberofcommodities];
		int dest[]= new int [numberofcommodities];
		int i;
		long start = System.currentTimeMillis();;
		int GRAPHSIZE=30;
		int BUFFER=3;
	src[0]=0;
	src[1]=4;
	
    src[2]=7;
	src[3]=10;
	src[4]=13;
	
	src[5]=33;
	src[6]=20;
	src[7]=24;
	src[8]=27;
	src[9]=30;
	src[10]=13;
	src[11]=33;
	src[12]=20;
	src[13]=24;
	src[14]=27;
	
	dest[0]=53;
    dest[1]=59;
    
    dest[2]=56;
    dest[3]=53;
    dest[4]=53;
    
    dest[5]=53;
    dest[6]=59;
    dest[7]=56;
    dest[8]=50;
    dest[9]=59;
    dest[10]=56;
    dest[11]=53;
    dest[12]=53;
    dest[13]=53;
    dest[14]=59;
	
    
    for(int numrun=0;numrun< Numberofruns; numrun++)
	//	for(BUFFER=1 ; BUFFER < 10; BUFFER=BUFFER+2)
		{
		
			 int timeofgraph=timeofT + rangeofT;
			// rangeofT=rangeofT+2;
			 String filename = "smallnet" + BUFFER + GRAPHSIZE+ ".txt" ;
			 System.out.println(filename);
		graph G = new graph(filename, timeofgraph);
		// Read in number of commodities
		flowC[] comm = new flowC[numberofcommodities];
		// Read in commodities and requirements-comm is the array of commodities

		
		
		
		
		for (i = 0; i < numberofcommodities; i++) {
			
			 
			src[i] = src[i] * G.NumCopies;
			dest[i] = ((dest[i]+1) * G.NumCopies) - 1;
			flowC fi = new flowC(100, src[i], dest[i]);
			comm[i] = fi;
			// replace with demand

		}

		double threshold = .01;
		int numcom = 0;
		double delta = .01;
		boolean done = false;
		boolean[] stability = new boolean[numberofcommodities];
		for (i = 0; i < numberofcommodities; i++)
			stability[i] = false;

	outerloop : 	while (!done) {
			done = true;
		innerloop :	for (numcom = 0; numcom < numberofcommodities; numcom++) {

				if (comm[numcom].flowValue() <= comm[numcom].req - delta) {
					done = false;
					G.PriceEdges();
					double callbforddelay[][] = new double[BFSIZE][BFSIZE];
					int callbfordcost[][] = new int[BFSIZE][BFSIZE];
					
					for (int a = 0; a < G.NumVert * G.NumCopies; a++)
						{for (int b = 0; b < G.NumVert * G.NumCopies; b++) {

							callbfordcost[a][b] = 0;
							if (G.tGraph[a][b] != null)
							{
								callbforddelay[a][b] = G.tGraph[a][b].priceOnOrig;
						
							}
							else
								{
								callbforddelay[a][b] = -1;
								}
						}
						}

					int callbfordn = G.NumVert * G.NumCopies;
					int callbfordm = G.NumEdges * G.NumCopies;
					int callbfordsink = comm[numcom].dest;
					int callbfordsource = comm[numcom].src;
					
					ArrayList PathfromBF = DistributedBellmanFordj
							.ShortestDelayPaths(callbfordn, callbfordm,
									callbfordsource, callbfordsink,
									callbfordcost, callbforddelay);
					path npath = path.convertSPathtofpath(PathfromBF, G);
					// When you convert path make sure flow variables are 0
				
					
					
					
			System.out.println(PathfromBF);
		
			//	System.out.println(npath.source);
				  if(npath==null)
					  break innerloop;
					if(npath!=null)
					{
						int addid = comm[numcom].addPath(npath);// adds a path
					double flowUp = comm[numcom].UpdateFlowOnPath(addid);
					
					
					if (Math.abs(flowUp) <= threshold) {
				
						stability[numcom] = true;
						int stabcount = 0;
						for (int stab = 0; stab < numberofcommodities; stab++) {
							if (stability[stab] == true)
								{
								stabcount++;

								System.out.print("commodity" + numcom + "\t is stable"
										+ "\t");
							System.out.println(comm[numcom].totalflow);

								}
						}
						if (stabcount == numberofcommodities)
						{ 
							for(int numcomms=0; numcomms< numberofcommodities; numcomms++)
							{
								System.out.print("commodity" + numcomms + "\t is stable"
									+ "\t");
							System.out.println(comm[numcomms].totalflow);
							}
							System.out.println("T=" + timeofgraph );
							if(timeofgraph == Numberofruns + timeofT - 1)
								{
								long end = System.currentTimeMillis();;
								System.out.println((end - start) + " ms");
								System.exit(0);
								
								}
							break outerloop;
							
						}
					}
					}
					//

					// shortest path call and store in p

				}

			}
		}


	}
	}//end of numrun
	
}