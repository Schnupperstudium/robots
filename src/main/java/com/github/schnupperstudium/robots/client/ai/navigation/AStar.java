package com.github.schnupperstudium.robots.client.ai.navigation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.github.schnupperstudium.robots.entity.Facing;
import com.github.schnupperstudium.robots.world.Location;
import com.github.schnupperstudium.robots.world.Map;

public final class AStar {
	public static final List<Location> TOUCHED = new ArrayList<>();
		
	public static List<Location> computeOtherPath(final int sX, final int sY, final Facing sFacing, final int tX, final int tY, Map map) {
		TOUCHED.clear();
		
		// queue of open states
		List<State> openList = new ArrayList<>();
		// set of closed states
		Set<State> closedList = new HashSet<>();
		// map of all states
		HashMap<Integer, State> states = new HashMap<>();
		
		final State initialState = new State(null, sX, sY, sFacing, 0, computeEstimatedCost(sX, sY, tX, tY));
		states.put(initialState.hashCode(), initialState);
		openList.add(initialState);
		
		State state = null;
		do {
			// retrieve best current best open state
			Collections.sort(openList);
			state = openList.remove(0);
			
			TOUCHED.add(state.getLocation());
			
			// check if state was already closed
			if (closedList.contains(state))
				continue;
			
			// compute hash and mark state as closed
			final int hash = state.hashCode();
			states.put(hash, state);
			
			// generate predecessors
			// drive forward
			checkNext(states, openList, closedList, map, tX, tY, state,
					state.x + state.facing.dx, state.y + state.facing.dy, state.facing, state.cost + 1);
			
			// drive backward
			checkNext(states, openList, closedList, map, tX, tY, state,
					state.x - state.facing.dx, state.y - state.facing.dy, state.facing, state.cost + 1);
			
			// turn left
			checkNext(states, openList, closedList, map, tX, tY, state,
					state.x, state.y, state.facing.left(), state.cost + 1);
			
			// turn right
			checkNext(states, openList, closedList, map, tX, tY, state,
					state.x, state.y, state.facing.right(), state.cost + 1);
			
		} while (!openList.isEmpty() && (state.x != tX || state.y != tY));
		
		LinkedList<Location> result = new LinkedList<>();
		while (state != null) {
			result.addFirst(state.getLocation());
			state = state.previous;
		}
		
		return result;
	}
	
	private static void checkNext(HashMap<Integer, State> states, List<State> openList, Set<State> closedList, Map map, int tX, int tY,
			State state, final int nX, final int nY, final Facing nFacing, final int nextCost) {
		final int nextEstimatedCost = computeEstimatedCost(nX, nY, tX, tY);
		
		// compute hash for next state and try to retrieve it
		final int nextHash = State.computeHash(nX, nY, nFacing);
		State nextState = states.get(nextHash);
		
		if (nextState == null) {
			if (!map.getTile(nX, nY).isVisitable())
				return;
			
			// next state was not yet preset -> create and add to open list
			nextState = new State(state, nX, nY, nFacing, nextCost, nextEstimatedCost);
			states.put(nextHash, nextState);
			openList.add(nextState);
			
//			TOUCHED.add(nextState.getLocation());
		} else {
			// check if next state is already closed
			if (closedList.contains(nextState))
				return;
			
			// check if we found a better way to reach the point and if so update it
			if (nextCost < state.cost) {
				nextState.previous = state;
				nextState.cost = nextCost;
//				TOUCHED.add(nextState.getLocation());
			}
		}
	}
	
	private static int computeEstimatedCost(int x, int y, int targetX, int targetY) {
		return Math.abs(x - targetX) + Math.abs(y - targetY);
	}
	
	
	private static class State implements Comparable<State> {
		private final int x;
		private final int y;
		private final Facing facing;
		
		private State previous;
		private int cost;
		private int estimatedCost;
		
		private State(State previous, int x, int y, Facing facing, int cost, int estimatedCost) {
			this.previous = previous;
			this.x = x;
			this.y = y;
			this.facing = facing;
			this.cost = cost;
			this.estimatedCost = estimatedCost;
		}

		private static int computeHash(int x, int y, Facing facing) {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			result = prime * result + facing.ordinal();
			return result;
		}
		
		@Override
		public int compareTo(State o) {
			return Integer.compare(cost + estimatedCost, o.cost + o.estimatedCost);
		}
		
		@Override
		public int hashCode() {
			return computeHash(x, y, facing);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			State other = (State) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}

		private Location getLocation() {
			return new Location(x, y, facing);
		}
	}
}
