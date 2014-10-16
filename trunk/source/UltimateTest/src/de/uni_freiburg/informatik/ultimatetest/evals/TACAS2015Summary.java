package de.uni_freiburg.informatik.ultimatetest.evals;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.core.services.IResultService;
import de.uni_freiburg.informatik.ultimate.util.Utils;
import de.uni_freiburg.informatik.ultimate.util.csv.CsvUtils;
import de.uni_freiburg.informatik.ultimate.util.csv.CsvUtils.IExplicitConverter;
import de.uni_freiburg.informatik.ultimate.util.csv.ICsvProvider;
import de.uni_freiburg.informatik.ultimate.util.csv.ICsvProviderProvider;
import de.uni_freiburg.informatik.ultimate.util.csv.SimpleCsvProvider;
import de.uni_freiburg.informatik.ultimatetest.UltimateRunDefinition;
import de.uni_freiburg.informatik.ultimatetest.UltimateTestSuite;
import de.uni_freiburg.informatik.ultimatetest.decider.ITestResultDecider.TestResult;
import de.uni_freiburg.informatik.ultimatetest.summary.NewTestSummary;
import de.uni_freiburg.informatik.ultimatetest.util.Util;

/**
 * 
 * 
 * @author dietsch@informatik.uni-freiburg.de
 * 
 */
public class TACAS2015Summary extends NewTestSummary {

	private final Collection<Class<? extends ICsvProviderProvider<? extends Object>>> mBenchmarks;
	private final LinkedHashMap<UltimateRunDefinition, ICsvProvider<?>> mCsvProvider;
	private int mCsvConversionGoneWrong;

