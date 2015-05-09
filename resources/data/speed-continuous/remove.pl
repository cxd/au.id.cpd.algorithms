#!/usr/bin/perl

my $file = shift @ARGV;
my $out = shift @ARGV;
my $column = shift @ARGV;
open(FH, "<$file") || die "cannot open file $file\n";
open(FOUT, ">$out") || die "Cannot open file $out\n";
while(my $line = <FH>) {
	$line =~ s/\n//g;
	@attribs = split(",", $line);
	my @newatts;
	my $len = scalar @attribs;
	for(my $i=0;$i<scalar @attribs;$i++) {
		push @newatts, $attribs[$i] if ($i != $column);
	}
	print FOUT join(",", @newatts) . "\n";
}
close(FIN);
close(FOUT);
