/*
 * Copyright (C) 2014-2015 Alexander Nutz (nutz@informatik.uni-freiburg.de)
 * Copyright (C) 2014-2015 Mostafa Mahmoud Amin
 * Copyright (C) 2015 University of Freiburg
 * 
 * This file is part of the ULTIMATE CodeCheck plug-in.
 * 
 * The ULTIMATE CodeCheck plug-in is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The ULTIMATE CodeCheck plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ULTIMATE CodeCheck plug-in. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Additional permission under GNU GPL version 3 section 7:
 * If you modify the ULTIMATE CodeCheck plug-in, or any covered work, by linking
 * or combining it with Eclipse RCP (or a modified version of Eclipse RCP), 
 * containing parts covered by the terms of the Eclipse Public License, the 
 * licensors of the ULTIMATE CodeCheck plug-in grant you additional permission 
 * to convey the resulting work.
 */
package de.uni_freiburg.informatik.ultimate.plugins.generator.codecheck.preferences;

import de.uni_freiburg.informatik.ultimate.core.preferences.UltimatePreferenceInitializer;
import de.uni_freiburg.informatik.ultimate.core.preferences.UltimatePreferenceItem;
import de.uni_freiburg.informatik.ultimate.core.preferences.UltimatePreferenceItem.IUltimatePreferenceItemValidator;
import de.uni_freiburg.informatik.ultimate.core.preferences.UltimatePreferenceItem.PreferenceType;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SolverBuilder.SolverMode;
import de.uni_freiburg.informatik.ultimate.plugins.generator.codecheck.Activator;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TraceAbstractionPreferenceInitializer.INTERPOLATION;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TraceAbstractionPreferenceInitializer.UnsatCores;

public class CodeCheckPreferenceInitializer extends UltimatePreferenceInitializer {