	public TACAS2015Summary(Class<? extends UltimateTestSuite> ultimateTestSuite,
			Collection<Class<? extends ICsvProviderProvider<? extends Object>>> benchmarks) {
		super(ultimateTestSuite);
		mBenchmarks = benchmarks;
		mCsvProvider = new LinkedHashMap<>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addResult(UltimateRunDefinition urd, TestResult threeValuedResult, String category, String message,
			String testname, IResultService resultService) {
		super.addResult(urd, threeValuedResult, category, message, testname, resultService);
		if (resultService == null) {
			return;
		}
		ICsvProvider<Object> aggregate = new SimpleCsvProvider<Object>(new ArrayList<String>());
		for (Class<? extends ICsvProviderProvider<? extends Object>> benchmark : mBenchmarks) {
			for (ICsvProviderProvider<?> benchmarkResultWildcard : Util.getCsvProviderProviderFromUltimateResults(
					resultService.getResults(), benchmark)) {
				aggregate = CsvUtils.concatenateRows(aggregate,
						(ICsvProvider<Object>) benchmarkResultWildcard.createCvsProvider());
			}
		}
		add(urd, aggregate);
	}

	private void add(UltimateRunDefinition urd, ICsvProvider<?> benchmarkCsvWithRunDefinition) {
		mCsvProvider.put(urd, benchmarkCsvWithRunDefinition);
	}

	@Override
	public String getSummaryLog() {
		StringBuilder sb = new StringBuilder();
		PartitionedResults results = partitionResults(mResults.entrySet());

		ICsvProvider<String> csvTotal = makePrintCsvProviderFromResults(results.All);
		printCsv(sb, "CSV", csvTotal);

		mCsvConversionGoneWrong = 0;

		sb.append("################################# Summary #######################").append(
				Util.getPlatformLineSeparator());
		sb.append(results);
		sb.append(Util.getPlatformLineSeparator());
		sb.append("CSV conversion gone wrong: ").append(mCsvConversionGoneWrong);
		sb.append(Util.getPlatformLineSeparator());
		sb.append(Util.getPlatformLineSeparator());

		// sb.append("################################# HTML #######################").append(
		// Util.getPlatformLineSeparator());
		//
		// CsvUtils.toHTML(csvSafe, sb, true, null);
		// sb.append(Util.getPlatformLineSeparator());
		// CsvUtils.toHTML(badCsv, sb, true, null);
		// sb.append(Util.getPlatformLineSeparator());

		sb.append("################################# Latex #######################").append(
				Util.getPlatformLineSeparator());

		makeTables(sb, results);

		return sb.toString();
	}

	private void makeTables(StringBuilder sb, PartitionedResults results) {

		Set<String> tools = Util.reduceDistinct(results.All, new IMyReduce<String>() {
			@Override
			public String reduce(Entry<UltimateRunDefinition, ExtendedResult> entry) {
				return entry.getKey().getToolchain().getName();
			}
		});

		String br = Util.getPlatformLineSeparator();
		// define commands
		sb.append("\\newcommand{\\headcolor}{}").append(br);
		sb.append("\\newcommand{\\header}[1]{\\parbox{2.8em}{\\centering #1}\\headcolor}").append(br);
		sb.append("\\newcommand{\\folder}[1]{\\parbox{5em}{#1}}").append(br);

		for (final String tool : tools) {
			// make table header
			sb.append("\\begin{table}").append(br);
			sb.append("\\centering").append(br);
			sb.append("\\resizebox{\\linewidth}{!}{%").append(br);
			sb.append("\\begin{tabu} to \\linewidth {lcllcccccccccc}").append(br);
			sb.append("\\toprule").append(br);
			sb.append("  \\header{}& ").append(br);
			sb.append("  \\header{Count}&").append(br);
			sb.append("  \\header{Result}&").append(br);
			sb.append("  \\header{Variant}& ").append(br);
			sb.append("  \\header{Count}&").append(br);
			sb.append("  \\header{Avg. runtime}&").append(br);
			sb.append("  \\header{Mem{-}ory}&").append(br);
			sb.append("  \\header{Iter{-}ations}&").append(br);
			sb.append("  \\header{Loc{-}ations}&").append(br);
			sb.append("  \\header{Pred. size}&").append(br);
			sb.append("  \\header{Conj. SSA}&").append(br);
			sb.append("  \\header{Conj. IC}&").append(br);
			sb.append("  \\header{ICC}\\\\").append(br);
			sb.append("  \\cmidrule(r){2-14}").append(br);

			// make table body
			PartitionedResults resultsPerTool = partitionResults(Util.where(results.All,
					new ITestSummaryResultPredicate() {
						@Override
						public boolean check(Entry<UltimateRunDefinition, ExtendedResult> entry) {
							return entry.getKey().getToolchain().getName().equals(tool);
						}
					}));
			makeTableBody(sb, resultsPerTool, tool);

			// end table
			sb.append("\\end{tabu}}").append(br);
			sb.append("\\caption{Results for ").append(tool).append(".}").append(br);
			sb.append("\\end{table}").append(br);
		}
	}

	private void makeTableBody(StringBuilder sb, PartitionedResults results, String toolname) {
		// make header

		Set<String> folders = Util.reduceDistinct(results.All, new IMyReduce<String>() {
			@Override
			public String reduce(Entry<UltimateRunDefinition, ExtendedResult> entry) {
				return entry.getKey().getInput().getParentFile().getName();
			}
		});

		int i = 0;
		for (final String folder : folders) {
			PartitionedResults resultsPerFolder = partitionResults(Util.where(results.All,
					new ITestSummaryResultPredicate() {
						@Override
						public boolean check(Entry<UltimateRunDefinition, ExtendedResult> entry) {
							return entry.getKey().getInput().getParentFile().getName().equals(folder);
						}
					}));
			i++;
			makeFolderRow(sb, resultsPerFolder, folder, i >= folders.size());
		}

	}

	private void makeFolderRow(StringBuilder sb, PartitionedResults results, String folder, boolean last) {
		String br = Util.getPlatformLineSeparator();

		List<String> variants = new ArrayList<>(Util.reduceDistinct(results.All, new IMyReduce<String>() {
			@Override
			public String reduce(Entry<UltimateRunDefinition, ExtendedResult> entry) {
				return entry.getKey().getSettings().getName();
			}
		}));

		// folder name
		sb.append("\\multirow{");
		sb.append(variants.size() * 5);
		sb.append("}{*}{\\folder{");
		sb.append(folder);
		sb.append("}} &").append(br);

		// count expected unsafe & row header unsafe
		sb.append("\\multirow{");
		sb.append(variants.size());
		sb.append("}{*}{");
		sb.append(results.ExpectedUnsafe / variants.size());
		sb.append("} &").append(br);
		sb.append("\\multirow{");
		sb.append(variants.size());
		sb.append("}{*}{\\folder{Unsafe}} ").append(br);

		// results unsafe
		for (int i = 0; i < variants.size(); ++i) {
			makeVariantEntry(sb, results.Unsafe, variants.get(i), i == 0);
		}

		// count expected safe & row header safe
		sb.append("& \\multirow{");
		sb.append(variants.size());
		sb.append("}{*}{");
		sb.append(results.ExpectedSafe / variants.size());
		sb.append("} &").append(br);
		sb.append("\\multirow{");
		sb.append(variants.size());
		sb.append("}{*}{\\folder{Safe}} ").append(br);

		// results safe
		for (int i = 0; i < variants.size(); ++i) {
			makeVariantEntry(sb, results.Safe, variants.get(i), i == 0);
		}

		// row timeout
		sb.append("& & \\multirow{");
		sb.append(variants.size());
		sb.append("}{*}{\\folder{Timeout}} ").append(br);
		for (int i = 0; i < variants.size(); ++i) {
			makeVariantEntry(sb, results.Timeout, variants.get(i), i == 0);
		}

		// row error
		sb.append("& & \\multirow{");
		sb.append(variants.size());
		sb.append("}{*}{\\folder{Error}} ").append(br);
		for (int i = 0; i < variants.size(); ++i) {
			makeVariantEntry(sb, results.Error, variants.get(i), i == 0);
		}

		// count total & row header total
		sb.append("& \\multirow{");
		sb.append(variants.size());
		sb.append("}{*}{");
		sb.append(results.All.size() / variants.size());
		sb.append("} &").append(br);
		sb.append("\\multirow{");
		sb.append(variants.size());
		sb.append("}{*}{\\folder{Total}} ").append(br);
		for (int i = 0; i < variants.size(); ++i) {
			makeVariantEntry(sb, results.All, variants.get(i), i == 0);
		}

		if (last) {
			sb.append("\\bottomrule").append(br);
			sb.append("& & & & & & & & & & & & & \\\\").append(br);
		} else {
			sb.append("\\midrule").append(br);
		}
	}

	private void makeVariantEntry(StringBuilder sb, Collection<Entry<UltimateRunDefinition, ExtendedResult>> current,
			final String variant, boolean isFirst) {

		Collection<Entry<UltimateRunDefinition, ExtendedResult>> results = Util.where(current,
				new ITestSummaryResultPredicate() {
					@Override
					public boolean check(Entry<UltimateRunDefinition, ExtendedResult> entry) {
						return entry.getKey().getSettings().getName().equals(variant);
					}
				});

		String br = Util.getPlatformLineSeparator();
		String sep = " & ";

		if (isFirst) {
			sb.append(sep);
		} else {
			sb.append(sep).append(sep).append(sep);
		}
		sb.append(variant);
		sb.append(sep);

		ICsvProvider<String> csv = makePrintCsvProviderFromResults(results);
		csv = CsvUtils.projectColumn(csv, Arrays.asList(new String[] { "Runtime (ns)", "Allocated memory end (bytes)",
				"Overall iterations", "NumberOfCodeBlocks", "SizeOfPredicates", "Conjuncts in SSA",
				"Conjuncts in UnsatCore", "ICC" }));

		csv = reduceProvider(csv, null, null, csv.getColumnTitles());
		csv = makeHumanReadable(csv);
		csv = CsvUtils.addColumn(csv, "Count", 0, Arrays.asList(new String[] { Integer.toString(results.size()) }));
		int i = 0;
		List<String> row = csv.getRow(0);
		if (row == null || row.size() < 9) {
			// no results in this category, just fill with empty fields
			for (; i < 9; ++i) {
				sb.append(sep);
			}
			sb.append("\\\\");

		} else {
			for (String cell : row) {
				if (isInvalidForLatex(cell)) {
					sb.append("-");
				} else {
					sb.append(cell);
				}

				if (i < 9) {
					if (i < row.size() - 1) {
						sb.append(sep);
					} else {
						sb.append("\\\\");
					}
				} else {
					// TODO: Too much stuff in csv provider
					sb.append("\\\\");
					break;
				}
				i++;
			}
		}
		sb.append(br);
	}

	/**
	 * Works only for TACAS tables
	 */
	private ICsvProvider<String> makeHumanReadable(ICsvProvider<String> csv) {

		ICsvProvider<String> newProvider = new SimpleCsvProvider<>(csv.getColumnTitles());
		List<String> newRow = new ArrayList<>();
		List<String> oldRow = csv.getRow(0);
		// Runtime: ns to s
		newRow.add(makeHumanReadableDivide(oldRow.get(0), "1000000000", " s"));
		// Peak memory consumption: bytes to MB
		newRow.add(makeHumanReadableDivide(oldRow.get(1), "1048576", " MB"));
		// Overall iterations: convert to closest readable
		newRow.add(makeHumanReadableNumber(oldRow.get(2)));
		// NumberOfCodeBlocks: convert to closest readable
		newRow.add(makeHumanReadableNumber(oldRow.get(3)));
		// SizeOfPredicates: convert to closest readable
		newRow.add(makeHumanReadableNumber(oldRow.get(4)));
		// Conjuncts in SSA: convert to closest readable
		newRow.add(makeHumanReadableNumber(oldRow.get(5)));
		// Conjuncts in UnsatCore: convert to closest readable
		newRow.add(makeHumanReadableNumber(oldRow.get(6)));
		// ICC: make percent
		newRow.add(makeHumanReadablePercent(oldRow.get(7)));

		newProvider.addRow(newRow);
		return newProvider;
	}

	private String makeHumanReadableDivide(String input, String divisor, String unit) {
		BigDecimal current = convert(input);
		if (current == null) {
			return "-";
		}

		try {
			return current.divide(new BigDecimal(divisor)).setScale(2, RoundingMode.HALF_UP).toString() + unit;
		} catch (Exception ex) {
			return "NaN";
		}
	}

	private String makeHumanReadablePercent(String input) {
		BigDecimal current = convert(input);
		if (current == null) {
			return "-";
		}

		try {
			return current.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP).toString() + "\\%";
		} catch (Exception ex) {
			return "NaN";
		}
	}

