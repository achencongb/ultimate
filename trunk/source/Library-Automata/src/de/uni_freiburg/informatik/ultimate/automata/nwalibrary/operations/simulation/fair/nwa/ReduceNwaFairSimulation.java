/*
 * Copyright (C) 2015-2016 Daniel Tischner
 * Copyright (C) 2009-2016 University of Freiburg
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
package de.uni_freiburg.informatik.ultimate.automata.nwalibrary.operations.simulation.fair.nwa;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryException;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.AutomataOperationCanceledException;
import de.uni_freiburg.informatik.ultimate.automata.IOperation;
import de.uni_freiburg.informatik.ultimate.automata.LibraryIdentifiers;
import de.uni_freiburg.informatik.ultimate.automata.ResultChecker;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.StateFactory;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.buchiNwa.BuchiAccepts;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.buchiNwa.LassoExtractor;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.buchiNwa.NestedLassoWord;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.operations.IsIncluded;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.operations.minimization.LookaheadPartitionConstructor;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.operations.simulation.fair.FairSimulation;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.operations.simulation.fair.ReduceBuchiFairSimulation;

/**
 * Operation that reduces a given nwa by using {@link FairSimulation}.
 * <br/>
 * Once constructed the reduction automatically starts, the result can be get by
 * using {@link #getResult()}.
 * 
 * @author Daniel Tischner
 * 
 * @param <LETTER>
 *            Letter class of nwa
 * @param <STATE>
 *            State class of nwa
 */
