package com.miamioh.ridesharing.app.utilities.helper;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.miamioh.ridesharing.app.constants.VertexTypeEnum;

public class PrimMST {
	


    /**
     * Main method of Prim's algorithm.
     * @param dropToPickupVertexMap 
     */
    public List<Edge> primMST(Graph graph, Vertex startVertex, Map<Vertex, Vertex> dropToPickupVertexMap){

        //binary heap + map data structure
        BinaryMinHeap<Vertex> minHeap = new BinaryMinHeap<>();

        //map of vertex to edge which gave minimum weight to this vertex.
        Map<Vertex,Edge> vertexToEdge = new HashMap<>();

        //stores final result
        List<Edge> result = new LinkedList<>();

        //insert all vertices with infinite value initially.
        for(Vertex v : graph.getAllVertex().values()){
            minHeap.add(Integer.MAX_VALUE, v);
        }

        //start from any random vertex
        //Vertex startVertex = graph.getAllVertex().values().iterator().next();

        //for the start vertex decrease the value in heap + map to 0
        minHeap.decrease(startVertex, 0);

        //iterate till heap + map has elements in it
        while(!minHeap.empty()){
            //extract min value vertex from heap + map
            Vertex current = minHeap.extractMin();

            //get the corresponding edge for this vertex if present and add it to final result.
            //This edge wont be present for first vertex.
            Edge spanningTreeEdge = vertexToEdge.get(current);
            if(spanningTreeEdge != null) {
                result.add(spanningTreeEdge);
            }

            //iterate through all the adjacent vertices
            //for(Edge edge : current.getEdges()){
            for(Vertex v : graph.getAllVertex().values()){
            	if(v.equals(current)) {// || (!current.equals(startVertex) && v.equals(startVertex))) {
            		continue;
            	} 
            	if(v.equals(startVertex)) {
            		continue;
            	}
            	if(v.getType().equals(VertexTypeEnum.DROP)) {
            		Vertex pickUpVert = dropToPickupVertexMap.get(v);
            		if(minHeap.containsData(pickUpVert)) {
            			continue;
            		}
            	}
            	
            	Edge edge = new Edge(current, v, calculateWeight(current, v));
                Vertex adjacent = getVertexForEdge(current, edge);
                //check if adjacent vertex exist in heap + map and weight attached with this vertex is greater than this edge weight
                if(minHeap.containsData(adjacent) && minHeap.getWeight(adjacent) > edge.getWeight()){
                    //decrease the value of adjacent vertex to this edge weight.
                    minHeap.decrease(adjacent, edge.getWeight());
                    //add vertex->edge mapping in the graph.
                    vertexToEdge.put(adjacent, edge);
                }
            }
        }
        return result;
    }
    
    private double calculateWeight(Vertex startVertex, Vertex endVertex) {
    	return ScheduleTaxiEventsHelper.distance(startVertex.getLatitude(), endVertex.getLatitude(), startVertex.getLongitude(), endVertex.getLongitude(), 0.0, 0.0);
	}
    
	private Vertex getVertexForEdge(Vertex v, Edge e){
        return e.getStartVertex().equals(v) ? e.getEndVertex() : e.getStartVertex();
    }

}
