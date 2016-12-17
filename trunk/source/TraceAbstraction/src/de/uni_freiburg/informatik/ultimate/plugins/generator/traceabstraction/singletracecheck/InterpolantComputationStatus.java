/*
 * Copyright (C) 2016 Matthias Heizmann (heizmann@informatik.uni-freiburg.de)
 * Copyright (C) 2016 University of Freiburg
 *
 * This file is part of the ULTIMATE TraceAbstraction plug-in.
 *
 * The ULTIMATE TraceAbstraction plug-in is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ULTIMATE TraceAbstraction plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ULTIMATE TraceAbstraction plug-in. If not, see <http://www.gnu.org/licenses/>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 * If you modify the ULTIMATE TraceAbstraction plug-in, or any covered work, by linking
 * or combining it with Eclipse RCP (or a modified version of Eclipse RCP),
 * containing parts covered by the terms of the Eclipse Public License, the
 * licensors of the ULTIMATE TraceAbstraction plug-in grant you additional permission
 * to convey the resulting work.
 */
package de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singletracecheck;

/**
 * Encodes the success status of an interpolant computation.
 *
 * @author Matthias Heizmann (heizmann@informatik.uni-freiburg.de)
 */
public class InterpolantComputationStatus {
	
	public enum ItpErrorStatus {
		
		/**
		 * SMT solver is not able to interpolate the input formula, e.g., 
		 * because arrays are not supported
		 */
		SMT_SOLVER_CANNOT_INTERPOLATE_INPUT,
		
		SMT_SOLVER_CRASH,
		
		ALGORITHM_FAILED,
		
		OTHER,
		
		/**
		 * Trace was feasible, we cannot interpolate.
		 */
		TRACE_FEASIBLE,
	}

	private final boolean mComputationSuccessful;
	private final ItpErrorStatus mStatus;
	private final Exception mException;
	public InterpolantComputationStatus(final boolean computationSuccessful, final ItpErrorStatus status, final Exception exception) {
		super();
		mComputationSuccessful = computationSuccessful;
		mStatus = status;
		mException = exception;
	}
	public boolean wasComputationSuccesful() {
		return mComputationSuccessful;
	}
	public ItpErrorStatus getStatus() {
		return mStatus;
	}
	public Exception getException() {
		return mException;
	}
	
	
	

}