	@Override
	protected UltimatePreferenceItem<?>[] initDefaultPreferences() {
		return new UltimatePreferenceItem<?>[] {
			/*
			 * settings concerning the kojak/impulse algorithm
			 */
			new UltimatePreferenceItem<Checker>(
					LABEL_CHECKER, DEF_CHECKER, 
					PreferenceType.Combo, Checker.values()),
			new UltimatePreferenceItem<Boolean>(
					LABEL_MEMOIZENORMALEDGECHECKS,
					DEF_MEMOIZENORMALEDGECHECKS, PreferenceType.Boolean),
			new UltimatePreferenceItem<Boolean>(
					LABEL_MEMOIZERETURNEDGECHECKS,
					DEF_MEMOIZERETURNEDGECHECKS, PreferenceType.Boolean),
			/*
			 * settings concerning the interpolation and predicate handling in general
			 */
			new UltimatePreferenceItem<INTERPOLATION>(
					LABEL_INTERPOLATIONMODE, DEF_INTERPOLATIONMODE,
					PreferenceType.Combo, INTERPOLATION.values()),
			new UltimatePreferenceItem<Boolean>(
					LABEL_INTERPOLANTCONSOLIDATION, DEF_INTERPOLANTCONSOLIDATION,
					PreferenceType.Boolean),
			new UltimatePreferenceItem<PredicateUnification>(
					LABEL_PREDICATEUNIFICATION, DEF_PREDICATEUNIFICATION,
					PreferenceType.Combo, PredicateUnification.values()),
			new UltimatePreferenceItem<EdgeCheckOptimization>(
					LABEL_EDGECHECKOPTIMIZATION, DEF_EDGECHECKOPTIMIZATION,
					PreferenceType.Combo, EdgeCheckOptimization.values()),
			/*
			 * settings concerning a separate solver for trace checks and interpolation
			 */
			new UltimatePreferenceItem<Boolean>(LABEL_USESEPARATETRACECHECKSOLVER, 
					DEF_USESEPARATETRACECHECKSOLVER, PreferenceType.Boolean),
			new UltimatePreferenceItem<SolverMode>(LABEL_CHOOSESEPARATETRACECHECKSOLVER, 
					DEF_CHOOSESEPARATETRACECHECKSOLVER, PreferenceType.Combo, SolverMode.values()),
			new UltimatePreferenceItem<String>(LABEL_SEPARATETRACECHECKSOLVERCOMMAND, 
					DEF_SEPARATETRACECHECKSOLVERCOMMAND, PreferenceType.String),
			new UltimatePreferenceItem<String>(LABEL_SEPARATETRACECHECKSOLVERTHEORY, 
					DEF_SEPARATETRACECHECKSOLVERTHEORY, PreferenceType.String),
			new UltimatePreferenceItem<Boolean>(LABEL_USEFALLBACKFORSEPARATETRACECHECKSOLVER, 
					DEF_USEFALLBACKFORSEPARATETRACECHECKSOLVER, PreferenceType.Boolean),
			/*
			 * settings concerning betim interpolation
			 */
			new UltimatePreferenceItem<UnsatCores>(LABEL_UNSAT_CORES, UnsatCores.CONJUNCT_LEVEL,
					PreferenceType.Combo, UnsatCores.values()),
			new UltimatePreferenceItem<Boolean>(LABEL_LIVE_VARIABLES, true, PreferenceType.Boolean),
			/*
			 * settings used mostly for debugging
			 */
			new UltimatePreferenceItem<String>(LABEL_GRAPHWRITERPATH,
					DEF_GRAPHWRITERPATH, PreferenceType.Directory),
			new UltimatePreferenceItem<Integer>(LABEL_TIMEOUT, DEF_TIMEOUT,
					PreferenceType.Integer,
					new IUltimatePreferenceItemValidator.IntegerValidator(
							0, 100000)),
			new UltimatePreferenceItem<Integer>(LABEL_ITERATIONS, DEF_ITERATIONS,
					PreferenceType.Integer, new IUltimatePreferenceItemValidator.IntegerValidator(
							-1, 100000)),
			/*
			 * settings concerning only impulse
			 */
			new UltimatePreferenceItem<RedirectionStrategy>(LABEL_REDIRECTION, DEF_REDIRECTION,
					PreferenceType.Combo, RedirectionStrategy.values()),
			new UltimatePreferenceItem<Boolean>(LABEL_DEF_RED,
					DEF_DEF_RED, PreferenceType.Boolean),
			new UltimatePreferenceItem<Boolean>(LABEL_CHK_SAT,
					DEF_CHK_SAT, PreferenceType.Boolean),
			new UltimatePreferenceItem<Boolean>(LABEL_RM_FALSE,
					DEF_RM_FALSE, PreferenceType.Boolean),
		};
	}

	@Override
	protected String getPlugID() {
		return Activator.s_PLUGIN_ID;
	}

	@Override
	public String getPreferencePageTitle() {
		return "CodeCheck";
	}

//	public enum SolverAndInterpolator {
//		SMTINTERPOL, Z3SPWP
//	}

	public enum EdgeCheckOptimization {
		NONE, PUSHPOP, SDEC, PUSHPOP_SDEC
	}

	public enum PredicateUnification {
		PER_ITERATION, PER_VERIFICATION, NONE
	}

//	public enum Solver {
//		Z3, SMTInterpol
//	}
	
	public enum Checker {
		ULTIMATE, IMPULSE
	}
	
	public enum RedirectionStrategy {
		No_Strategy, FIRST, RANDOM, RANDOM_STRONGEST
	}


	/*
	 * labels for the different preferencess
	 */
//	public static final String LABEL_ONLYMAINPROCEDURE = "only verify starting from main procedure";
	
	public static final String LABEL_CHECKER = "the checking algorithm to use";
	
	public static final String LABEL_MEMOIZENORMALEDGECHECKS = "memoize already made edge checks for non-return edges";
	public static final String LABEL_MEMOIZERETURNEDGECHECKS = "memoize already made edge checks for return edges";