	private String makeHumanReadableNumber(String input) {
		BigDecimal current = convert(input);
		if (current == null) {
			return "-";
		}

		try {
			return Utils.humanReadableNumber(current.longValue());
		} catch (Exception ex) {
			return "NaN";
		}
	}

	private BigDecimal convert(String input) {
		try {
			return new BigDecimal(input);
		} catch (Exception ex) {
			return null;
		}
	}

	private boolean isInvalidForLatex(String cell) {
		return cell == null || cell.contains("[");
	}

	private void printCsv(StringBuilder sb, String header, ICsvProvider<String> provider) {
		sb.append("################################# ").append(header).append(" #######################")
				.append(Util.getPlatformLineSeparator());
		provider.toCsv(sb, null);
		sb.append(Util.getPlatformLineSeparator());
		sb.append(Util.getPlatformLineSeparator());
	}

	private ICsvProvider<String> makePrintCsvProviderFromResults(
			Collection<Entry<UltimateRunDefinition, ExtendedResult>> goodResults) {
		ICsvProvider<String> current = new SimpleCsvProvider<>(new ArrayList<String>());
		for (Entry<UltimateRunDefinition, ExtendedResult> entry : goodResults) {
			ICsvProvider<?> provider = mCsvProvider.get(entry.getKey());
			if (provider == null) {
				mCsvConversionGoneWrong++;
				continue;
			}
			current = CsvUtils.concatenateRows(current,
					preparePrintProvider(provider, entry.getKey(), entry.getValue().Message));
		}
		return current;
	}

