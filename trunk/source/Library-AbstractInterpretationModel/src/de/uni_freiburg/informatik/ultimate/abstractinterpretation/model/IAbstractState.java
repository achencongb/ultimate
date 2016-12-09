/*
 * Copyright (C) 2015 Daniel Dietsch (dietsch@informatik.uni-freiburg.de)
 * Copyright (C) 2015 Marius Greitschus (greitsch@informatik.uni-freiburg.de)
 * Copyright (C) 2015 University of Freiburg
 *
 * This file is part of the ULTIMATE AbstractInterpretationV2 plug-in.
 *
 * The ULTIMATE AbstractInterpretationV2 plug-in is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ULTIMATE AbstractInterpretationV2 plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ULTIMATE AbstractInterpretationV2 plug-in. If not, see <http://www.gnu.org/licenses/>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 * If you modify the ULTIMATE AbstractInterpretationV2 plug-in, or any covered work, by linking
 * or combining it with Eclipse RCP (or a modified version of Eclipse RCP),
 * containing parts covered by the terms of the Eclipse Public License, the
 * licensors of the ULTIMATE AbstractInterpretationV2 plug-in grant you additional permission
 * to convey the resulting work.
 */

package de.uni_freiburg.informatik.ultimate.abstractinterpretation.model;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;

/**
 * An abstract state is an abstraction of all program variables at a certain program location.
 *
 * Note that {@link FixpointEngine} assumes that all operations on an instance of {@link IAbstractState} do not change
 * this instance.
 *
 * @param <STATE>
 *            The actual type of the abstract state.
 * @param <ACTION>
 *            Any action type.
 *
 * @author Daniel Dietsch (dietsch@informatik.uni-freiburg.de)
 * @author Marius Greitschus (greitsch@informatik.uni-freiburg.de)
 */
public interface IAbstractState<STATE extends IAbstractState<STATE, ACTION, VARDECL>, ACTION, VARDECL> {
	
	/**
	 * {@link FixpointEngine} will call this method to add a variable to the set of variables of an abstract state s.t.
	 * they match the current scope.
	 *
	 * All variable names are unique.
	 *
	 * @param variable
	 *            The variable to add.
	 * @return A new abstract state that is a copy of this instance except that it contains the freshly added variable.
	 */
	STATE addVariable(final VARDECL variable);
	
	/**
	 * {@link FixpointEngine} will call this method to remove a variable from the set of variables of an abstract state
	 * s.t. they match the current scope.
	 *
	 * All variable names should be unique.
	 *
	 * A variable will only be removed if it was added before.
	 *
	 * @param variable
	 *            The variable to remove.
	 * @return A new abstract state that is a copy of this instance except that the removed variable is missing.
	 */
	STATE removeVariable(final VARDECL variable);
	
	/**
	 * Adds multiple variables at once.
	 *
	 * @param variables
	 *            A {@link Set} describing all the variables that have to be added.
	 * @return A new abstract state that is a copy of this instance except that it contains the freshly added variables.
	 */
	STATE addVariables(final Collection<VARDECL> variables);
	
	/**
	 * Remove multiple variables at once (see {@link #removeVariable(String, Object)} for details).
	 *
	 * @param variables
	 *            A {@link Map} describing all the variables that have to be removed.
	 * @return A new abstract state that is a copy of this instance except that all the variables defined by
	 *         <code>variables</code> are missing.
	 */
	STATE removeVariables(final Collection<VARDECL> variables);
	
	/**
	 * Check if a given variable exists in the abstract state.
	 *
	 * @param var
	 *            The variable to be tested for containment.
	 * @return true if the variable exists, false otherwise.
	 */
	boolean containsVariable(final VARDECL var);
	
	/**
	 * @return an unmodifiable {@link Set} containing all variables declared in this state.
	 */
	Set<VARDECL> getVariables();
	
