package de.uni_freiburg.informatik.ultimate.modelcheckerutils.absint.vpdomain;

import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;

/**
 * Helper class to convert formulas into EqDisjunctiveConstraints.
 *
 * @author Alexander Nutz (nutz@informatik.uni-freiburg.de)
 */
public class FormulaToEqDisjunctiveConstraint {

	private final EqConstraintFactory<EqNode> mEqConstraintFactory;
	private final EqNodeAndFunctionFactory mEqNodeAndFunctionFactory;
	private final ManagedScript mMgdScript;
	private final IUltimateServiceProvider mServices;

	public FormulaToEqDisjunctiveConstraint(final IUltimateServiceProvider services, final ManagedScript mgdScript) {
		mServices = services;
		mMgdScript = mgdScript;

		mEqNodeAndFunctionFactory = new EqNodeAndFunctionFactory(mServices, mMgdScript);
		mEqConstraintFactory = new EqConstraintFactory<>(mEqNodeAndFunctionFactory, services, mMgdScript);
	}

	/**
	 * Constructs an EqDisjunctiveConstraint from the given formula.
	 *
	 * @param formula
	 * @return
	 */
	public EqDisjunctiveConstraint<EqNode> convertFormula(final Term formula) {
		final FormulaToEqDisjunctiveConstraintConverter converter =
				new FormulaToEqDisjunctiveConstraintConverter(mServices, mMgdScript, mEqConstraintFactory,
						mEqNodeAndFunctionFactory, formula);
		return converter.getResult();
	}
}