	private String[] columnsToKeep = new String[] { "Runtime (ns)", "Allocated memory end (bytes)",
			"Max. memory available (bytes)", "Overall iterations", "NumberOfCodeBlocks", "SizeOfPredicates",
			"Conjuncts in SSA", "Conjuncts in UnsatCore", "ICC %" };

	private ICsvProvider<String> preparePrintProvider(ICsvProvider<?> provider, UltimateRunDefinition urd,
			String message) {
		List<String> names = new ArrayList<>(provider.getColumnTitles());
		for (String name : names) {
			int idx = name.indexOf("TraceCheckerBenchmark_");
			if (idx != -1) {
				provider.renameColumnTitle(name, name.substring(22));
			}
		}

		provider = CsvUtils.projectColumn(provider, columnsToKeep);
		provider.renameColumnTitle("ICC %", "ICC");

		ICsvProvider<String> newProvider = reduceProvider(provider, Arrays.asList(new String[] { "Runtime (ns)", }),
				Arrays.asList(new String[] { "Allocated memory end (bytes)", "Max. memory available (bytes)", }), null);
		newProvider = addUltimateRunDefinition(urd, message, newProvider);
		return newProvider;
	}

	private ICsvProvider<String> reduceProvider(ICsvProvider<?> provider, Collection<String> columnsToSum,
			Collection<String> columnsToMax, Collection<String> columnsToAverage) {

		final HashSet<String> sum;
		if (columnsToSum != null) {
			sum = new HashSet<>(columnsToSum);
		} else {
			sum = new HashSet<>();
		}

		final HashSet<String> max;
		if (columnsToSum != null) {
			max = new HashSet<>(columnsToMax);
		} else {
			max = new HashSet<>();
		}

		final HashSet<String> avg;
		if (columnsToAverage != null) {
			avg = new HashSet<>(columnsToAverage);
		} else {
			avg = new HashSet<>();
		}

		ICsvProvider<String> newProvider = CsvUtils.convertComplete(provider,
				new IExplicitConverter<ICsvProvider<?>, ICsvProvider<String>>() {
					@Override
					public ICsvProvider<String> convert(ICsvProvider<?> input) {
						ICsvProvider<String> rtr = new SimpleCsvProvider<>(input.getColumnTitles());
						List<String> newRow = new ArrayList<>();

						int idx = 0;

						for (String columnTitle : input.getColumnTitles()) {
							String finalValue = null;
							int intValue = 0;
							double doubleValue = 0;
							float floatValue = 0;
							long longValue = 0;
							BigDecimal numberValue = BigDecimal.ZERO;
							if (sum.contains(columnTitle)) {
								for (List<?> row : input.getTable()) {
									Object cell = row.get(idx);
									if (cell == null) {
										continue;
									} else if (cell instanceof Double) {
										doubleValue += (Double) cell;
										finalValue = Double.toString(doubleValue);
									} else if (cell instanceof Integer) {
										intValue += (Integer) cell;
										finalValue = Integer.toString(intValue);
									} else if (cell instanceof Long) {
										longValue += (Long) cell;
										finalValue = Long.toString(longValue);
									} else if (cell instanceof Float) {
										floatValue += (Float) cell;
										finalValue = Float.toString(floatValue);
									} else if (cell instanceof String) {
										try {
											numberValue = numberValue.add(new BigDecimal((String) cell));
											finalValue = numberValue.toString();
										} catch (Exception ex) {
											finalValue = cell.toString();
										}
									} else {
										finalValue = cell.toString();
									}
								}
							} else if (max.contains(columnTitle)) {
								for (List<?> row : input.getTable()) {
									Object cell = row.get(idx);
									if (cell == null) {
										continue;
									} else if (cell instanceof Double) {
										doubleValue = Math.max(doubleValue, (Double) cell);
										finalValue = Double.toString(doubleValue);
									} else if (cell instanceof Integer) {
										intValue = Math.max(intValue, (Integer) cell);
										finalValue = Integer.toString(intValue);
									} else if (cell instanceof Long) {
										longValue = Math.max(longValue, (Long) cell);
										finalValue = Long.toString(longValue);
									} else if (cell instanceof Float) {
										floatValue = Math.max(floatValue, (Float) cell);
										finalValue = Float.toString(floatValue);
									} else if (cell instanceof String) {
										try {
											numberValue = numberValue.max(new BigDecimal((String) cell));
											finalValue = numberValue.toString();
										} catch (Exception ex) {
											finalValue = cell.toString();
										}
									} else {
										finalValue = cell.toString();
									}
								}
							} else if (avg.contains(columnTitle)) {
								int size = input.getTable().size();
								for (List<?> row : input.getTable()) {
									Object cell = row.get(idx);
									if (cell == null) {
										continue;
									} else if (cell instanceof Double) {
										doubleValue += (Double) cell;
										finalValue = Double.toString(doubleValue / (double) size);
									} else if (cell instanceof Integer) {
										intValue += (Integer) cell;
										finalValue = Integer.toString(intValue / size);
									} else if (cell instanceof Long) {
										longValue += (Long) cell;
										finalValue = Long.toString(longValue / (long) size);
									} else if (cell instanceof Float) {
										floatValue += (Float) cell;
										finalValue = Float.toString(floatValue / (float) size);
									} else if (cell instanceof String) {
										try {
											numberValue = numberValue.add(new BigDecimal((String) cell));
											finalValue = numberValue.divide(new BigDecimal(size), 5,
													RoundingMode.HALF_UP).toString();
										} catch (Exception ex) {
											finalValue = cell.toString();
										}
									} else {
										finalValue = cell.toString();
									}
								}
							} else {
								for (List<?> row : input.getTable()) {
									Object cell = row.get(idx);
									if (cell == null) {
										continue;
									} else {
										finalValue = cell.toString();
									}
								}
							}
							idx++;
							newRow.add(finalValue);
						}
						rtr.addRow(newRow);
						return rtr;
					}
				});
		return newProvider;
	}