	/**
	 * Create a new state that has all the variables and abstraction of this {@link IAbstractState} and of the
	 * <code>dominator</code> (i.e., Var(return) = Var(this) &cup; Var(dominator)). If both states (this and dominator)
	 * share variables, the abstraction of dominator should replace the abstraction of this state (e.g., if
	 * Var(this)={x} and Var(dominator)={x}, then return dominator).
	 * <p>
	 * Each variable from Var(dominator) is<br>
	 * <b>either</b> identical to a variable from Var(this), (i.e. they have the same name, and the same type)<br>
	 * <b>or</b> has a unique name that is not used by any variable in Var(this).
	 *
	 * @param dominator
	 *            The dominator state that should be patched onto <code>this</code>.
	 */
	STATE patch(STATE dominator);
	
	/**
	 * An abstract state is empty when it does not contain any variable.
	 *
	 * @return true if this abstract state is empty, false otherwise.
	 */
	boolean isEmpty();
	
	/**
	 * An abstract state is bottom when it represents the smallest element of the lattice. This should be equivalent to
	 * a predicate stating false.
	 *
	 * @return true if this abstract state is bottom, false otherwise.
	 */
	boolean isBottom();
	
	/**
	 * Check whether this instance is equal to <code>other</code> or not. Instances are equal if they have the same set
	 * of variables and describe the same abstract state.
	 *
	 * @param other
	 *            The other instance.
	 * @return true if both instances have the same set of variables and describe the same abstract state, false
	 *         otherwise.
	 */
	boolean isEqualTo(final STATE other);
	
	/**
	 * Check whether this instance is a strict subset, a subset, equal, or none of this compared to another instance.
	 * Only states with the same set of variables can be compared.
	 *
	 * @param other
	 *            The other instance.
	 * @return A {@link SubsetResult}.
	 */
	SubsetResult isSubsetOf(final STATE other);
	
	/**
	 * Create an SMT constraint that represents this abstract state. If you do not want to implement this right away,
	 * just return <code>script.term("true")</code>.
	 *
	 * @param script
	 *            The {@link Script} instance of the current RCFG.
	 * @return A {@link Term} instance representing this abstract state. Must be false if isBottom is true.
	 */
	Term getTerm(final Script script);
	
	/**
	 * Is used for debug output.
	 *
	 * @return A {@link String} representing this abstract state.
	 */
	String toLogString();
	
	/**
	 * The result of {@link IAbstractState#isSubsetOf(IAbstractState)}.
	 *
	 * @author Daniel Dietsch (dietsch@informatik.uni-freiburg.de)
	 *
	 */
	public enum SubsetResult {
		/**
		 * The set of program states represented by this abstract state is a strict subset of the set of program states
		 * represented by the other abstract state.
		 */
		STRICT,
		/**
		 * The set of program states represented by this abstract state is equal to the set of program states
		 * represented by the other abstract state.
		 */
		EQUAL,
		/**
		 * The set of program states represented by this abstract state is either a strict subset of or equal to the set
		 * of program states represented by the other abstract state.
		 */
		NON_STRICT,
		/**
		 * If none of the other results apply.
		 */
		NONE;
		
		/**
		 * Update this (the current result) with another one. Useful to determine the combined {@link SubsetResult} of a
		 * collection.
		 *
		 * @param accumulated
		 *            The so far accumulated result of an operation.
		 * @param current
		 *            The current intermediate result.
		 * @return The current overall result.
		 */
		public SubsetResult update(final SubsetResult current) {
			switch (this) {
			case EQUAL:
				if (current == SubsetResult.EQUAL) {
					return this;
				}
				break;
			case STRICT:
				if (current == SubsetResult.STRICT || current == SubsetResult.EQUAL) {
					return this;
				}
				break;
			case NON_STRICT:
				if (current != SubsetResult.NONE) {
					return this;
				}
				break;
			case NONE:
				return this;
			default:
				throw new UnsupportedOperationException("Unhandled case " + this);
			}
			return current;
		}
	}
}