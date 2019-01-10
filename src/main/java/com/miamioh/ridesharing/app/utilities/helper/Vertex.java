package com.miamioh.ridesharing.app.utilities.helper;

import java.util.ArrayList;
import java.util.List;

import com.miamioh.ridesharing.app.constants.VertexTypeEnum;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class Vertex {
	
		private String id;
		private double latitude;
		private double longitude;
		private VertexTypeEnum type;
		private List<Edge> edges = new ArrayList<>();
	    private List<Vertex> adjacentVertex = new ArrayList<>();
	    
	    public Vertex(String id, double latitude, double longitude, VertexTypeEnum type) {
	    	this.id = id;
	    	this.latitude = latitude;
	    	this.longitude = longitude;
	    	this.type = type;
		}
	    
	    public void addAdjacentVertex(Edge e, Vertex v) {
	    	edges.add(e);
	    	adjacentVertex.add(v);
	    }
	    
	    @Override
	    public int hashCode() {
	        return this.id.hashCode();
	    }
	    
	    @Override
	    public boolean equals(Object obj) {
	        if (this == obj)
	            return true;
	        if (obj == null)
	            return false;
	        if (getClass() != obj.getClass())
	            return false;
	        Vertex other = (Vertex) obj;
	        if (id != other.id && !type.equals(other.type))
	            return false;
	        return true;
	    }

}
