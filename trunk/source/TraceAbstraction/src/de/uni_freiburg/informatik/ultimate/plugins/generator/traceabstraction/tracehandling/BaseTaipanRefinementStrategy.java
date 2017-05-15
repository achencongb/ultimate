/*
 * Copyright (C) 2016-2017 Christian Schilling (schillic@informatik.uni-freiburg.de)
 * Copyright (C) 2016-2017 Daniel Dietsch (dietsch@informatik.uni-freiburg.de)
 * Copyright (C) 2016-2017 University of Freiburg
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
package de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.tracehandling;

import java.util.List;
import java.util.stream.Collectors;

import de.uni_freiburg.informatik.ultimate.automata.IAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.IRun;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomatonSimple;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.CfgSmtToolkit;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IIcfgTransition;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SolverBuilder;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SolverBuilder.Settings;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SolverBuilder.SolverMode;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.TermTransferrer;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicateUnifier;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.CegarAbsIntRunner;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.CegarLoopStatisticsGenerator;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.interpolantautomata.builders.IInterpolantAutomatonBuilder;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.interpolantautomata.builders.MultiTrackInterpolantAutomatonBuilder;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.predicates.PredicateFactory;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TraceAbstractionPreferenceInitializer.AssertCodeBlockOrder;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TraceAbstractionPreferenceInitializer.InterpolationTechnique;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TraceAbstractionPreferenceInitializer.RefinementStrategyExceptionBlacklist;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singletracecheck.IInterpolantGenerator;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singletracecheck.InterpolatingTraceChecker;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singletracecheck.PredicateUnifier;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singletracecheck.TraceChecker;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singletracecheck.TraceCheckerUtils.InterpolantsPreconditionPostcondition;
import de.uni_freiburg.informatik.ultimate.smtinterpol.smtlib2.SMTInterpol;
import de.uni_freiburg.informatik.ultimate.util.CoreUtil;

/**
 * {@link IRefinementStrategy} that is used by Taipan. It first tries an {@link InterpolatingTraceChecker} using
 * {@link SMTInterpol} with {@link InterpolationTechnique#Craig_TreeInterpolation}.<br>
 * If successful and the interpolant sequence is perfect, those interpolants are used.<br>
 * If not successful, it tries {@link TraceChecker} {@code Z3} and, if again not successful, {@code CVC4}.<br>
 * If none of those is successful, the strategy gives up.<br>
 * Otherwise, if the trace is infeasible, the strategy uses an {@link CegarAbsIntRunner} to construct interpolants.<br>
 * If not successful, the strategy again tries {@code Z3} and {@code CVC4}, but this time using interpolation
 * {@link InterpolationTechnique#FPandBP}.
 *
 * @author Christian Schilling (schillic@informatik.uni-freiburg.de)
 * @author Daniel Dietsch (dietsch@informatik.uni-freiburg.de)
 */