	// private String[] columnsToKeep = new String[] { "Runtime (ns)",
	// "Peak memory consumption (bytes)",
	// "Max. memory available (bytes)", "Overall iterations",
	// "TraceCheckerBenchmark_NumberOfCodeBlocks",
	// "TraceCheckerBenchmark_SizeOfPredicates",
	// "TraceCheckerBenchmark_Conjuncts in SSA",
	// "TraceCheckerBenchmark_Conjuncts in UnsatCore",
	// "InterpolantCoveringCapability", "#iterations" };

	private ICsvProvider<String> addUltimateRunDefinition(UltimateRunDefinition ultimateRunDefinition, String message,
			ICsvProvider<String> benchmark) {
		List<String> resultColumns = new ArrayList<>();
		resultColumns.add("Folder");
		resultColumns.add("File");
		resultColumns.add("Settings");
		resultColumns.add("Toolchain");
		resultColumns.add("Message");
		resultColumns.addAll(benchmark.getColumnTitles());
		ICsvProvider<String> result = new SimpleCsvProvider<>(resultColumns);
		int rows = benchmark.getRowHeaders().size();
		for (int i = 0; i < rows; i++) {
			List<String> resultRow = new ArrayList<>();
			resultRow.add(ultimateRunDefinition.getInput().getParentFile().getName());
			resultRow.add(ultimateRunDefinition.getInput().getName());
			resultRow.add(ultimateRunDefinition.getSettings().getName());
			resultRow.add(ultimateRunDefinition.getToolchain().getName());
			resultRow.add(message);
			resultRow.addAll(benchmark.getRow(i));
			result.addRow(resultRow);
		}
		return result;
	}

