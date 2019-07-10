package com.miamioh.ridesharing.app.pso.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.miamioh.ridesharing.app.entity.Event;

public class PSOImpl {

	private final int PARTICLE_COUNT = 100;
	private final int V_MAX = 4; // Maximum velocity change allowed.
	// Range: 0 >= V_MAX < noOfNodes

	private final int MAX_EPOCHS = 100;

	private ArrayList<Particle> particles = new ArrayList<Particle>();

	//private ArrayList<Node> map = new ArrayList<Node>();
	private int noOfNodes;
	// private final double TARGET = 86.63; // Number for algorithm to find.
	// private int XLocs[] = new int[] {30, 40, 40, 29, 19, 9, 9, 20};
	// private int YLocs[] = new int[] {5, 10, 20, 25, 25, 19, 9, 5};

	private List<Event> events;
	private Map<Event, Event> dropToPickupVertexMap = new HashMap<>();

	public PSOImpl(List<Event> events) {
		this.events = events;
		this.noOfNodes = events.size();
	}

	private void initializeMap() {
		Map<String, List<Event>> requestEventMap = new HashMap<>();
		for (Event event : events) {
			if (requestEventMap.get(event.getRequestId()) != null) {
				requestEventMap.get(event.getRequestId()).add(event);
			} else {
				List<Event> eventList = new ArrayList<>();
				eventList.add(event);
				requestEventMap.put(event.getRequestId(), eventList);
			}

		}
		for (String requestId : requestEventMap.keySet()) {
			List<Event> nodeList = requestEventMap.get(requestId);
			Event p1 = null;
			Event d1 = null;
			for (Event vert : nodeList) {
				if (vert.isPickup()) {
					p1 = vert;
				} else {
					d1 = vert;
				}
			}
			dropToPickupVertexMap.put(d1, p1);
		}
		return;
	}

	private void PSOAlgorithm() {
		Particle aParticle = null;
		int epoch = 0;
		boolean done = false;

		initialize();

		while (!done) {
			// Two conditions can end this loop:
			// if the maximum number of epochs allowed has been reached, or,
			// if the Target value has been found.
			if (epoch < MAX_EPOCHS) {

				for (int i = 0; i < PARTICLE_COUNT; i++) {
					aParticle = particles.get(i);
					System.out.print("Route: ");
					for (int j = 0; j < noOfNodes; j++) {
						System.out.print(aParticle.data(j) + ", ");
					} // j

					getTotalDistance(i);
					System.out.print("Distance: " + aParticle.pBest() + "\n");

				} // i

				bubbleSort(); // sort particles by their pBest scores, best to worst.

				getVelocity();

				updateparticles();

				System.out.println("epoch number: " + epoch);

				epoch++;

			} else {
				done = true;
			}
		}
		return;
	}

	private void initialize() {
		for (int i = 0; i < PARTICLE_COUNT; i++) {
			Particle newParticle = new Particle();
			for (int j = 0; j < noOfNodes; j++) {
				newParticle.data(j, j);
			} // j
			particles.add(newParticle);
			for (int j = 0; j < 10; j++) {
				randomlyArrange(particles.indexOf(newParticle));
			}
			getTotalDistance(particles.indexOf(newParticle));
		} // i
		return;
	}

	private void randomlyArrange(final int index) {
		int cityA = new Random().nextInt(noOfNodes);
		int cityB = 0;
		boolean done = false;
		while (!done) {
			cityB = new Random().nextInt(noOfNodes);
			if (cityB != cityA) {
				done = true;
			}
		}

		int temp = particles.get(index).data(cityA);
		particles.get(index).data(cityA, particles.get(index).data(cityB));
		particles.get(index).data(cityB, temp);
		// swap if drop exist before pickup
		int[] mData = particles.get(index).getmData();
		for (int i = 0; i < mData.length; i++) {
			Event node = events.get(mData[i]);
			if (!node.isPickup()) {
				for (int j = i + 1; j < mData.length; j++) {
					Event pickUpNode = events.get(mData[j]);
					if (dropToPickupVertexMap.get(node) != null && dropToPickupVertexMap.get(node).equals(pickUpNode)) {
						int temp1 = mData[i];
						mData[i] = mData[j];
						mData[j] = temp1;
						break;
					}
				}
			}
		}
		/*
		 * System.out.print("Randomly Route: "); for(int j = 0; j < noOfNodes; j++) {
		 * System.out.print(mData[j] + ", "); }
		 */

		return;
	}