public abstract class BaseTaipanRefinementStrategy<LETTER extends IIcfgTransition<?>>
		implements IRefinementStrategy<LETTER> {
	protected static final String UNKNOWN_MODE = "Unknown mode: ";

	private final IUltimateServiceProvider mServices;
	private final ILogger mLogger;
	private final TaCheckAndRefinementPreferences<LETTER> mPrefs;
	private final PredicateFactory mPredicateFactory;
	private final PredicateUnifier mPredicateUnifierSmt;
	private final CegarAbsIntRunner<LETTER> mAbsIntRunner;
	private final AssertionOrderModulation<LETTER> mAssertionOrderModulation;
	private final IRun<LETTER, IPredicate, ?> mCounterexample;
	private final IAutomaton<LETTER, IPredicate> mAbstraction;

	private Mode mCurrentMode;

	// store if the trace has already been shown to be infeasible in a previous attempt
	private boolean mHasShownInfeasibilityBefore;

	private TraceCheckerConstructor<LETTER> mTcConstructor;
	private TraceCheckerConstructor<LETTER> mPrevTcConstructor;

	private TraceChecker mTraceChecker;
	private IInterpolantGenerator mInterpolantGenerator;
	private IInterpolantAutomatonBuilder<LETTER, IPredicate> mInterpolantAutomatonBuilder;
	private final int mIteration;
	private final CegarLoopStatisticsGenerator mCegarLoopBenchmark;

	/**
	 * @param logger
	 *            Logger.
	 * @param services
	 *            Ultimate services
	 * @param prefs
	 *            preferences
	 * @param cfgSmtToolkit
	 * @param predicateUnifier
	 *            predicate unifier
	 * @param absIntRunner
	 *            abstract interpretation runner
	 * @param assertionOrderModulation
	 *            assertion order modulation
	 * @param counterexample
	 *            counterexample
	 * @param abstraction
	 *            abstraction
	 * @param iteration
	 *            current CEGAR loop iteration
	 * @param cegarLoopBenchmark
	 *            benchmark
	 */
	public BaseTaipanRefinementStrategy(final ILogger logger, final IUltimateServiceProvider services,
			final TaCheckAndRefinementPreferences<LETTER> prefs, final CfgSmtToolkit cfgSmtToolkit,
			final PredicateFactory predicateFactory, final PredicateUnifier predicateUnifier,
			final CegarAbsIntRunner<LETTER> absIntRunner,
			final AssertionOrderModulation<LETTER> assertionOrderModulation,
			final IRun<LETTER, IPredicate, ?> counterexample, final IAutomaton<LETTER, IPredicate> abstraction,
			final int iteration, final CegarLoopStatisticsGenerator cegarLoopBenchmark) {
		mServices = services;
		mLogger = logger;
		mPrefs = prefs;
		mPredicateFactory = predicateFactory;
		mPredicateUnifierSmt = predicateUnifier;
		mAbsIntRunner = absIntRunner;
		mAssertionOrderModulation = assertionOrderModulation;
		mCounterexample = counterexample;
		mAbstraction = abstraction;
		mIteration = iteration;
		mCegarLoopBenchmark = cegarLoopBenchmark;

		mCurrentMode = getInitialMode();
	}

	protected abstract Mode getInitialMode();

	@Override
	public abstract boolean hasNextTraceChecker();

	@Override
	public void nextTraceChecker() {
		final Mode nextMode = getNextTraceCheckerMode();
		mCurrentMode = nextMode;

		// reset trace checker, interpolant generator, and constructor
		mInterpolantGenerator = null;
		resetTraceChecker();

		if (mLogger.isInfoEnabled()) {
			mLogger.info("Switched to TraceChecker mode " + mCurrentMode);
		}
	}

	protected abstract Mode getNextTraceCheckerMode();

	@Override
	public boolean hasNextInterpolantGenerator(final List<InterpolantsPreconditionPostcondition> perfectIpps,
			final List<InterpolantsPreconditionPostcondition> imperfectIpps) {
		// current policy: stop after finding one perfect interpolant sequence
		return perfectIpps.isEmpty() && hasNextInterpolantGeneratorAvailable();
	}

	protected abstract boolean hasNextInterpolantGeneratorAvailable();

	@Override
	public void nextInterpolantGenerator() {
		final Mode nextMode = getNextInterpolantGenerator();
		mCurrentMode = nextMode;

		mInterpolantGenerator = null;

		if (mLogger.isInfoEnabled()) {
			mLogger.info("Switched to InterpolantGenerator mode " + mCurrentMode);
		}
	}

	protected void resetTraceChecker() {
		mTraceChecker = null;
		mPrevTcConstructor = mTcConstructor;
		mTcConstructor = null;
	}

	/**
	 *
	 * @return
	 */
	protected abstract Mode getNextInterpolantGenerator();

	protected Mode getCurrentMode() {
		return mCurrentMode;
	}

	@Override
	public TraceChecker getTraceChecker() {
		if (mTraceChecker == null) {
			if (mTcConstructor == null) {
				mTcConstructor = constructTraceCheckerConstructor();
			}
			mTraceChecker = mTcConstructor.get();
		}
		return mTraceChecker;
	}

	@Override
	public IInterpolantGenerator getInterpolantGenerator() {
		mHasShownInfeasibilityBefore = true;
		if (mInterpolantGenerator == null) {
			mInterpolantGenerator = constructInterpolantGenerator(mCurrentMode);
		}
		return mInterpolantGenerator;
	}

	@Override
	public IInterpolantAutomatonBuilder<LETTER, IPredicate> getInterpolantAutomatonBuilder(
			final List<InterpolantsPreconditionPostcondition> perfectIpps,
			final List<InterpolantsPreconditionPostcondition> imperfectIpps) {
		if (mInterpolantAutomatonBuilder == null) {
			mInterpolantAutomatonBuilder =
					constructInterpolantAutomatonBuilder(perfectIpps, imperfectIpps, mCurrentMode);
		}
		return mInterpolantAutomatonBuilder;
	}

	private IInterpolantAutomatonBuilder<LETTER, IPredicate> constructInterpolantAutomatonBuilder(
			final List<InterpolantsPreconditionPostcondition> perfectIpps,
			final List<InterpolantsPreconditionPostcondition> imperfectIpps, final Mode mode) {
		switch (mode) {
		case ABSTRACT_INTERPRETATION:
		case SMTINTERPOL:
		case Z3_IG:
		case CVC4_IG:
			if (perfectIpps.isEmpty()) {
				// if we have only imperfect interpolants, we take the first two
				mLogger.info("Using the first two imperfect interpolant sequences");
				return new MultiTrackInterpolantAutomatonBuilder<>(mServices, mCounterexample,
						imperfectIpps.stream().limit(2).collect(Collectors.toList()), mAbstraction);
			}
			// if we have some perfect, we take one of those
			mLogger.info("Using the first perfect interpolant sequence");
			return new MultiTrackInterpolantAutomatonBuilder<>(mServices, mCounterexample,
					perfectIpps.stream().limit(1).collect(Collectors.toList()), mAbstraction);
		case Z3_NO_IG:
		case CVC4_NO_IG:
			throw new AssertionError("The mode " + mode + " should be unreachable here.");
		default:
			throw new IllegalArgumentException(UNKNOWN_MODE + mode);
		}
	}

	private TraceCheckerConstructor<LETTER> constructTraceCheckerConstructor() {
		final InterpolationTechnique interpolationTechnique = getInterpolationTechnique(mCurrentMode);
		final boolean useTimeout = mHasShownInfeasibilityBefore;

		final Mode scriptMode;
		if (CoreUtil.OS_IS_WINDOWS) {
			scriptMode = getModeForWindowsUsers();
		} else {
			scriptMode = mCurrentMode;
		}

		final ManagedScript managedScript =
				constructManagedScript(mServices, mPrefs, scriptMode, useTimeout, mIteration);

		final AssertCodeBlockOrder assertionOrder =
				mAssertionOrderModulation.reportAndGet(mCounterexample, interpolationTechnique);

		mLogger.info("Using TraceChecker mode " + mCurrentMode + " with AssertCodeBlockOrder " + assertionOrder
				+ " (IT: " + interpolationTechnique + ")");
		TraceCheckerConstructor<LETTER> result;
		if (mPrevTcConstructor == null) {
			result = new TraceCheckerConstructor<>(mPrefs, managedScript, mServices, mPredicateFactory,
					mPredicateUnifierSmt, mCounterexample, assertionOrder, interpolationTechnique, mIteration,
					mCegarLoopBenchmark);
		} else {
			result = new TraceCheckerConstructor<>(mPrevTcConstructor, managedScript, assertionOrder,
					interpolationTechnique, mCegarLoopBenchmark);
		}
		return result;
	}

	/**
	 * Because we rely on the "golden copy" of CVC4 and we only have this for Linux, windows users are screwed during
	 * debugging. To enable at least some debugging, we hack the mode if someone is a poor windows user.
	 */
	private Mode getModeForWindowsUsers() {
		final Mode modeHack;
		if (mCurrentMode == Mode.CVC4_IG || mCurrentMode == Mode.CVC4_NO_IG) {
			modeHack = getWindowsCvcReplacementMode(mCurrentMode);
		} else {
			modeHack = mCurrentMode;
		}
		if (modeHack != mCurrentMode) {
			mLogger.warn("Poor windows users use " + modeHack + " instead of " + mCurrentMode);
		}
		return modeHack;
	}

	protected abstract Mode getWindowsCvcReplacementMode(Mode cvcMode);

	protected abstract InterpolationTechnique getInterpolationTechnique(final Mode mode);

	@SuppressWarnings("squid:S1151")
	private ManagedScript constructManagedScript(final IUltimateServiceProvider services,
			final TaCheckAndRefinementPreferences<LETTER> prefs, final Mode mode, final boolean useTimeout,
			final int iteration) {
		final boolean dumpSmtScriptToFile = prefs.getDumpSmtScriptToFile();
		final String pathOfDumpedScript = prefs.getPathOfDumpedScript();
		final String baseNameOfDumpedScript =
				"Script_" + prefs.getIcfgContainer().getIdentifier() + "_Iteration" + iteration;
		final Settings solverSettings;
		final SolverMode solverMode;
		final String logicForExternalSolver;
		final String command;
		switch (mode) {
		case SMTINTERPOL:
		case ABSTRACT_INTERPRETATION:
			final long timeout = useTimeout ? TIMEOUT_SMTINTERPOL : TIMEOUT_NONE_SMTINTERPOL;
			solverSettings = new Settings(false, false, null, timeout, null, dumpSmtScriptToFile, pathOfDumpedScript,
					baseNameOfDumpedScript);
			solverMode = SolverMode.Internal_SMTInterpol;
			logicForExternalSolver = null;
			break;
		case Z3_IG:
		case Z3_NO_IG:
			command = useTimeout ? COMMAND_Z3_TIMEOUT : COMMAND_Z3_NO_TIMEOUT;
			solverSettings = new Settings(false, true, command, 0, null, dumpSmtScriptToFile, pathOfDumpedScript,
					baseNameOfDumpedScript);
			solverMode = SolverMode.External_ModelsAndUnsatCoreMode;
			logicForExternalSolver = LOGIC_Z3;
			break;
		case CVC4_IG:
		case CVC4_NO_IG:
			command = useTimeout ? COMMAND_CVC4_TIMEOUT : COMMAND_CVC4_NO_TIMEOUT;
			solverSettings = new Settings(false, true, command, 0, null, dumpSmtScriptToFile, pathOfDumpedScript,
					baseNameOfDumpedScript);
			solverMode = SolverMode.External_ModelsAndUnsatCoreMode;
			logicForExternalSolver = LOGIC_CVC4_DEFAULT;
			break;
		default:
			throw new IllegalArgumentException(UNKNOWN_MODE + mode);
		}
		final Script solver = SolverBuilder.buildAndInitializeSolver(services, prefs.getToolchainStorage(), solverMode,
				solverSettings, false, false, logicForExternalSolver, "TraceCheck_Iteration" + iteration);
		final ManagedScript result = new ManagedScript(services, solver);

		final TermTransferrer tt = new TermTransferrer(solver);
		final Term axioms = prefs.getCfgSmtToolkit().getAxioms().getFormula();
		solver.assertTerm(tt.transform(axioms));

		return result;
	}

	private IInterpolantGenerator constructInterpolantGenerator(final Mode mode) {
		switch (mode) {
		case SMTINTERPOL:
		case CVC4_IG:
			return castTraceChecker();
		case Z3_IG:
			return castTraceChecker();
		case Z3_NO_IG:
		case CVC4_NO_IG:
		case ABSTRACT_INTERPRETATION:
			mCurrentMode = Mode.ABSTRACT_INTERPRETATION;
			mAbsIntRunner.generateFixpoints(mCounterexample,
					(INestedWordAutomatonSimple<LETTER, IPredicate>) mAbstraction, mPredicateUnifierSmt);
			return mAbsIntRunner.getInterpolantGenerator();
		default:
			throw new IllegalArgumentException(UNKNOWN_MODE + mode);
		}
	}

	private IInterpolantGenerator castTraceChecker() {
		final TraceChecker traceChecker = getTraceChecker();
		assert traceChecker != null && traceChecker instanceof InterpolatingTraceChecker;
		return (InterpolatingTraceChecker) traceChecker;
	}

	@Override
	public IPredicateUnifier getPredicateUnifier() {
		if (mCurrentMode == Mode.ABSTRACT_INTERPRETATION) {
			if (getInterpolantGenerator().getInterpolantComputationStatus().wasComputationSuccesful()) {
				return getInterpolantGenerator().getPredicateUnifier();
			}
		}
		return mPredicateUnifierSmt;
	}

	@Override
	public RefinementStrategyExceptionBlacklist getExceptionBlacklist() {
		return RefinementStrategyExceptionBlacklist.UNKNOWN;
	}

	/**
	 * Current mode in this strategy.
	 *
	 * @author Christian Schilling (schillic@informatik.uni-freiburg.de)
	 */
	protected enum Mode {
		/**
		 * SMTInterpol with tree interpolation.
		 */
		SMTINTERPOL,
		/**
		 * Z3 without interpolant generation.
		 */
		Z3_NO_IG,
		/**
		 * CVC4 without interpolant generation.
		 */
		CVC4_NO_IG,
		/**
		 * Abstract interpretation.
		 */
		ABSTRACT_INTERPRETATION,
		/**
		 * Z3 with interpolant generation.
		 */
		Z3_IG,
		/**
		 * CVC4 with interpolant generation.
		 */
		CVC4_IG,
	}
}