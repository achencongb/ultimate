package de.uni_freiburg.informatik.ultimate.automata.nwalibrary.operations.simulation.util.nwa.graph.game;

import java.util.Map;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.StateFactory;

/**
 * Implementation of a StateFactory for game automaton used for summarize edge
 * computation in NWA game graphs. A game automaton uses IGameState, which
 * usually are Spoiler vertices, as states and GameLetter as letters.
 * 
 * @author Daniel Tischner
 *
 */
public final class GameFactory extends StateFactory<IGameState> {

	/**
	 * The state that represents an empty stack.
	 */
	private IGameState emptyStackState;

	/**
	 * Creates a new instance of a game automaton factory object.
	 */
	public GameFactory() {
		emptyStackState = new GameEmptyState();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_freiburg.informatik.ultimate.automata.nwalibrary.StateFactory#
	 * createEmptyStackState()
	 */
	@Override
	public IGameState createEmptyStackState() {
		return emptyStackState;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_freiburg.informatik.ultimate.automata.nwalibrary.StateFactory#
	 * determinize(java.util.Map)
	 */
	@Override
	public IGameState determinize(final Map<IGameState, Set<IGameState>> down2up) {
		return new GameDoubleDeckerSet(down2up);
	}
}