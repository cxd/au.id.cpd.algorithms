#!/usr/bin/perl

my $file = "convert.txt";
open(FH, "<$file") || die "cannot open file\n";
open(FOUT, ">convert-processed.txt") || die "Cannot open file\n";
while(my $line = <FH>) {
	$line =~ s/\n//g;
	@attribs = split(",", $line);
	next if (scalar @attribs != 13);
	my $class = shift @attribs;
	push @attribs, $class;
	print FOUT join(",", @attribs) . "\n";
}
close(FIN);
close(FOUT);
