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

package de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.nonrelational.evaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.boogie.ast.UnaryExpression.Operator;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.IBoogieVar;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.nonrelational.BooleanValue;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.nonrelational.INonrelationalAbstractState;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.nonrelational.INonrelationalValue;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.nonrelational.INonrelationalValueFactory;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.nonrelational.NonrelationalEvaluationResult;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.nonrelational.NonrelationalUtils;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.CodeBlock;

/**
 * An evaluator for unary expressions in a nonrelational abstract domain.
 * 
 * @author Marius Greitschus (greitsch@informatik.uni-freiburg.de)
 *
 * @param <VALUE>
 *            The type of values of the abstract domain.
 * @param <STATE>
 *            The type of states of the abstract domain.
 */
public class UnaryExpressionEvaluator<VALUE extends INonrelationalValue<VALUE>, STATE extends INonrelationalAbstractState<STATE, CodeBlock>>
        implements INAryEvaluator<VALUE, STATE, CodeBlock> {

	private final ILogger mLogger;
	private final INonrelationalValueFactory<VALUE> mNonrelationalValueFactory;

	private IEvaluator<VALUE, STATE, CodeBlock> mSubEvaluator;
	private Operator mOperator;

	public UnaryExpressionEvaluator(final ILogger logger,
	        final INonrelationalValueFactory<VALUE> nonrelationalValueFactory) {
		mLogger = logger;
		mNonrelationalValueFactory = nonrelationalValueFactory;
	}

	@Override
	public List<IEvaluationResult<VALUE>> evaluate(STATE currentState) {
		assert currentState != null;

		final List<IEvaluationResult<VALUE>> returnList = new ArrayList<>();

		final List<IEvaluationResult<VALUE>> subEvaluatorResult = mSubEvaluator.evaluate(currentState);

		for (final IEvaluationResult<VALUE> result : subEvaluatorResult) {
			VALUE returnValue = mNonrelationalValueFactory.createTopValue();
			BooleanValue returnBool;

			switch (mOperator) {
			case ARITHNEGATIVE:
				returnBool = new BooleanValue(false);
				returnValue = result.getValue().negate();
				break;
			case LOGICNEG:
				returnBool = result.getBooleanValue().neg();
				returnValue = mNonrelationalValueFactory.createTopValue();
				break;
			default:
				mLogger.warn(
				        "Operator " + mOperator + " is not implemented. Assuming logical interpretation to be TOP.");
				returnBool = new BooleanValue();
				mLogger.warn("Possible loss of precision: cannot handle operator " + mOperator
				        + ". Returning current state. Returned value is top.");
				returnValue = mNonrelationalValueFactory.createTopValue();
				break;
			}

			returnList.add(new NonrelationalEvaluationResult<>(returnValue, returnBool));
		}

		assert !returnList.isEmpty();
		return NonrelationalUtils.mergeIfNecessary(returnList, 2);
	}

	@Override
	public List<STATE> inverseEvaluate(IEvaluationResult<VALUE> computedValue, STATE currentState) {
		VALUE evalValue = computedValue.getValue();
		BooleanValue evalBool = computedValue.getBooleanValue();

		switch (mOperator) {
		case ARITHNEGATIVE:
			evalValue = computedValue.getValue().negate();
			break;
		case LOGICNEG:
			evalBool = computedValue.getBooleanValue().neg();
			break;
		default:
			throw new UnsupportedOperationException(
			        new StringBuilder().append("Operator ").append(mOperator).append(" not supported.").toString());
		}

		final NonrelationalEvaluationResult<VALUE> evalResult = new NonrelationalEvaluationResult<>(evalValue,
		        evalBool);
		return mSubEvaluator.inverseEvaluate(evalResult, currentState);
	}

	@Override
	public void addSubEvaluator(IEvaluator<VALUE, STATE, CodeBlock> evaluator) {
		assert evaluator != null;

		if (mSubEvaluator == null) {
			mSubEvaluator = evaluator;
		} else {
			throw new UnsupportedOperationException("Cannot add more evaluators to this unary expression evaluator.");
		}
	}

	@Override
	public Set<IBoogieVar> getVarIdentifiers() {
		return mSubEvaluator.getVarIdentifiers();
	}

	@Override
	public boolean hasFreeOperands() {
		return mSubEvaluator == null;
	}

	@Override
	public boolean containsBool() {
		return mSubEvaluator.containsBool();
	}

	@Override
	public void setOperator(Object operator) {
		assert operator != null;
		assert operator instanceof Operator;
		mOperator = (Operator) operator;
	}

	@Override
	public int getArity() {
		return 1;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		switch (mOperator) {
		case LOGICNEG:
			sb.append('!');
			break;
		case OLD:
			sb.append("old(");
			break;
		case ARITHNEGATIVE:
			sb.append('-');
			break;
		default:
		}

		sb.append(mSubEvaluator);

		if (mOperator == Operator.OLD) {
			sb.append(')');
		}

		return sb.toString();
	}
}