	private void getVelocity() {
		double worstResults = 0;
		double vValue = 0.0;

		// after sorting, worst will be last in list.
		worstResults = particles.get(PARTICLE_COUNT - 1).pBest();

		for (int i = 0; i < PARTICLE_COUNT; i++) {
			vValue = (V_MAX * particles.get(i).pBest()) / worstResults;

			if (vValue > V_MAX) {
				particles.get(i).velocity(V_MAX);
			} else if (vValue < 0.0) {
				particles.get(i).velocity(0.0);
			} else {
				particles.get(i).velocity(vValue);
			}
		}
		return;
	}

	private void updateparticles() {
		// Best is at index 0, so start from the second best.
		for (int i = 1; i < PARTICLE_COUNT; i++) {
			// The higher the velocity score, the more changes it will need.
			int changes = (int) Math.floor(Math.abs(particles.get(i).velocity()));
			System.out.println("Changes for particle " + i + ": " + changes);
			for (int j = 0; j < changes; j++) {
				if (new Random().nextBoolean()) {
					randomlyArrange(i);
				}
				// Push it closer to it's best neighbor.
				// copyFromParticle(i - 1, i);
			} // j

			// Update pBest value.
			getTotalDistance(i);
		} // i

		return;
	}

	private Particle printBestSolution() {
		/*
		 * if(particles.get(0).pBest() <= TARGET){ // Print it.
		 * System.out.println("Target reached."); }else{
		 * System.out.println("Target not reached"); }
		 */
		System.out.print("Shortest Route: ");
		for (int j = 0; j < noOfNodes; j++) {
			System.out.print(particles.get(0).data(j) + ", ");
		} // j
		System.out.print("Distance: " + particles.get(0).pBest() + "\n");
		return particles.get(0);
	}

	/*private void copyFromParticle(final int source, final int destination) {
		// push destination's data points closer to source's data points.
		Particle best = particles.get(source);
		int targetA = new Random().nextInt(noOfNodes); // source's city to target.
		int targetB = 0;
		int indexA = 0;
		int indexB = 0;
		int tempIndex = 0;

		// targetB will be source's neighbor immediately succeeding targetA (circular).
		int i = 0;
		for (; i < noOfNodes; i++) {
			if (best.data(i) == targetA) {
				if (i == noOfNodes - 1) {
					targetB = best.data(0); // if end of array, take from beginning.
				} else {
					targetB = best.data(i + 1);
				}
				break;
			}
		}

		// Move targetB next to targetA by switching values.
		for (int j = 0; j < noOfNodes; j++) {
			if (particles.get(destination).data(j) == targetA) {
				indexA = j;
			}
			if (particles.get(destination).data(j) == targetB) {
				indexB = j;
			}
		}
		// get temp index succeeding indexA.
		if (indexA == noOfNodes - 1) {
			tempIndex = 0;
		} else {
			tempIndex = indexA + 1;
		}

		// Switch indexB value with tempIndex value.
		int temp = particles.get(destination).data(tempIndex);
		particles.get(destination).data(tempIndex, particles.get(destination).data(indexB));
		particles.get(destination).data(indexB, temp);

		return;
	}*/

	private void getTotalDistance(final int index) {
		Particle thisParticle = null;
		thisParticle = particles.get(index);
		thisParticle.pBest(0.0);

		for (int i = 0; i < noOfNodes - 1; i++) {
			// commenting below as cab doesnt have to go to start node in order to complete
			// the trip.
			// if(i == noOfNodes - 1){
			// thisParticle.pBest(thisParticle.pBest() +
			// getDistance(thisParticle.data(noOfNodes - 1), thisParticle.data(0))); //
			// Complete trip.
			// }else{
			thisParticle.pBest(thisParticle.pBest() + getDistance(thisParticle.data(i), thisParticle.data(i + 1)));
			// }
		}
		return;
	}

	private double getDistance(final int nodeAIndex, final int nodeBIndex) {
		Event nodeA = events.get(nodeAIndex);
		Event nodeB = events.get(nodeBIndex);
		double distance = distance(nodeA.getLatitude(), nodeB.getLatitude(), nodeA.getLongitude(), nodeB.getLongitude(), 0.0, 0.0);

		return distance;
	}