	private PartitionedResults partitionResults(Collection<Entry<UltimateRunDefinition, ExtendedResult>> all) {
		final LinkedHashSet<Entry<UltimateRunDefinition, ExtendedResult>> goodResults = new LinkedHashSet<>();
		goodResults.addAll(Util.where(all, new ITestSummaryResultPredicate() {
			@Override
			public boolean check(Entry<UltimateRunDefinition, ExtendedResult> entry) {
				return entry.getValue().Result == TestResult.SUCCESS;
			}
		}));

		final LinkedHashSet<Entry<UltimateRunDefinition, ExtendedResult>> timeoutResults = new LinkedHashSet<>();
		timeoutResults.addAll(Util.where(all, new ITestSummaryResultPredicate() {
			@Override
			public boolean check(Entry<UltimateRunDefinition, ExtendedResult> entry) {
				return (entry.getValue().Result == TestResult.UNKNOWN && entry.getValue().Message.toLowerCase()
						.contains("timeout"));
			}
		}));
		Collection<Entry<UltimateRunDefinition, ExtendedResult>> errorResults = Util.where(all,
				new ITestSummaryResultPredicate() {
					@Override
					public boolean check(Entry<UltimateRunDefinition, ExtendedResult> entry) {
						return !goodResults.contains(entry) && !timeoutResults.contains(entry);
					}
				});

		final LinkedHashSet<Entry<UltimateRunDefinition, ExtendedResult>> unsafeResults = new LinkedHashSet<>();
		unsafeResults.addAll(Util.where(goodResults, new ITestSummaryResultPredicate() {
			@Override
			public boolean check(Entry<UltimateRunDefinition, ExtendedResult> entry) {
				return entry.getValue().Message.contains("UNSAFE");
			}
		}));

		Collection<Entry<UltimateRunDefinition, ExtendedResult>> safeResults = Util.where(goodResults,
				new ITestSummaryResultPredicate() {
					@Override
					public boolean check(Entry<UltimateRunDefinition, ExtendedResult> entry) {
						return !unsafeResults.contains(entry);
					}
				});
		PartitionedResults rtr = new PartitionedResults();

		int expectedSafe = 0;
		int expectedUnsafe = 0;
		for (Entry<UltimateRunDefinition, ExtendedResult> entry : all) {
			if (entry.getValue().Message.contains("ExpectedResult: UNSAFE")) {
				expectedUnsafe++;
			}
			if (entry.getValue().Message.contains("ExpectedResult: SAFE")) {
				expectedSafe++;
			}
		}

		rtr.All = all;
		rtr.Timeout = timeoutResults;
		rtr.Error = errorResults;
		rtr.Unsafe = unsafeResults;
		rtr.Safe = safeResults;
		rtr.ExpectedSafe = expectedSafe;
		rtr.ExpectedUnsafe = expectedUnsafe;

		return rtr;
	}