	public static final String LABEL_SOLVERANDINTERPOLATOR = "Interpolating solver";
	public static final String LABEL_INTERPOLATIONMODE = "interpolation mode";
	public static final String LABEL_INTERPOLANTCONSOLIDATION = "use interpolant consolidation (only useful for interpolationmode fp+bp)";
	public static final String LABEL_PREDICATEUNIFICATION = "Predicate Unification Mode";
	public static final String LABEL_EDGECHECKOPTIMIZATION = "EdgeCheck Optimization Mode";

	public static final String LABEL_GRAPHWRITERPATH = "write dot graph files here (empty for don't write)";

	public static final String LABEL_TIMEOUT = "Timeout in seconds";
	
	public static final String LABEL_ITERATIONS = "Limit maxmium number of iterations. (-1 for no limitations)";

	public static final String LABEL_REDIRECTION = "The redirection strategy for Impulse";
	
	public static final String LABEL_DEF_RED = "Default Redirection";
	
	public static final String LABEL_RM_FALSE = "Remove False Nodes Manually";
	
	public static final String LABEL_CHK_SAT = "Check edges satisfiability";
	
	public static final String LABEL_USESEPARATETRACECHECKSOLVER = "Use separate solver for tracechecks";
	public static final String LABEL_CHOOSESEPARATETRACECHECKSOLVER= "Choose which separate solver to use for tracechecks";
	public static final String LABEL_SEPARATETRACECHECKSOLVERCOMMAND = "Command for calling external solver";
	public static final String LABEL_SEPARATETRACECHECKSOLVERTHEORY = "Theory for external solver";
	public static final String LABEL_USEFALLBACKFORSEPARATETRACECHECKSOLVER = "Use standard solver (from RCFGBuilder) with FP interpolation as fallback";
	
	public static final String LABEL_UNSAT_CORES = "Use unsat cores in FP/BP interpolation";
	public static final String LABEL_LIVE_VARIABLES = "Use live variables in FP/BP interpolation";	
	
	
	/*
	 * default values for the different preferences
	 */
	public static final Checker DEF_CHECKER = Checker.ULTIMATE;
	
//	public static final boolean DEF_ONLYMAINPROCEDURE = false;
	public static final boolean DEF_MEMOIZENORMALEDGECHECKS = true;
	public static final boolean DEF_MEMOIZERETURNEDGECHECKS = true;

//	public static final SolverAndInterpolator DEF_SOLVERANDINTERPOLATOR = SolverAndInterpolator.Z3SPWP;
	public static final INTERPOLATION DEF_INTERPOLATIONMODE = INTERPOLATION.Craig_TreeInterpolation;
	public static final boolean DEF_INTERPOLANTCONSOLIDATION = false;
	public static final PredicateUnification DEF_PREDICATEUNIFICATION = PredicateUnification.PER_ITERATION;
	public static final EdgeCheckOptimization DEF_EDGECHECKOPTIMIZATION = EdgeCheckOptimization.NONE;

	public static final String DEF_GRAPHWRITERPATH = "";

	public static final int DEF_TIMEOUT = 1000000;
	
	public static final int DEF_ITERATIONS = -1;
	
	public static final RedirectionStrategy DEF_REDIRECTION = RedirectionStrategy.RANDOM_STRONGEST;
	
	public static final boolean DEF_DEF_RED = false;
	
	public static final boolean DEF_RM_FALSE = false;
	
	public static final boolean DEF_CHK_SAT = false;

	public static final boolean DEF_USESEPARATETRACECHECKSOLVER = true;
	public static final SolverMode DEF_CHOOSESEPARATETRACECHECKSOLVER = SolverMode.Internal_SMTInterpol;
	public static final String DEF_SEPARATETRACECHECKSOLVERCOMMAND = "";
	public static final String DEF_SEPARATETRACECHECKSOLVERTHEORY = "QF_AUFLIA";
	public static final boolean DEF_USEFALLBACKFORSEPARATETRACECHECKSOLVER = true;

}
