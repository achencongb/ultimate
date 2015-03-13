package de.uni_freiburg.informatik.ultimatetest.traceabstraction;

import java.util.ArrayList;

import de.uni_freiburg.informatik.ultimate.plugins.generator.buchiautomizer.BuchiAutomizerModuleDecompositionBenchmark;
import de.uni_freiburg.informatik.ultimate.plugins.generator.buchiautomizer.BuchiAutomizerTimingBenchmark;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.TraceAbstractionBenchmarks;
import de.uni_freiburg.informatik.ultimate.util.Benchmark;
import de.uni_freiburg.informatik.ultimate.util.csv.ICsvProviderProvider;
import de.uni_freiburg.informatik.ultimatetest.AbstractModelCheckerTestSuite;
import de.uni_freiburg.informatik.ultimatetest.TraceAbstractionTestSummary;
import de.uni_freiburg.informatik.ultimatetest.UltimateRunDefinition;
import de.uni_freiburg.informatik.ultimatetest.decider.ITestResultDecider;
import de.uni_freiburg.informatik.ultimatetest.decider.SafetyCheckTestResultDecider;
import de.uni_freiburg.informatik.ultimatetest.evals.ColumnDefinition;
import de.uni_freiburg.informatik.ultimatetest.evals.ConversionContext;
import de.uni_freiburg.informatik.ultimatetest.evals.HTMLSummary;
import de.uni_freiburg.informatik.ultimatetest.evals.LatexDetailedSummary;
import de.uni_freiburg.informatik.ultimatetest.evals.LatexOverviewSummary;
import de.uni_freiburg.informatik.ultimatetest.evals.ColumnDefinition.Aggregate;
import de.uni_freiburg.informatik.ultimatetest.summary.CsvConcatenator;
import de.uni_freiburg.informatik.ultimatetest.summary.IIncrementalLog;
import de.uni_freiburg.informatik.ultimatetest.summary.ITestSummary;

public abstract class AbstractTraceAbstractionTestSuite extends AbstractModelCheckerTestSuite {

	@Override
	public ITestResultDecider constructITestResultDecider(UltimateRunDefinition ultimateRunDefinition) {
		return new SafetyCheckTestResultDecider(ultimateRunDefinition, true);
	}

	@Override
	protected ITestSummary[] constructTestSummaries() {
		ArrayList<Class<? extends ICsvProviderProvider<? extends Object>>> benchmarks = 
				new ArrayList<Class<? extends ICsvProviderProvider<? extends Object>>>();
		benchmarks.add(TraceAbstractionBenchmarks.class);
		benchmarks.add(Benchmark.class);

		ColumnDefinition[] columnDef = new ColumnDefinition[] { new ColumnDefinition(
				"Runtime (ns)", "Avg. runtime",
				ConversionContext.Divide(1000000000, 2, " s"), Aggregate.Sum, Aggregate.Average) };

		return new ITestSummary[] { 
				new TraceAbstractionTestSummary(this.getClass()),
				new CsvConcatenator(this.getClass(), TraceAbstractionBenchmarks.class), 
				new LatexOverviewSummary(getClass(), benchmarks, columnDef),
				new LatexDetailedSummary(getClass(), benchmarks, columnDef),
//				new CsvSummary(getClass(), benchmarks, columnDef),
				new HTMLSummary(getClass(), benchmarks, columnDef)
		};
	}

	@Override
	protected IIncrementalLog[] constructIncrementalLog() {
		return new IIncrementalLog[] { 
			new IncrementalLogWithBenchmarkResults(this.getClass()) 
				
		};
	}
}
