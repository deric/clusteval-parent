# clusteval
[![Build Status](https://travis-ci.org/deric/clusteval-parent.svg?branch=master)](https://travis-ci.org/deric/clusteval-parent)

Fork of [original ClustEval](https://github.com/wiwie/clusteval) with complete list of dependencies, converted to Maven structure

## Backend requirements

  * `r-base`
  * MCODE algorithm requires `libawt_xawt.so` which is provided on Debian by `libxext6`
  * MCODE algorithm requires `libXrender.so.1` which is provided on Debian by `libxrender1`
  * MCODE algorithm requires `libXi.so.6` which is provided on Debian by `libxi6`

## Versions of R packages

Version used in original VirtualBox image:
```
acepack 1.3-3.3
bit     1.1-12
bitops  1.0-6
clv     0.3-2.1
colorspace      1.2-6
DEoptimR        1.0-3
dichromat       2.0-0
digest  0.6.8
diptest 0.75-7
doParallel      1.0.8
e1071   1.6-7
ff      2.2-13
fields  8.2-1
flexmix 2.3-13
foreach 1.4.2
Formula 1.2-1
fpc     2.1-10
ggplot2 1.0.1
gridBase        0.4-7
gridExtra       2.0.0
gtable  0.1.2
Hmisc   3.16-0
igraph  1.0.1
irlba   1.0.3
iterators       1.0.7
kernlab 0.9-22
kohonen 2.0.19
labeling        0.3
lars    1.2
latticeExtra    0.6-26
magrittr        1.5
maps    2.3-11
mclust  5.0.2
mlbench 2.1-1
modeltools      0.2-21
munsell 0.4.2
mvtnorm 1.0-3
NMF     0.20.6
pkgmaker        0.22
plyr    1.8.3
prabclus        2.2-6
proto   0.3-10
proxy   0.4-15
randomForest    4.6-10
RColorBrewer    1.1-2
Rcpp    0.12.0
RCurl   1.95-4.7
registry        0.3
reshape2        1.4.1
rlecuyer        0.3-3
rngtools        1.2.4
robustbase      0.92-5
Rserve  1.7-3
scales  0.2.5
spam    1.0-1
sprint  1.0.7
stringi 0.5-5
stringr 1.0.0
trimcluster     0.1-2
xtable  1.7-4
```