	private class PartitionedResults {
		Collection<Entry<UltimateRunDefinition, ExtendedResult>> All;
		Collection<Entry<UltimateRunDefinition, ExtendedResult>> Timeout;
		Collection<Entry<UltimateRunDefinition, ExtendedResult>> Error;
		Collection<Entry<UltimateRunDefinition, ExtendedResult>> Unsafe;
		Collection<Entry<UltimateRunDefinition, ExtendedResult>> Safe;
		int ExpectedSafe;
		int ExpectedUnsafe;

		private PartitionedResults() {

		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("Safe: ").append(Safe.size());
			sb.append(Util.getPlatformLineSeparator());
			sb.append("Unsafe: ").append(Unsafe.size());
			sb.append(Util.getPlatformLineSeparator());
			sb.append("Timeout: ").append(Timeout.size());
			sb.append(Util.getPlatformLineSeparator());
			sb.append("Error: ").append(Error.size());
			sb.append(Util.getPlatformLineSeparator());
			sb.append("Expected Safe: ").append(ExpectedSafe);
			sb.append(Util.getPlatformLineSeparator());
			sb.append("Expected Unsafe: ").append(ExpectedUnsafe);
			sb.append(Util.getPlatformLineSeparator());
			sb.append("Total: ").append(All.size());
			return sb.toString();
		}

	}

}
