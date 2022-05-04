package it.polito.tdp.extflightdelays.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	
	private ExtFlightDelaysDAO dao = new ExtFlightDelaysDAO();
	private Graph<Airport, DefaultWeightedEdge> grafo;
	private Map<Integer, Airport> mappaAeroporti = new HashMap<>();
	private Map<Integer, Flight> mappaVoli = new HashMap<>();
	
	public String creaGrafo(int distanzaMinima) {
		
		String s = "";
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		this.mappaAeroporti.clear();
		for(Airport a: this.dao.loadAllAirports()) {
			this.mappaAeroporti.put(a.getId(), a);
		}
		
		this.mappaVoli.clear();
		for(Flight f: this.dao.loadAllFlights()) {
			this.mappaVoli.put(f.getId(), f);
		}
		
		Graphs.addAllVertices(this.grafo, this.mappaAeroporti.values());
		
		for(Airport a: this.grafo.vertexSet()) {
			for(Integer i: this.getVoliByAeroporto(a.getId())) {
				Flight f = this.mappaVoli.get(i);
				if(!this.grafo.containsEdge(this.mappaAeroporti.get(f.getOriginAirportId()), 
						this.mappaAeroporti.get(f.getDestinationAirportId()))
						&& f.getDistance()>=distanzaMinima) {
					Graphs.addEdge(this.grafo, this.mappaAeroporti.get(f.getOriginAirportId()), 
							this.mappaAeroporti.get(f.getDestinationAirportId()), f.getDistance());
				}
			}
		}
			
		s += "Numero vertici: "+this.grafo.vertexSet().size()+"\n"
				+ "Numero archi: "+this.grafo.edgeSet().size()+"\n"
				+ "Elenco archi: \n";
		for(DefaultWeightedEdge arco: this.grafo.edgeSet()) {
			s += this.grafo.getEdgeSource(arco).getAirportName()+" -> "+this.grafo.getEdgeTarget(arco).getAirportName()+
					" | distanza = "+this.grafo.getEdgeWeight(arco)+"\n";
		}
		
		return s;
	}
	
	private Set<Integer> getVoliByAeroporto(int id) {
		Set<Integer> tempL = new HashSet<>();
		for(Flight f: this.mappaVoli.values()) {
			if(f.getOriginAirportId()==id) {
				tempL.add(f.getId());
			}
		}
		return tempL;
	}

}
