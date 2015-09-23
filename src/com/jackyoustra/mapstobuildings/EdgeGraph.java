package com.jackyoustra.mapstobuildings;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class EdgeGraph {
	
	public final List<EdgeNode> edgeNodes = new ArrayList<>();
	
	public void add(Point nodeLocation){
		EdgeNode newNode = new EdgeNode(nodeLocation);
		edgeNodes.add(newNode);
	}
	
	public void connectNodes(){
		for(EdgeNode currentNode : edgeNodes){
			for(EdgeNode testNode : edgeNodes){
				if(currentNode.ID > testNode.ID && currentNode.isAdjacent(testNode)){ // id test first so no intensive later test
					// know contact occurs, current node can now make reference
					currentNode.contactingNodes.add(testNode);
				}
			}
		}
	}
	
	public static class EdgeNode{
		public final int ID;
		public final Point edgePoint;
		private static int currentID = 0;
		public final List<EdgeNode> contactingNodes = new ArrayList<>();
		
		public EdgeNode(Point location){
			ID = currentID++;
			edgePoint = location;
		}
		
		public boolean isAdjacent(EdgeNode other){
			return Math.abs(this.edgePoint.x-other.edgePoint.x) <= 1 || Math.abs(this.edgePoint.y-other.edgePoint.y) <= 1;
		}
	}
}