public final class ReduceNwaFairSimulation<LETTER, STATE> extends ReduceBuchiFairSimulation<LETTER, STATE>
		implements IOperation<LETTER, STATE> {

	/**
	 * Creates a new nwa reduce object that starts reducing the given nwa
	 * automaton using SCCs as an optimization.<br/>
	 * Once finished the result can be get by using {@link #getResult()}.
	 * 
	 * @param services
	 *            Service provider of Ultimate framework
	 * @param stateFactory
	 *            The state factory used for creating states
	 * @param operand
	 *            The nwa to reduce
	 * @throws AutomataOperationCanceledException
	 *             If the operation was canceled, for example from the Ultimate
	 *             framework.
	 */
	public ReduceNwaFairSimulation(final AutomataLibraryServices services, final StateFactory<STATE> stateFactory,
			final INestedWordAutomaton<LETTER, STATE> operand) throws AutomataOperationCanceledException {
		this(services, stateFactory, operand, true, Collections.emptyList());
	}

	/**
	 * Creates a new nwa reduce object that starts reducing the given nwa
	 * automaton.<br/>
	 * Once finished the result can be get by using {@link #getResult()}.
	 * 
	 * @param services
	 *            Service provider of Ultimate framework
	 * @param stateFactory
	 *            The state factory used for creating states
	 * @param operand
	 *            The nwa to reduce
	 * @param useSCCs
	 *            If the simulation calculation should be optimized using SCC,
	 *            Strongly Connected Components.
	 * @throws AutomataOperationCanceledException
	 *             If the operation was canceled, for example from the Ultimate
	 *             framework.
	 */
	public ReduceNwaFairSimulation(final AutomataLibraryServices services, final StateFactory<STATE> stateFactory,
			final INestedWordAutomaton<LETTER, STATE> operand, final boolean useSCCs)
					throws AutomataOperationCanceledException {
		this(services, stateFactory, operand, useSCCs,
				new LookaheadPartitionConstructor<LETTER, STATE>(services, operand).getResult());
	}

	/**
	 * Creates a new nwa reduce object that starts reducing the given nwa
	 * automaton.<br/>
	 * Once finished the result can be get by using {@link #getResult()}.
	 * 
	 * @param services
	 *            Service provider of Ultimate framework
	 * @param stateFactory
	 *            The state factory used for creating states
	 * @param operand
	 *            The nwa to reduce
	 * @param useSCCs
	 *            If the simulation calculation should be optimized using SCC,
	 *            Strongly Connected Components.
	 * @param possibleEquivalentClasses
	 *            A collection of sets which contains states of the nwa
	 *            automaton that may be merge-able. States which are not in the
	 *            same set are definitely not merge-able which is used as an
	 *            optimization for the simulation
	 * @throws AutomataOperationCanceledException
	 *             If the operation was canceled, for example from the Ultimate
	 *             framework.
	 */
	public ReduceNwaFairSimulation(final AutomataLibraryServices services, final StateFactory<STATE> stateFactory,
			final INestedWordAutomaton<LETTER, STATE> operand, final boolean useSCCs,
			final Collection<Set<STATE>> possibleEquivalentClasses) throws AutomataOperationCanceledException {
		super(services, stateFactory, operand, useSCCs, false,
				new FairNwaSimulation<LETTER, STATE>(services.getProgressMonitorService(),
						services.getLoggingService().getLogger(LibraryIdentifiers.PLUGIN_ID), useSCCs, stateFactory,
						possibleEquivalentClasses,
						new FairNwaGameGraph<LETTER, STATE>(services, services.getProgressMonitorService(),
								services.getLoggingService().getLogger(LibraryIdentifiers.PLUGIN_ID), operand,
								stateFactory, possibleEquivalentClasses)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_freiburg.informatik.ultimate.automata.nwalibrary.operations.
	 * simulation.fair.ReduceBuchiFairSimulation#checkResult(de.uni_freiburg.
	 * informatik.ultimate.automata.nwalibrary.StateFactory)
	 */
	@Override
	public boolean checkResult(final StateFactory<STATE> stateFactory) throws AutomataLibraryException {
		getLogger().info("Start testing correctness of " + operationName());
		boolean correct = true;

		final AutomataLibraryServices services = getServices();
		final INestedWordAutomaton<LETTER, STATE> operand = getOperand();
		final INestedWordAutomaton<LETTER, STATE> result = getResult();

		// This is a semi-test, if it returns false, the result can also be
		// correct though
		correct &= (new IsIncluded<LETTER, STATE>(services, stateFactory, operand, result)).getResult();
		correct &= (new IsIncluded<LETTER, STATE>(services, stateFactory, result, operand)).getResult();

		// Try using some random lasso-words to prove a possible incorrectness
		if (!correct) {
			final List<NestedLassoWord<LETTER>> nestedLassoWords = new LinkedList<>();
			nestedLassoWords.addAll((new LassoExtractor<LETTER, STATE>(services, operand)).getResult());
			nestedLassoWords.addAll((new LassoExtractor<LETTER, STATE>(services, result)).getResult());

			nestedLassoWords.add(ResultChecker.getRandomNestedLassoWord(result, 1));
			nestedLassoWords.add(ResultChecker.getRandomNestedLassoWord(result, 1));
			nestedLassoWords.add(ResultChecker.getRandomNestedLassoWord(result, 1));
			nestedLassoWords.add(ResultChecker.getRandomNestedLassoWord(result, 1));
			nestedLassoWords.add(ResultChecker.getRandomNestedLassoWord(result, 1));
			nestedLassoWords.add(ResultChecker.getRandomNestedLassoWord(result, 1));

			nestedLassoWords.add(ResultChecker.getRandomNestedLassoWord(result, 2));
			nestedLassoWords.add(ResultChecker.getRandomNestedLassoWord(result, 2));
			nestedLassoWords.add(ResultChecker.getRandomNestedLassoWord(result, 2));
			nestedLassoWords.add(ResultChecker.getRandomNestedLassoWord(result, 2));
			nestedLassoWords.add(ResultChecker.getRandomNestedLassoWord(result, 2));
			nestedLassoWords.add(ResultChecker.getRandomNestedLassoWord(result, 2));
			nestedLassoWords.add(ResultChecker.getRandomNestedLassoWord(result, 2));
			nestedLassoWords.add(ResultChecker.getRandomNestedLassoWord(result, 2));
			nestedLassoWords.add(ResultChecker.getRandomNestedLassoWord(result, 2));
			nestedLassoWords.add(ResultChecker.getRandomNestedLassoWord(result, 2));
			nestedLassoWords.add(ResultChecker.getRandomNestedLassoWord(result, 2));

			correct = true;
			for (final NestedLassoWord<LETTER> nestedLassoWord : nestedLassoWords) {
				final boolean op = (new BuchiAccepts<LETTER, STATE>(services, operand, nestedLassoWord)).getResult();
				final boolean res = (new BuchiAccepts<LETTER, STATE>(services, operand, nestedLassoWord)).getResult();
				correct &= (op == res);
			}
		}
		getLogger().info("Finished testing correctness of " + operationName());

		// We may assume the result is correct as the chance of it being not
		// covered by the test is comparable small
		return correct;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_freiburg.informatik.ultimate.automata.nwalibrary.operations.
	 * buchiReduction.fair.ReduceBuchiFairSimulation#operationName()
	 */
	@Override
	public String operationName() {
		return "reduceNwaFairSimulation";
	}
}