	public static double distance(double lat1, double lat2, double lon1, double lon2, double el1, double el2) {

		final int R = 6371; // Radius of the earth

		double latDistance = Math.toRadians(lat2 - lat1);
		double lonDistance = Math.toRadians(lon2 - lon1);
		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = R * c * 1000; // convert to meters

		double height = el1 - el2;

		distance = Math.pow(distance, 2) + Math.pow(height, 2);

		return Math.sqrt(distance);
	}

	private void bubbleSort() {
		boolean done = false;
		while (!done) {
			int changes = 0;
			int listSize = particles.size();
			for (int i = 0; i < listSize - 1; i++) {
				if (particles.get(i).compareTo(particles.get(i + 1)) == 1) {
					Particle temp = particles.get(i);
					particles.set(i, particles.get(i + 1));
					particles.set(i + 1, temp);
					changes++;
				}
			}
			if (changes == 0) {
				done = true;
			}
		}
		return;
	}

	private class Particle implements Comparable<Particle> {
		private int mData[] = new int[noOfNodes];
		private double mpBest = 0;
		private double mVelocity = 0.0;

		public Particle() {
			this.mpBest = 0;
			this.mVelocity = 0.0;
		}

		public int compareTo(Particle that) {
			if (this.pBest() < that.pBest()) {
				return -1;
			} else if (this.pBest() > that.pBest()) {
				return 1;
			} else {
				return 0;
			}
		}

		public int[] getmData() {
			return mData;
		}

		public void setmData(int[] mData) {
			this.mData = mData;
		}

		public int data(final int index) {
			return this.mData[index];
		}

		public void data(final int index, final int value) {
			this.mData[index] = value;
			return;
		}

		public double pBest() {
			return this.mpBest;
		}

		public void pBest(final double value) {
			this.mpBest = value;
			return;
		}

		public double velocity() {
			return this.mVelocity;
		}

		public void velocity(final double velocityScore) {
			this.mVelocity = velocityScore;
			return;
		}
	} // Particle

	
	public static void main(String[] args) {
		List<Event> events = new ArrayList<>();
		Event ev1 = new Event();
		ev1.setRequestId(UUID.randomUUID().toString());
		ev1.setLatitude(41.890922026);
		ev1.setLongitude(-87.618868355);
		ev1.setPickup(true);

		Event ev2 = new Event();
		ev2.setRequestId(ev1.getRequestId());
		ev2.setLatitude(41.892072635);
		ev2.setLongitude(-87.62887415700001);
		ev2.setPickup(false);

		Event ev3 = new Event();
		ev3.setRequestId(UUID.randomUUID().toString());
		ev3.setLatitude(41.899602111);
		ev3.setLongitude(-87.633308037);
		ev3.setPickup(true);

		Event ev4 = new Event();
		ev4.setRequestId(ev3.getRequestId());
		ev4.setLatitude(41.785998518);
		ev4.setLongitude(-87.750934289);
		ev4.setPickup(false);

		Event ev5 = new Event();
		ev5.setRequestId(UUID.randomUUID().toString());
		ev5.setLatitude(41.97907082);
		ev5.setLongitude(-87.90303966100002);
		ev5.setPickup(true);

		Event ev6 = new Event();
		ev6.setRequestId(ev5.getRequestId());
		ev6.setLatitude(41.944226601);
		ev6.setLongitude(-87.655998182);
		ev6.setPickup(false);

		
		events.add(ev1);
		events.add(ev2);
		events.add(ev3);
		events.add(ev4);
		events.add(ev5);
		events.add(ev6);

		PSOImpl psoImpl = new PSOImpl(events);
		psoImpl.initializeMap();
		psoImpl.PSOAlgorithm();
		Particle printBestSolution = psoImpl.printBestSolution();
		List<Event> psoNodes = new ArrayList<>();
		for(int i : printBestSolution.getmData()) {
			Event node = psoImpl.events.get(i);
			psoNodes.add(node);
		}
		System.out.println(psoNodes);
		return;
	}

	public List<Event> start() {
		initializeMap();
		PSOAlgorithm();
		Particle printBestSolution = printBestSolution();
		List<Event> psoNodes = new ArrayList<>();
		for(int i : printBestSolution.getmData()) {
			Event node = events.get(i);
			psoNodes.add(node);
		}
		System.out.println(psoNodes);
		return psoNodes;
	}
}