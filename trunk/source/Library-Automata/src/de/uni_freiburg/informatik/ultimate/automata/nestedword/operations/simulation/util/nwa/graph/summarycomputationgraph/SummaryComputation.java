/*
 * Copyright (C) 2016 Matthias Heizmann (heizmann@informatik.uni-freiburg.de)
 * Copyright (C) 2016 University of Freiburg
 * 
 * This file is part of the ULTIMATE Automata Library.
 * 
 * The ULTIMATE Automata Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The ULTIMATE Automata Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ULTIMATE Automata Library. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Additional permission under GNU GPL version 3 section 7:
 * If you modify the ULTIMATE Automata Library, or any covered work, by linking
 * or combining it with Eclipse RCP (or a modified version of Eclipse RCP),
 * containing parts covered by the terms of the Eclipse Public License, the
 * licensors of the ULTIMATE Automata Library grant you additional permission
 * to convey the resulting work.
 */
package de.uni_freiburg.informatik.ultimate.automata.nestedword.operations.simulation.util.nwa.graph.summarycomputationgraph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.IDoubleDeckerAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.operations.simulation.util.nwa.graph.SpoilerNwaVertex;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.operations.simulation.util.nwa.graph.game.GameLetter;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.operations.simulation.util.nwa.graph.game.IGameState;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.IncomingInternalTransition;
import de.uni_freiburg.informatik.ultimate.util.datastructures.relation.HashRelation;
import de.uni_freiburg.informatik.ultimate.util.datastructures.relation.NestedMap2;

/**
 * Class for computation of game graph summaries.
 * The computation of the summaries is done in the game automaton.
 * @author Matthias Heizmann (heizmann@informatik.uni-freiburg.de)
 *
 * @param <LETTER>
 * @param <STATE>
 */
public class SummaryComputation<LETTER, STATE> {
	
	private final IDoubleDeckerAutomaton<GameLetter<LETTER, STATE>, IGameState> mGameAutomaton = null;
	private final Map<NestedMap2<IGameState, SpoilerNwaVertex<LETTER, STATE>, Integer>, SummaryComputationGraphNode<LETTER, STATE>> mSummaryComputationGraphNodes = 
			new HashMap<NestedMap2<IGameState, SpoilerNwaVertex<LETTER, STATE>, Integer>, SummaryComputationGraphNode<LETTER, STATE>>();
	private final ArrayDeque<SummaryComputationGraphNode<LETTER, STATE>> mWorklist = new ArrayDeque<>();
	
	
	
	private void initialThings() {
		for (final IGameState gs : mGameAutomaton.getStates()) {
			mGameAutomaton.summarySuccessors(gs);
			
		}
	}
	
	
	private void computePredecessors(final SummaryComputationGraphNode<LETTER, STATE> succNode) {
		// collect all relevant letters first
		final Set<LETTER> letters = new HashSet<>();
		for (final IGameState gs : succNode.getCurrent()) {
			for ( final IncomingInternalTransition<GameLetter<LETTER, STATE>, IGameState> trans : mGameAutomaton.internalPredecessors(gs)) {
				final GameLetter<LETTER, STATE> gl = trans.getLetter();
				letters.add(gl.getLetter());
			}
		}
		final List<SummaryComputationGraphNode<LETTER, STATE>> predecessors = new ArrayList();
		for (final LETTER letter : letters) {
			final HashRelation<GameLetter<LETTER, STATE>, IGameState> dupl2spoi = new HashRelation<GameLetter<LETTER, STATE>, IGameState>();
			final HashRelation<IGameState, GameLetter<LETTER, STATE>> spoi2dupl = new HashRelation<IGameState, GameLetter<LETTER, STATE>>();
					
			for (final IGameState gs : succNode.getCurrent()) {
				for (final IncomingInternalTransition<GameLetter<LETTER, STATE>, IGameState> trans : mGameAutomaton.internalPredecessors(gs)) {
					final GameLetter<LETTER, STATE> gl = trans.getLetter();
					if (!gl.equals(letter)) {
						continue;
					}
					dupl2spoi.addPair(trans.getLetter(), gs);
					spoi2dupl.addPair(trans.getPred(), trans.getLetter());
				}
			}
			
			
		}
	}
}
