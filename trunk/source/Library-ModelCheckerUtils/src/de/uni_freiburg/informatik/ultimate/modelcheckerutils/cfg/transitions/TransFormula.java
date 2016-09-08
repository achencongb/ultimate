/*
 * Copyright (C) 2014-2015 Daniel Dietsch (dietsch@informatik.uni-freiburg.de)
 * Copyright (C) 2010-2015 Matthias Heizmann (heizmann@informatik.uni-freiburg.de)
 * Copyright (C) 2012-2015 University of Freiburg
 * 
 * This file is part of the ULTIMATE ModelCheckerUtils Library.
 * 
 * The ULTIMATE ModelCheckerUtils Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The ULTIMATE ModelCheckerUtils Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ULTIMATE ModelCheckerUtils Library. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Additional permission under GNU GPL version 3 section 7:
 * If you modify the ULTIMATE ModelCheckerUtils Library, or any covered work, by linking
 * or combining it with Eclipse RCP (or a modified version of Eclipse RCP), 
 * containing parts covered by the terms of the Eclipse Public License, the 
 * licensors of the ULTIMATE ModelCheckerUtils Library grant you additional permission 
 * to convey the resulting work.
 */
package de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.transitions;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.logic.TermVariable;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.IProgramVar;

/**
 * Represents the transition of a program or a transition system as an SMT
 * formula. The formula denotes a binary relation of this-state/next-state
 * pairs, where we consider a state as a variable assignment. The variables that
 * describe the "this-state"s are given as a BoogieVar, stored as the keySet of
 * the Map mInVars. mInVars maps to each of these variables a corresponding
 * TermVariable in the formula. The variables that describe the "next-state"s
 * are given as a set of strings, stored as the keySet of the Map mOutVars.
 * mInVars maps to each of these variables a corresponding TermVariable in the
 * formula. All TermVariables that occur in the formula are stored in the Set
 * mVars. The names of all variables that are assigned/updated by this
 * transition are stored in mAssignedVars (this information is obtained from
 * mInVars and mOutVars). If a variable does not occur in the this-state, but
 * in the next-state it may have any value (think of a Havoc Statement).
 * <p>
 * A TransFormula represents the set of transitions denoted by the formula φ
 * over primed and unprimed variables where φ is obtained by
 * <ul>
 * <li>first replacing for each x ∈ dom(invar) the TermVariable invar(x) in
 * mFormula by x
 * <li>then replacing for each x ∈ dom(outvar) the TermVariable onvar(x) in
 * mFormula by x'
 * <li>finally, adding the conjunct x=x' for each x∈(dom(invar)⋂dom(outvar)
 * such that invar(x)=outvar(x)
 * </ul>
 * 
 * 
 * 
 * @author heizmann@informatik.uni-freiburg.de
 */
public abstract class TransFormula {

	private final Map<IProgramVar, TermVariable> mInVars;
	private final Map<IProgramVar, TermVariable> mOutVars;
	private final Set<TermVariable> mAuxVars;

	public TransFormula(final Map<IProgramVar, TermVariable> inVars, final Map<IProgramVar, TermVariable> outVars,
			final Set<TermVariable> auxVars) {
		super();
		mInVars = inVars;
		mOutVars = outVars;
		mAuxVars = auxVars;
	}

	public abstract Set<IProgramVar> getAssignedVars();

	public abstract Term getFormula();

	/**
	 * compute the assigned/updated variables. A variable is updated by this
	 * transition if it occurs as outVar and
	 * - it does not occur as inVar
	 * - or the inVar is represented by a different TermVariable
	 */
	protected HashSet<IProgramVar> computeAssignedVars(final Map<IProgramVar, TermVariable> inVars, final Map<IProgramVar, TermVariable> outVars) {
		final HashSet<IProgramVar> assignedVars = new HashSet<IProgramVar>();
		for (final IProgramVar var : outVars.keySet()) {
			assert (outVars.get(var) != null);
			if (outVars.get(var) != inVars.get(var)) {
				assignedVars.add(var);
			}
		}
		return assignedVars;
	}

	public Map<IProgramVar, TermVariable> getInVars() {
		return Collections.unmodifiableMap(mInVars);
	}

	public Map<IProgramVar, TermVariable> getOutVars() {
		return Collections.unmodifiableMap(mOutVars);
	}

	public Set<TermVariable> getAuxVars() {
		return Collections.unmodifiableSet(mAuxVars);
	}

	/**
	 * If this method returns true, the outVar of bv may have any value even if
	 * the value of the inVar is restricted. If the methods returns false there
	 * are constraints on the outVar or syntactic check was not able to find out
	 * that there are no constraints.
	 */
	public boolean isHavocedOut(final IProgramVar bv) {
		final TermVariable inVar = mInVars.get(bv);
		final TermVariable outVar = mOutVars.get(bv);
		if (inVar == outVar) {
			return false;
		} else {
			return !Arrays.asList(getFormula().getFreeVars()).contains(outVar);
		}
	}

	public boolean isHavocedIn(final IProgramVar bv) {
		final TermVariable inVar = mInVars.get(bv);
		final TermVariable outVar = mOutVars.get(bv);
		if (inVar == outVar) {
			return false;
		} else {
			return !Arrays.asList(getFormula().getFreeVars()).contains(inVar); 
		}
	}

}