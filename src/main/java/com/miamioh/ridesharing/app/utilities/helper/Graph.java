package com.miamioh.ridesharing.app.utilities.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class Graph {
	private Map<String, Vertex> allVertex;
	private List<Edge> allEdges;
	
	public Graph() {
		this.allVertex = new HashMap<>();
		this.allEdges = new ArrayList<>();
		
	}
	
	public void addVertex(Vertex vertex){
        if(allVertex.containsKey(vertex.getId())){
            return;
        }
        allVertex.put(vertex.getId(), vertex);
        for(Edge edge : vertex.getEdges()){
            allEdges.add(edge);
        }
    }
	
	public void addEdge(Vertex v1, Vertex v2, double weight){
        if(allVertex.containsKey(v1.getId())){
            v1 = allVertex.get(v1.getId());
        }else{
            allVertex.put(v1.getId(), v1);
        }
        if(allVertex.containsKey(v2.getId())){
            v2 = allVertex.get(v2.getId());
        }else{
            allVertex.put(v2.getId(), v2);
        }

        Edge edge = new Edge(v1,v2,weight);
        allEdges.add(edge);
        v1.addAdjacentVertex(edge, v2);

    }
	
	public void addSingleVertex(Vertex vertex){
        allVertex.put(vertex.getId(), vertex);
    }
	
	/*public Collection<Vertex> getAllVertex(){
        return allVertex.values();
    }*/
}


