package com.miamioh.ridesharing.app.utilities.helper;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class Edge{
	private Vertex startVertex;
	private Vertex endVertex;
	private double weight;
	
	public Edge( Vertex startVertex, Vertex endVertex, double weight) {
		this.startVertex = startVertex;
		this.endVertex = endVertex;
		this.weight = weight;
	}
}