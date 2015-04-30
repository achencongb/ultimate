#!/usr/bin/perl -i
# Perl script that does minor modifications in the SMT scripts that I want to
# submit to the unsat core track.
#
# In the bash shell you can apply this script to all files in the folder using the
# for i in *.smt2 ; do perl THIS_SCRIPT.pl $i; done

while (<>) {
     next if $_ =~ /^\(set-option :produce-models.*\)/;
  if (/^\(set-info :category \"industrial\"\)/) {
    print $_;
    print "(set-info :status unsat)\n"
  } elsif (/^\(get-unsat-core\)/) {
    print $_;
    print "(exit)\n"
  } elsif (/^.*SMT script generated on.*/) {
     print '
SMT script generated by Ultimate Automizer [1,2].
Ultimate Automizer is an automatic software verification tool that implements
a new automata-theoretic approach[3].

This SMT script belongs to a set of SMT scripts that was generated by applying
Ultimate Automizer (revision r14204) to benchmarks from the SV-COMP 2015 [4,5] 
which are available at [6].

This script contains the SMT commands that were used by Ultimate Automizer
while performing one subtask, namely the computation of an inductive sequence 
of state assertions along a trace.
This subtask can be solved using Craig interpolation (if an interpolating
SMT solver is available for the given theory). The implementation that
produced this SMT script follows a different approach where unsatisfiable cores
provided by the SMT solver together with the post operator are used to compute 
the inductive sequence of state assertions.

2015-04-30, Matthias Heizmann (heizmann@informatik.uni-freiburg.de)


[1] https://ultimate.informatik.uni-freiburg.de/automizer/
[2] Matthias Heizmann, Daniel Dietsch, Jan Leike, Betim Musa, Andreas Podelski:
Ultimate Automizer with Array Interpolation - (Competition Contribution). 
TACAS 2015: 455-457
[3] Matthias Heizmann, Jochen Hoenicke, Andreas Podelski: Software Model 
Checking for People Who Love Automata. CAV 2013:36-52
[4] Dirk Beyer: Software Verification and Verifiable Witnesses - (Report on 
SV-COMP 2015). TACAS 2015: 401-416
[5] http://sv-comp.sosy-lab.org/2015/
[6] https://svn.sosy-lab.org/software/sv-benchmarks/tags/svcomp15/
';
  } else {
    print $_;
  }
}
