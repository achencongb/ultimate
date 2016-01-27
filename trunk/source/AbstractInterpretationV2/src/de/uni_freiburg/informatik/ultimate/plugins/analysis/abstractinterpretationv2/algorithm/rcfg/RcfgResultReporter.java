/*
 * Copyright (C) 2015 Daniel Dietsch (dietsch@informatik.uni-freiburg.de)
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

package de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.algorithm.rcfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.uni_freiburg.informatik.ultimate.core.services.model.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.Expression;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.Activator;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.algorithm.IResultReporter;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.RcfgProgramExecution;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.CodeBlock;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.ProgramPoint;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.RCFGEdge;
import de.uni_freiburg.informatik.ultimate.result.AllSpecificationsHoldResult;
import de.uni_freiburg.informatik.ultimate.result.UnprovableResult;
import de.uni_freiburg.informatik.ultimate.result.model.IProgramExecution;
import de.uni_freiburg.informatik.ultimate.result.model.IResult;
import de.uni_freiburg.informatik.ultimate.result.model.IProgramExecution.ProgramState;

/**
 * 
 * @author Daniel Dietsch (dietsch@informatik.uni-freiburg.de)
 *
 */
public class RcfgResultReporter implements IResultReporter<CodeBlock> {

	protected final IUltimateServiceProvider mServices;
	protected final BaseRcfgAbstractStateStorageProvider<?,?> mStorageProvider;

	public RcfgResultReporter(IUltimateServiceProvider services,
			BaseRcfgAbstractStateStorageProvider<?,?> storageProvider) {
		mServices = services;
		mStorageProvider = storageProvider;
	}

	@Override
	public void reportPossibleError(CodeBlock start, CodeBlock end) {
		final IResult result = new UnprovableResult<ProgramPoint, RCFGEdge, Expression>(Activator.PLUGIN_ID,
				(ProgramPoint) end.getTarget(), mServices.getBacktranslationService(), getProgramExecution(start, end),
				"Abstract Interpretation could reach this error location");
		mServices.getResultService().reportResult(Activator.PLUGIN_ID, result);
	}

	private IProgramExecution<RCFGEdge, Expression> getProgramExecution(CodeBlock start, CodeBlock end) {
		//TODO: Report a meaningful error trace
		//		final List<CodeBlock> trace = mStorageProvider.getErrorTrace(start, end);
		final Map<Integer, ProgramState<Expression>> map = Collections.emptyMap();
		final List<RCFGEdge> trace = new ArrayList<>();
		trace.add(start);
		trace.add(end);
		return new RcfgProgramExecution(trace, map);
	}

	@Override
	public void reportSafe(CodeBlock first) {
		reportSafe(first, "No error locations were reached.");
	}

	@Override
	public void reportSafe(CodeBlock first, String msg) {
		mServices.getResultService().reportResult(Activator.PLUGIN_ID,
				new AllSpecificationsHoldResult(Activator.PLUGIN_NAME, msg));
	}
}
