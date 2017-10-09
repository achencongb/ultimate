/*
 * Copyright (C) 2017 Alexander Nutz (nutz@informatik.uni-freiburg.de)
 * Copyright (C) 2017 University of Freiburg
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
package de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.transformula.vp.states;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.absint.vpdomain.EqConstraint;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.absint.vpdomain.EqConstraintFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.absint.vpdomain.EqDisjunctiveConstraint;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.absint.vpdomain.EqNode;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.absint.vpdomain.EqNodeAndFunctionFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.absint.vpdomain.IEqNodeIdentifier;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.IIcfgSymbolTable;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.IProgramVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.IProgramVarOrConst;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

/**
 *
 * @author Alexander Nutz (nutz@informatik.uni-freiburg.de)
 *
 */
public class EqStateFactory {

	private final EqNodeAndFunctionFactory mEqNodeAndFunctionFactory;
	private final EqConstraintFactory<EqNode> mEqConstraintFactory;
	private final IIcfgSymbolTable mSymbolTable;
	private EqState mTopStateWithEmptyPvocs;
	private final ManagedScript mMgdScript;

	public EqStateFactory(final EqNodeAndFunctionFactory eqNodeAndFunctionFactory,
			final EqConstraintFactory<EqNode> eqConstraintFactory,
			final IIcfgSymbolTable symbolTable, final ManagedScript mgdScript) {
		mEqNodeAndFunctionFactory = eqNodeAndFunctionFactory;
		mEqConstraintFactory = eqConstraintFactory;
		mSymbolTable = symbolTable;
		mMgdScript = mgdScript;
	}

	public EqState disjoinAll(final Set<EqState> statesForCurrentEc) {
		final EqDisjunctiveConstraint<EqNode> disjunctiveConstraint =
				mEqConstraintFactory.getDisjunctiveConstraint(
						statesForCurrentEc.stream()
								.map(state -> state.getConstraint())
								.collect(Collectors.toSet()));
		final EqConstraint<EqNode> flattenedConstraint = disjunctiveConstraint.flatten();
		return getEqState(flattenedConstraint, flattenedConstraint.getPvocs(mSymbolTable));
	}

	public EqState getTopState() {
		if (mTopStateWithEmptyPvocs == null) {
			mTopStateWithEmptyPvocs = getEqState(mEqConstraintFactory.getEmptyConstraint(), Collections.emptySet());
		}
		return mTopStateWithEmptyPvocs;
	}

	public EqNodeAndFunctionFactory getEqNodeAndFunctionFactory() {
		return mEqNodeAndFunctionFactory;
	}

	public <NODE extends IEqNodeIdentifier<NODE>> EqState getEqState(final EqConstraint<NODE> constraint,
				final Set<IProgramVarOrConst> variables) {
		// TODO manage EqStates smarter?
		return new EqState((EqConstraint<EqNode>) constraint, mEqNodeAndFunctionFactory, this, variables);
	}

	public EqConstraintFactory<EqNode> getEqConstraintFactory() {
		return mEqConstraintFactory;
	}

	public IIcfgSymbolTable getSymbolTable() {
		return mSymbolTable;
	}

	public ManagedScript getManagedScript() {
		return mMgdScript;
	}

	public EqPredicate stateToPredicate(final EqState state) {
		return new EqPredicate(
				getEqConstraintFactory().getDisjunctiveConstraint(Collections.singleton(state.getConstraint())),
				state.getConstraint().getVariables(getSymbolTable()),
				null,
				getSymbolTable(),
				getManagedScript()); // TODO: what procedures does the predicate need?
	}

	public EqPredicate statesToPredicate(final List<EqState> states) {

		final Set<IProgramVar> variables = new HashSet<>();
		final Set<EqConstraint<EqNode>>  constraints = new HashSet<>();
		for (final EqState state : states) {
			variables.addAll(state.getConstraint().getVariables(mSymbolTable));
			constraints.add(state.getConstraint());
		}

		return new EqPredicate(
				getEqConstraintFactory().getDisjunctiveConstraint(constraints),
				variables,
				null,
				getSymbolTable(),
				getManagedScript()); // TODO: what procedures does the predicate need?
	}

	public EqPredicate termToPredicate(final Term spPrecise,
			final IPredicate postConstraint) {
		return new EqPredicate(spPrecise, postConstraint.getVars(), postConstraint.getProcedures(), mSymbolTable,
				mMgdScript);

	}

}
