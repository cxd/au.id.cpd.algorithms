function [ output_args ] = displayResults( )
%UNTITLED1 Summary of this function goes here
%   Detailed explanation goes here
M = csvread('test_results.txt');
QI = reshape(M(1,:), sqrt(size(M, 2)), sqrt(size(M,2)));
RI = reshape(M(2,:), sqrt(size(M, 2)), sqrt(size(M,2)));
figure();
colormap(gray(256));
subplot(1,2,1);
image(QI);
title('Search Image');
subplot(1,2,2);
image(RI);
title('Match Image');

