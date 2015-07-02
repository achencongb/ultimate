#!/bin/bash
mkdir StarExecArchive
mkdir StarExecArchive/bin
mkdir StarExecArchive/Ultimate
cp -a ../../trunk/source/BA_SiteRepository/target/products/CLI-E4/linux/gtk/x86_64/* StarExecArchive/Ultimate/
cp LICENSE* StarExecArchive/Ultimate/
cp starexec_description.txt StarExecArchive/
cp ../../trunk/examples/toolchains/AutomizerAndBuchiAutomizerCWithBlockEncoding.xml StarExecArchive/
cp starexec_run_default StarExecArchive/bin
cp Ultimate.ini StarExecArchive/Ultimate/
cp ../../trunk/examples/settings/buchiAutomizer/termcomp2015.epf StarExecArchive/
mkdir StarExecArchive/z3
cp -R /usr/bin/z3 StarExecArchive/z3/
cd StarExecArchive
zip ../UltimateCommandline.zip -r *

