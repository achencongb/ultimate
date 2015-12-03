/*
 * Copyright (C) 2015 Christian Schilling <schillic@informatik.uni-freiburg.de>
 * Copyright (C) 2009-2015 University of Freiburg
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
package de.uni_freiburg.informatik.ultimate.automata.nwalibrary.deltaDebug;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.StateFactory;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.StringFactory;
import de.uni_freiburg.informatik.ultimate.core.services.model.IUltimateServiceProvider;

/**
 * Exemplary usage of the {@link AutomatonDebugger}.
 * 
 * @author Christian Schilling <schillic@informatik.uni-freiburg.de>
 */
public class SampleAutomatonDebugger {
	public static void main(String[] args) {
		// service provider (needed for the automaton and automaton factory)
		// DD: ToolchainStorage is not available outside of CoreRCP
		final IUltimateServiceProvider services = null;

		// use automata of type "string"
		final StateFactory<String> stateFactory = new StringFactory();

		// automaton
		final INestedWordAutomaton<String, String> automaton = getSampleAutomaton(services, stateFactory);

		// automaton factory
		final AAutomatonFactory<String, String> automatonFactory = new NestedWordAutomatonFactory<String, String>(
				stateFactory, automaton, services);

		// tester
		final ATester<String, String> tester = new SampleTester();

		// delta debugger
		final AutomatonDebugger<String, String> debugger = new AutomatonDebugger<String, String>(automaton,
				automatonFactory, tester);

		// list of shrinkers (i.e., rules to apply) to be applied iteratively
		final List<AShrinker<?, String, String>> shrinkersLoop = new ArrayList<AShrinker<?, String, String>>();
		shrinkersLoop.add(new StateShrinker<String, String>());
		shrinkersLoop.add(new InternalTransitionShrinker<String, String>());
		shrinkersLoop.add(new CallTransitionShrinker<String, String>());
		shrinkersLoop.add(new ReturnTransitionShrinker<String, String>());
		shrinkersLoop.add(new SingleExitShrinker<String, String>());

		// list of shrinkers (i.e., rules to apply) to be applied only once
		final List<AShrinker<?, String, String>> shrinkersEnd = new ArrayList<AShrinker<?, String, String>>();
		shrinkersEnd.add(new UnusedLetterShrinker<String, String>());
		shrinkersEnd.add(new NormalizeStateShrinker<String, String>());

		// execute delta debugger (binary search)
		final INestedWordAutomaton<String, String> result = debugger.shrink(shrinkersLoop, shrinkersEnd);

		// print result
		System.out.println("The automaton debugger terminated resulting in the following automaton:");
		System.out.println(result);
	}

	/**
	 * @param services
	 *            service provider
	 * @param stateFactory
	 *            state factory
	 * @return a sample automaton
	 */
	private static INestedWordAutomaton<String, String> getSampleAutomaton(final IUltimateServiceProvider services,
			final StateFactory<String> stateFactory) {
		final HashSet<String> internals = new HashSet<String>();
		internals.add("a1");
		internals.add("a2");
		internals.add("a3");
		internals.add("a4");
		final HashSet<String> calls = new HashSet<String>();
		calls.add("c1");
		calls.add("c2");
		calls.add("c3");
		final HashSet<String> returns = new HashSet<String>();
		returns.add("r1");
		returns.add("r2");
		returns.add("r3");

		NestedWordAutomaton<String, String> automaton = new NestedWordAutomaton<String, String>(services, internals,
				calls, returns, stateFactory);

		automaton.addState(true, false, "q0");
		automaton.addState(false, false, "q1");
		automaton.addState(false, false, "q2");
		automaton.addState(false, false, "q3");
		automaton.addState(false, false, "q4");
		automaton.addState(false, true, "q5");
		automaton.addState(false, false, "q6");
		automaton.addState(false, false, "q7");
		automaton.addState(false, true, "q8");

		automaton.addInternalTransition("q0", "a1", "q1");
		automaton.addInternalTransition("q1", "a1", "q2");
		automaton.addInternalTransition("q2", "a1", "q3");
		automaton.addInternalTransition("q3", "a1", "q4");
		automaton.addInternalTransition("q4", "a1", "q5");
		automaton.addInternalTransition("q5", "a1", "q6");

		automaton.addCallTransition("q1", "c1", "q1");
		automaton.addCallTransition("q5", "c2", "q2");
		automaton.addCallTransition("q5", "c3", "q3");
		automaton.addCallTransition("q6", "c1", "q7");

		automaton.addReturnTransition("q1", "q1", "r1", "q1");
		automaton.addReturnTransition("q2", "q5", "r1", "q1");
		automaton.addReturnTransition("q4", "q5", "r1", "q1");
		automaton.addReturnTransition("q7", "q6", "r1", "q8");

		return automaton;
	}

	/**
	 * Sample tester which throws an exception iff the automaton violates one of
	 * the specified constraints.
	 */
	static class SampleTester extends ATester<String, String> {
		public SampleTester() {
			super(new DebuggerException(null, "test exception"));
		}

		@Override
		public void execute(INestedWordAutomaton<String, String> automaton) throws DebuggerException {
			boolean result = true;

			// states: q1 and q3 exist
			result &= automaton.getStates().contains("q1");
			result &= automaton.getStates().contains("q2");
			result &= automaton.getStates().contains("q3") || automaton.getStates().contains("q_1");
			result &= automaton.getStates().contains("q5");
			result &= automaton.getStates().contains("q8");

			// internal transitions: q1 and q2 have an outgoing transition
			result &= automaton.internalSuccessors("q1").iterator().hasNext();
			result &= automaton.internalSuccessors("q2").iterator().hasNext();
			// q8 has an incoming internal transition
			result &= automaton.internalPredecessors("q8").iterator().hasNext();

			// call transitions:
			// q1 has an outgoing transition
			result &= automaton.callSuccessors("q1").iterator().hasNext();
			// q5 has an outgoing transition (nondeterministic!)
			result &= automaton.callSuccessors("q5").iterator().hasNext();

			// return transitions: q2 has an outgoing transition
			result &= automaton.returnSuccessors("q2").iterator().hasNext();

			// internal alphabet: a2 exists
			result &= automaton.getAlphabet().contains("a2");

			// throw an exception if one of the above constraints is violated
			if (result) {
				throw new DebuggerException(this.getClass(), "test exception");
			}
		}
	}
}