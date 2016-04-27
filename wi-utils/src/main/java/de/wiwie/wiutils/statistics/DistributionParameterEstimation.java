/**
 *
 */
package de.wiwie.wiutils.statistics;

import de.clusteval.utils.ArraysExt;
import de.wiwie.wiutils.utils.FilterFactory;
import de.wiwie.wiutils.utils.FilterFactory.FILTER_TYPE;
import de.wiwie.wiutils.utils.Log;
import de.clusteval.api.Pair;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author Christian Wiwie
 *
 */
public class DistributionParameterEstimation {

    protected int[] x;
    protected List<DistributionParameters> parameters;

    public DistributionParameterEstimation(final int[] distribution) {
        super();
        this.x = distribution;
        this.parameters = new ArrayList<>();
    }

    public List<DistributionParameters> getDistributionParameters() {
        return this.parameters;
    }

    public void doParetoAnalysis() {
        PowerLawParameters params = this.estimateParetoParameters();
        Log.println(params);
        Log.println(this.calculateParetoSignificance(params));
    }

    public void doBinomialAnalysis() {
        BinomialParameters params = this.estimateBinomialParameters();
        Log.println(params);
        Log.println(this.calculateBinomialSignificance(params));
    }

    public void doStudentsTAnalysis() {
        StudentsTParameters params = this.estimateStudentsTParameters();
        Log.println(params);
        Log.println(this.calculateStudentsTSignificance(params));
    }

    public void doShiftedZetaAnalysis() {
        ShiftedPowerLawParameters params = this.estimateShiftedZetaParameters();
        Log.println(params);
        Log.println(this.calculateShiftedZetaSignificance(params));
    }

    public void doZetaAnalysis() {
        PowerLawParameters params = this.estimateZetaParameters();
        Log.println(params);
        Log.println(this.calculateZetaSignificance(params));
    }

    public void doZetaSampleSizeAnalysis() {
        int numberSteps = 10;
        int stepsize = (this.x.length * 2 - this.x.length / 2) / numberSteps;
        int[] sampleSizes = ArraysExt.range(this.x.length / 2,
                this.x.length * 2, stepsize);
        for (int i = 0; i < sampleSizes.length; i++) {
            PowerLawParameters params = this.estimateZetaParameters();
            Log.println(params);
            Log.println(this.calculateZetaSignificance(params, sampleSizes[i]));
        }
    }

    public void doPoissonAnalysis() {
        PoissonParameters params = this.estimatePoissonParameters();
        Log.println(params);
        Log.println(this.calculatePoissonSignificance(params));
    }

    public PowerLawParameters estimateParetoParameters() {
        // parameter estimation
        double[] vec = new double[201];
        for (int i = 150; i < 351; i++) {
            vec[i - 150] = i / 100.0;
        }

        List<Double> zvec = new ArrayList<>();
        for (double d : vec) {
            zvec.add(zetaFunction(d));
        }

        int[] xmins = ArraysExt.unique(this.x);
        Arrays.sort(xmins);
        xmins = Arrays.copyOf(xmins, xmins.length - 1);

        int xmax = ArraysExt.max(this.x);

        int[] z = Arrays.copyOf(this.x, this.x.length);
        Arrays.sort(z);

        List<Double> datA = new ArrayList<>(), datB = new ArrayList<>();
        for (int xmin : xmins) {
            if (xmin == 0) {
                continue;
            }
            int startPos = 0;
            while (z[startPos] < xmin) {
                startPos++;
            }
            z = Arrays.copyOfRange(z, startPos, z.length);
            // System.out.print(xmin + "\t");
            // ArraysExt.print(z);
            int n = z.length;
            // estimate alpha via direct maximization of likelihood function

            // force iterative calculation
            List<Double> Llist = new ArrayList<Double>();
            double slogz = ArraysExt.sum(ArraysExt.log(z));
            double[] xminvec = new double[xmin - 1];
            for (int i = 1; i <= xminvec.length; i++) {
                xminvec[i - 1] = i;
            }
            for (int k = 0; k < vec.length; k++) {
                Llist.add(-vec[k] * slogz + n
                        * (Math.log(vec[k]) + vec[k] * Math.log(xmin)));
            }
            double[] L = ArraysExt.toPrimitive(Llist.toArray(new Double[0]));
            int I = ArraysExt.maxPos(L);
            // compute KS statistic
            double[] tmp = new double[xmax - xmin + 1];
            for (int pos = xmin; pos < xmax + 1; pos++) {
                tmp[pos - xmin] = vec[I] * Math.pow(xmin, vec[I])
                        / Math.pow(pos, vec[I] + 1);
            }
            tmp = ArraysExt.scaleBy(tmp, ArraysExt.sum(tmp));

            double[] fit = cdfPareto(xmin, new double[xmin], vec[I], 0, xmax,
                    1.0);
            fit = Arrays.copyOfRange(fit, xmin, fit.length);
            fit = ArraysExt.scaleBy(fit, fit[fit.length - 1]);

            double[] cdi = new double[xmax - xmin + 1];
            for (int XM = xmin; XM < xmax + 1; XM++) {
                List<Double> zSmaller = new ArrayList<Double>();
                for (double d : z) {
                    if (d <= XM) {
                        zSmaller.add(d);
                    }
                }
                cdi[XM - xmin] = zSmaller.size() / (double) n;
            }

            double[] bla = new double[xmax - xmin + 1];
            for (int x = 0; x < xmax - xmin + 1; x++) {
                bla[x] = Math.abs(fit[x] - cdi[x]);
            }
            datA.add(ArraysExt.max(bla));
            datB.add(vec[I]);
        }
        // select the index for the minimum value of D
        int I = ArraysExt.minPos(ArraysExt.toPrimitive(datA
                .toArray(new Double[0])));
        double D = datA.get(I);
        int xmin = xmins[I];
        double alpha = datB.get(I);

        double[] x = toDistribution(this.x);
        return new PowerLawParameters(alpha, xmin, Arrays.copyOf(x, xmin), D);
    }

    public PowerLawParameters estimateZetaParameters() {
        // parameter estimation
        double[] alphas = new double[201];
        for (int i = 150; i < 351; i++) {
            alphas[i - 150] = i / 100.0;
        }

        List<Double> zetas = new ArrayList<Double>();
        for (double d : alphas) {
            zetas.add(zetaFunction(d));
        }

        int[] xmins = ArraysExt.unique(this.x);
        Arrays.sort(xmins);
        if (xmins[0] == 0) {
            xmins = Arrays.copyOfRange(xmins, 1, xmins.length);
        }
        xmins = Arrays.copyOf(xmins, xmins.length - 1);

        int xmax = ArraysExt.max(this.x);

        int[] z = Arrays.copyOf(this.x, this.x.length);
        Arrays.sort(z);

        double[][] cdfsZetas = new double[alphas.length][];

        List<Double> datA = new ArrayList<Double>(), datB = new ArrayList<Double>();
        for (int xmin : xmins) {
            int startPos = 0;
            while (z[startPos] < xmin) {
                startPos++;
            }
            z = Arrays.copyOfRange(z, startPos, z.length);
            // System.out.print(xmin + "\t");
            // ArraysExt.print(z);
            int n = z.length;
            // estimate alpha via direct maximization of likelihood function

            // force iterative calculation
            List<Double> Llist = new ArrayList<Double>();
            double slogz = ArraysExt.sum(ArraysExt.log(z));
            double[] xminvec = new double[xmin - 1];
            for (int i = 1; i <= xminvec.length; i++) {
                xminvec[i - 1] = i;
            }
            for (int k = 0; k < alphas.length; k++) {
                Llist.add(-alphas[k]
                        * slogz
                        - n
                        * Math.log(zetas.get(k)
                                - ArraysExt.sum(ArraysExt.pow(xminvec,
                                        -alphas[k]))));
            }
            double[] L = ArraysExt.toPrimitive(Llist.toArray(new Double[0]));
            int I = ArraysExt.maxPos(L);
            // compute KS statistic

            // compute theoretical CDF
            double[] theoreticalCDF = null;
            if (cdfsZetas[I] == null) {
                double[] tmp = new double[xmax - xmin + 1];
                for (int pos = xmin; pos < xmax + 1; pos++) {
                    tmp[pos - xmin] = Math.pow(pos, -alphas[I])
                            / (zetas.get(I) - ArraysExt.sum(ArraysExt.pow(
                            xminvec, -alphas[I])));
                }

                List<Double> fit = new ArrayList<Double>();
                fit.add(0.0);
                for (double d : tmp) {
                    fit.add(fit.get(fit.size() - 1) + d);
                }
                fit.remove(0);
                cdfsZetas[I] = ArraysExt
                        .toPrimitive(fit.toArray(new Double[0]));
            }
            theoreticalCDF = cdfsZetas[I];

            double[] cdi = new double[xmax - xmin + 1];
            for (int XM = xmin; XM < xmax + 1; XM++) {
                List<Double> zSmaller = new ArrayList<Double>();
                for (double d : z) {
                    if (d <= XM) {
                        zSmaller.add(d);
                    }
                }
                cdi[XM - xmin] = zSmaller.size() / (double) n;
            }

            double[] bla = new double[xmax - xmin + 1];
            for (int x = 0; x < xmax - xmin + 1; x++) {
                bla[x] = Math.abs(theoreticalCDF[x] - cdi[x]);
            }
            // System.out.printf("%d, %s, %s \n", xmin, ArraysExt.maxPos(bla),
            // ArraysExt.max(bla));
            datA.add(ArraysExt.max(bla));
            datB.add(alphas[I]);
        }
        // select the index for the minimum value of D
        int I = ArraysExt.minPos(ArraysExt.toPrimitive(datA
                .toArray(new Double[0])));
        double D = datA.get(I);
        int xmin = xmins[I];
        double alpha = datB.get(I);

        double[] x = toDistribution(this.x);
        return new PowerLawParameters(alpha, xmin, Arrays.copyOf(x, xmin), D);
    }

    public ShiftedPowerLawParameters estimateShiftedZetaParameters() {
        // parameter estimation
        double[] alphas = new double[201];
        for (int i = 150; i < 351; i++) {
            alphas[i - 150] = i / 100.0;
        }

        List<Double> zvec = new ArrayList<Double>();
        for (double d : alphas) {
            zvec.add(zetaFunction(d));
        }

        int[] xmins = ArraysExt.unique(this.x);
        Arrays.sort(xmins);
        // xmins = Arrays.copyOf(xmins, xmins.length - 1);
        xmins = Arrays.copyOf(xmins, Math.min(50, xmins.length - 1));

        int[] xmins2 = ArraysExt.range(0, ArraysExt.max(this.x));
        xmins2 = Arrays.copyOf(xmins2, Math.min(50, xmins2.length - 1));

        int xmax = ArraysExt.max(this.x);

        int[] z = Arrays.copyOf(this.x, this.x.length);
        Arrays.sort(z);

        double[] counts = new double[xmax + 1];
        for (int XM = 0; XM < xmax + 1; XM++) {
            List<Double> zSmaller = new ArrayList<Double>();
            for (double d : z) {
                if (d <= XM) {
                    zSmaller.add(d);
                }
            }
            counts[XM] = zSmaller.size();
        }

        // List<Double> datA = new ArrayList<Double>(), datB = new
        // ArrayList<Double>();
        double[][] datA = new double[xmins.length][xmins2.length];
        double[][] datB = new double[xmins.length][xmins2.length];
        // double[][] sums = new double[xmax][alphas.length];
        // for (int i = 0; i < sums.length; i++)
        // for (int j = 0; j < sums[i].length; j++)
        // sums[i][j] = -1;
        // double[][][] distrs = new
        // double[ArraysExt.max(xmins)][alphas.length][xmax + 1];
        int pos1 = -1;
        int count = -1;
        int totalCount = -1;
        for (int xmin1 : xmins) {
            pos1++;
            if (xmin1 == 0) {
                continue;
            }
            int startPos = 0;
            while (z[startPos] < xmin1) {
                startPos++;
            }
            z = Arrays.copyOfRange(z, startPos, z.length);
            // System.out.print(xmin + "\t");
            // ArraysExt.print(z);
            int n = z.length;

            int pos2 = -1;
            for (int xmin2 : xmins2) {
                pos2++;
                if (xmin2 >= xmin1) {
                    datA[pos1][pos2] = Double.MAX_VALUE;
                    continue;
                }
                // estimate alpha via direct maximization of likelihood function

                // force iterative calculation
                List<Double> Llist = new ArrayList<Double>();
                double slogz = ArraysExt.sum(ArraysExt.log(ArraysExt.subtract(
                        z, xmin2)));
                double[] xminvec = new double[xmin1 - 1 - xmin2];
                for (int i = 1; i <= xminvec.length; i++) {
                    xminvec[i - 1] = i;
                }
                for (int k = 0; k < alphas.length; k++) {
                    Llist.add(-alphas[k]
                            * slogz
                            - n
                            * Math.log(zvec.get(k)
                                    - ArraysExt.sum(ArraysExt.pow(xminvec,
                                            -alphas[k]))));
                }
                double[] L = ArraysExt
                        .toPrimitive(Llist.toArray(new Double[0]));
                int I = ArraysExt.maxPos(L);
                // compute KS statistic
                // if (sums[xmin1 - xmin2 - 1][I] == -1) {
                // double[] tmp2 = new double[xmin1 - 1 - xmin2];
                // for (int pos = 1; pos <= xmin1 - 1 - xmin2; pos++) {
                // tmp2[pos - 1] = pos;
                // }
                // sums[xmin1 - xmin2 - 1][I] = ArraysExt.sum(ArraysExt.pow(
                // tmp2, -alphas[I]));
                // }
                // double sum = sums[xmin1 - xmin2 - 1][I];

                double[] probDistr;
                totalCount++;
                // for (int pos = xmin1; pos < xmax + 1; pos++) {
                // probDistr[pos - xmin1] = Math.pow(pos - xmin2, -alphas[I]);
                // }
                // double[] probDistrTmp = new double[probDistr.length];
                // if (distrs[xmin1 - xmin2 - 1][I] == null) {
                count++;
                double[] tmp2 = new double[xmin1 - 1 - xmin2];
                for (int pos = 1; pos <= xmin1 - 1 - xmin2; pos++) {
                    tmp2[pos - 1] = pos;
                }
                double sum = ArraysExt.sum(ArraysExt.pow(tmp2, -alphas[I]));

                probDistr = new double[xmax + 1];
                for (int pos = 0; pos < xmax + 1; pos++) {
                    probDistr[pos] = Math.pow(pos + xmin1 - xmin2, -alphas[I]);
                }
                probDistr = ArraysExt.scaleBy(probDistr, (zvec.get(I) - sum));
                // distrs[xmin1 - xmin2 - 1][I] = probDistr;
                // }
                // probDistr = distrs[xmin1 - xmin2 - 1][I];
                List<Double> fit = new ArrayList<Double>();
                fit.add(0.0);
                for (double d : probDistr) {
                    fit.add(fit.get(fit.size() - 1) + d);
                }
                fit.remove(0);

                // double[] cdi = new double[xmax - xmin1 + 1];
                // for (int XM = xmin1; XM < xmax + 1; XM++) {
                // List<Double> zSmaller = new ArrayList<Double>();
                // for (double d : z) {
                // if (d <= XM)
                // zSmaller.add(d);
                // }
                // cdi[XM - xmin1] = zSmaller.size() / (double) n;
                // }
                double[] countsTmp = Arrays.copyOfRange(counts, xmin1,
                        counts.length);
                countsTmp = ArraysExt.subtract(countsTmp, counts[xmin1 - 1]);
                double[] cdi = ArraysExt.scaleBy(countsTmp,
                        countsTmp[countsTmp.length - 1]);
                // ArraysExt.print(cdi);
                // ArraysExt.print(testCdi);

                double[] bla = new double[xmax - xmin1 + 1];
                for (int x = 0; x < xmax - xmin1 + 1; x++) {
                    bla[x] = Math.abs(fit.get(x) - cdi[x]);
                }
                // System.out.printf("%d, %s, %s \n", xmin,
                // ArraysExt.maxPos(bla),
                // ArraysExt.max(bla));
                // datA.add(ArraysExt.max(bla));
                datA[pos1][pos2] = ArraysExt.max(bla);
                // datB.add(vec[I]);
                datB[pos1][pos2] = alphas[I];
            }
            pos2 = 0;
            // distrs[xmax - xmin1 - 1] = null;
        }
        // select the index for the minimum value of D
        // int I = ArraysExt.minPos(ArraysExt.toPrimitive(datA
        // .toArray(new Double[0])));
        int optXmin1 = 0, optXmin2 = 0;
        double min = Double.MAX_VALUE;
        for (int i = 0; i < datA.length; i++) {
            for (int j = 0; j < datA[i].length; j++) {
                if (datA[i][j] < min) {
                    optXmin1 = i;
                    optXmin2 = j;
                    min = datA[i][j];
                }
            }
        }
        // double D = datA.get(I);
        double D = datA[optXmin1][optXmin2];
        // int xmin = xmins[I];
        int xmin1 = xmins[optXmin1];
        int xmin2 = xmins2[optXmin2];
        // double alpha = datB.get(I);
        double alpha = datB[optXmin1][optXmin2];

        double[] x = toDistribution(this.x);
        return new ShiftedPowerLawParameters(alpha, xmin1, xmin2,
                Arrays.copyOf(x, xmin1), D);
    }

    public ShiftedPowerLawParameters estimateShiftedScaledZetaParameters() {
        // parameter estimation
        double[] alphas = new double[201];
        for (int i = 150; i < 351; i++) {
            alphas[i - 150] = i / 100.0;
        }

        List<Double> zvec = new ArrayList<Double>();
        for (double d : alphas) {
            zvec.add(zetaFunction(d));
        }

        int[] xmins = ArraysExt.unique(this.x);
        Arrays.sort(xmins);
        xmins = Arrays.copyOf(xmins, xmins.length - 1);

        int[] shiftings = ArraysExt.range(0, ArraysExt.max(this.x));

        int xmax = ArraysExt.max(this.x);

        int[] z = Arrays.copyOf(this.x, this.x.length);
        Arrays.sort(z);

        // double[] scalings = ArraysExt.range(0.1, 2.1, 0.2);
        double[] scalings = new double[]{1.0, 1.1};
        ArraysExt.print(scalings);

        double[][][] datA = new double[xmins.length][shiftings.length][scalings.length];
        double[][][] datB = new double[xmins.length][shiftings.length][scalings.length];
        int pos1 = -1;
        for (int xmin1 : xmins) {
            pos1++;
            if (xmin1 == 0) {
                continue;
            }
            int startPos = 0;
            while (z[startPos] < xmin1) {
                startPos++;
            }
            z = Arrays.copyOfRange(z, startPos, z.length);
            // System.out.print(xmin + "\t");
            // ArraysExt.print(z);
            int n = z.length;

            int pos2 = -1;
            for (int xmin2 : shiftings) {
                pos2++;
                if (xmin2 >= xmin1) {
                    for (int i = 0; i < scalings.length; i++) {
                        datA[pos1][pos2][i] = Double.MAX_VALUE;
                    }
                    continue;
                }
                int pos3 = -1;
                for (double scaling : scalings) {
                    pos3++;
                    // estimate alpha via direct maximization of likelihood
                    // function

                    // force iterative calculation
                    List<Double> Llist = new ArrayList<Double>();
                    double slogz = ArraysExt.sum(ArraysExt.log(ArraysExt
                            .subtract(z, xmin2)));
                    double[] xminvec = new double[xmin1 - 1 - xmin2];
                    for (int i = 1; i <= xminvec.length; i++) {
                        xminvec[i - 1] = i;
                    }
                    for (int k = 0; k < alphas.length; k++) {
                        Llist.add(-alphas[k]
                                * slogz
                                - n
                                * Math.log(zvec.get(k)
                                        - ArraysExt.sum(ArraysExt.pow(ArraysExt
                                                .scaleBy(xminvec, scaling,
                                                        false), -alphas[k])))
                                - alphas[k] * n * Math.log(scaling));
                    }
                    double[] L = ArraysExt.toPrimitive(Llist
                            .toArray(new Double[0]));
                    int I = ArraysExt.maxPos(L);
                    // compute KS statistic
                    double[] tmp2 = new double[xmin1 - 1 - xmin2];
                    for (int pos = 1; pos <= xmin1 - 1 - xmin2; pos++) {
                        tmp2[pos - 1] = scaling * pos;
                    }
                    double sum = ArraysExt.sum(ArraysExt.pow(tmp2, -alphas[I]));
                    double[] probDistr = new double[xmax - xmin1 + 1];
                    for (int x = xmin1; x < xmax + 1; x++) {
                        probDistr[x - xmin1] = Math.pow(scaling * (x - xmin2),
                                -alphas[I]) / (zvec.get(I) - sum);
                    }

                    List<Double> fit = new ArrayList<Double>();
                    fit.add(0.0);
                    for (double d : probDistr) {
                        fit.add(fit.get(fit.size() - 1) + d);
                    }
                    fit.remove(0);

                    double[] cdi = new double[xmax - xmin1 + 1];
                    for (int XM = xmin1; XM < xmax + 1; XM++) {
                        List<Double> zSmaller = new ArrayList<Double>();
                        for (double d : z) {
                            if (d <= XM) {
                                zSmaller.add(d);
                            }
                        }
                        cdi[XM - xmin1] = zSmaller.size() / (double) n;
                    }

                    double[] bla = new double[xmax - xmin1 + 1];
                    for (int x = 0; x < xmax - xmin1 + 1; x++) {
                        bla[x] = Math.abs(fit.get(x) - cdi[x]);
                    }
                    // System.out.printf("%d, %s, %s \n", xmin,
                    // ArraysExt.maxPos(bla),
                    // ArraysExt.max(bla));
                    // datA.add(ArraysExt.max(bla));
                    datA[pos1][pos2][pos3] = ArraysExt.max(bla);
                    // datB.add(vec[I]);
                    datB[pos1][pos2][pos3] = alphas[I];
                }
            }
            pos2 = 0;
        }
        // select the index for the minimum value of D
        // int I = ArraysExt.minPos(ArraysExt.toPrimitive(datA
        // .toArray(new Double[0])));
        int optXmin1 = 0, optXmin2 = 0, optScaling = 0;
        double min = Double.MAX_VALUE;
        for (int i = 0; i < datA.length; i++) {
            for (int j = 0; j < datA[i].length; j++) {
                for (int k = 0; k < datA[i][j].length; k++) {
                    if (datA[i][j][k] < min) {
                        optXmin1 = i;
                        optXmin2 = j;
                        optScaling = k;
                        min = datA[i][j][k];
                    }
                }
            }
        }
        // double D = datA.get(I);
        double D = datA[optXmin1][optXmin2][optScaling];
        // int xmin = xmins[I];
        int xmin1 = xmins[optXmin1];
        int xmin2 = shiftings[optXmin2];
        double scaling = scalings[optScaling];
        // double alpha = datB.get(I);
        double alpha = datB[optXmin1][optXmin2][optScaling];

        double[] x = toDistribution(this.x);
        return new ShiftedScaledPowerLawParameters(alpha, xmin1, xmin2,
                scaling, Arrays.copyOf(x, xmin1), D);
    }

    public StudentsTParameters estimateStudentsTParameters() {
        int optDf = 1;
        int optShifting = 0;
        int optXmin = 1;
        double optScaling = 0.0;
        double ksDistance = Double.MAX_VALUE;

        this.x = ArraysExt.filter(this.x,
                FilterFactory.createInstance(FILTER_TYPE.LARGER_THAN, 0));

        if (this.x.length == 0) {
            throw new IllegalArgumentException("Empty input");
        }

        int[] dfs = ArraysExt.range(1, 5);
        int[] shiftings = ArraysExt.range(-5, 5);
        int[] xmins = ArraysExt.unique(this.x);
        Arrays.sort(xmins);
        xmins = Arrays.copyOf(xmins, Math.min(xmins.length / 2, 50));

        int maxX = ArraysExt.max(this.x);

        double[] scalings = ArraysExt.range(0.3, 3.0, 0.1);

        double[][][][] cdfsStudents = new double[dfs.length][shiftings.length][scalings.length][];

        for (int xmin : xmins) {
            // System.out.println("###" + xmin);
            int[] z = ArraysExt
                    .filter(this.x, FilterFactory.createInstance(
                            FILTER_TYPE.LARGER_THAN, xmin));
            double[] zDistr = toDistribution(z);
            double[] empiricalCdf = cdf(zDistr);
            empiricalCdf = ArraysExt.subtract(empiricalCdf[0], empiricalCdf);
            empiricalCdf = ArraysExt.scaleBy(empiricalCdf,
                    empiricalCdf[empiricalCdf.length - 1]);

            int posDf = -1;
            for (int df : dfs) {
                posDf++;

                int posShifting = -1;
                for (int shifting : shiftings) {
                    posShifting++;

                    int posScaling = -1;
                    for (double stepsize : scalings) {
                        posScaling++;

                        double[] studentsTCdf;
                        if (cdfsStudents[posDf][posShifting][posScaling] == null) {
                            cdfsStudents[posDf][posShifting][posScaling] = cdfStudentsT(
                                    df, maxX, stepsize, shifting);
                        }
                        studentsTCdf = cdfsStudents[posDf][posShifting][posScaling];

                        double diff = studentsTCdf[studentsTCdf.length / 2
                                + xmin - 1];
                        studentsTCdf = Arrays.copyOfRange(studentsTCdf,
                                studentsTCdf.length / 2 + xmin,
                                studentsTCdf.length);
                        studentsTCdf = ArraysExt.subtract(studentsTCdf, diff);
                        studentsTCdf = ArraysExt.scaleBy(studentsTCdf,
                                studentsTCdf[studentsTCdf.length - 1]);

                        double dist = cdfDistance(xmin, empiricalCdf,
                                studentsTCdf, DISTANCE_TYPE.KS_DISTANCE);
                        if (dist < ksDistance) {
                            optXmin = xmin;
                            optDf = df;
                            optShifting = shifting;
                            optScaling = stepsize;
                            ksDistance = dist;
                        }
                        // System.out.println(dist);
                    }
                }
            }
        }

        return new StudentsTParameters(optDf, optXmin, optShifting, optScaling,
                ksDistance);
    }

    public double calculateZetaSignificance(PowerLawParameters params) {
        return calculateZetaSignificance(params, -1);
    }

    public double calculateZetaSignificance(PowerLawParameters params,
            int sampleSize) {
        int it = 1000, sigCount = 0;
        double p = 0.0;

        double[] alphas = new double[it];
        int[] xmins = new int[it];

        DISTANCE_TYPE distType = DISTANCE_TYPE.KS_DISTANCE;

        double[] x = toDistribution(this.x);
        // ArraysExt.print(cdf(x));
        double[] theoreticalDistr = generateZetaDistribution(params.getXMin(),
                params.getAlpha(), ArraysExt.max(this.x));
        // ArraysExt.print(cdf(theoreticalDistr));
        double empiricalDistance = distributionDistance(params.getXMin(), x,
                theoreticalDistr, distType);
        this.parameters.clear();
        Log.println("Empirical distance=" + empiricalDistance);

        sampleSize = (sampleSize <= 0 ? this.x.length : sampleSize);
        Log.println("Validation by semiparametric bootstrapping (sample size="
                + sampleSize + ")");

        for (int i = 0; i < it; i++) {
            int[] sampleZeta = sampleZetaDistribution(params.getXMin(),
                    params.getXMinDistribution(), params.getAlpha(),
                    ArraysExt.max(this.x), sampleSize);
            DistributionParameterEstimation estimator = new DistributionParameterEstimation(
                    sampleZeta);
            PowerLawParameters newParams = estimator.estimateZetaParameters();
            System.out.println(newParams);
            double[] sampleDistr = toDistribution(sampleZeta);
            // ArraysExt.print(sampleDistr);
            double[] newTheoreticalDistribution = generateZetaDistribution(
                    newParams.getXMin(), new double[newParams.getXMin()],
                    newParams.getAlpha(), 20 * ArraysExt.max(sampleZeta));
            // ArraysExt.print(newTheoreticalDistribution);
            double syntheticDistance = distributionDistance(
                    newParams.getXMin(), sampleDistr,
                    newTheoreticalDistribution, distType);
            // Log.println(params2);
            Log.println(syntheticDistance);
            if (syntheticDistance >= empiricalDistance) {
                sigCount++;
            }
            alphas[i] = newParams.getAlpha();
            xmins[i] = newParams.getXMin();
            this.parameters.add(newParams);
            p = sigCount / (double) (i + 1);
        }

        // ArraysExt.print(toDistribution(alphas));
        ArraysExt.print(toDistribution(xmins));

        return p;
    }

    public double calculateParetoSignificance(PowerLawParameters params) {
        int it = 1000, sigCount = 0;
        double p = 0.0;

        DISTANCE_TYPE distType = DISTANCE_TYPE.KS_DISTANCE;

        double[] x = toDistribution(this.x);
        x = Arrays.copyOfRange(x, params.getXMin(), x.length);
        x = ArraysExt.scaleBy(x, ArraysExt.sum(x));
        double[] empiricalCdf = cdf(x);

        double[] theoreticalCdf = cdfPareto(params.getXMin(),
                new double[params.getXMin()], params.getAlpha(), 0,
                this.x.length, 1.0);
        theoreticalCdf = Arrays.copyOfRange(theoreticalCdf, params.getXMin(),
                theoreticalCdf.length);

        double empiricalDistance = cdfDistance(params.getXMin(), empiricalCdf,
                theoreticalCdf, distType);

        this.parameters.clear();
        Log.println("Empirical distance=" + empiricalDistance);

        for (int i = 0; i < it; i++) {
            double[] sampleZeta = sampleParetoDistribution(params.getXMin(),
                    params.getXMinDistribution(), params.getAlpha(), 0,
                    ArraysExt.max(this.x), this.x.length, 1.0);

            int[] intSampleZeta = new int[sampleZeta.length];
            for (int j = 0; j < sampleZeta.length; j++) {
                intSampleZeta[j] = (int) sampleZeta[j];
            }

            DistributionParameterEstimation estimator = new DistributionParameterEstimation(
                    intSampleZeta);
            PowerLawParameters newParams = estimator.estimateParetoParameters();
            // System.out.println(newParams);

            double[] sampleDistr = toDistribution(intSampleZeta);
            sampleDistr = Arrays.copyOfRange(sampleDistr, newParams.getXMin(),
                    sampleDistr.length);
            sampleDistr = ArraysExt.scaleBy(sampleDistr,
                    ArraysExt.sum(sampleDistr));
            double[] sampleCdf = cdf(sampleDistr);

            // ArraysExt.print(sampleCdf);
            double[] newTheoreticalCdf = cdfPareto(newParams.getXMin(),
                    new double[newParams.getXMin()], newParams.getAlpha(), 0,
                    intSampleZeta.length, 1.0);
            newTheoreticalCdf = Arrays.copyOfRange(newTheoreticalCdf,
                    newParams.getXMin(), newTheoreticalCdf.length);
            // ArraysExt.print(newTheoreticalCdf);

            double syntheticDistance = cdfDistance(newParams.getXMin(),
                    sampleCdf, newTheoreticalCdf, distType);
            // Log.println(syntheticDistance);
            if (syntheticDistance >= empiricalDistance) {
                sigCount++;
            }
            this.parameters.add(newParams);
            p = sigCount / (double) (i + 1);
        }

        return p;
    }

    public double calculateShiftedZetaSignificance(
            ShiftedPowerLawParameters params) {
        int it = 1000, sigCount = 0;
        double p = 0.0;

        DISTANCE_TYPE distType = DISTANCE_TYPE.KS_DISTANCE;

        double[] x = toDistribution(this.x);
        double[] theoreticalDistr = generateZetaDistribution(params.getXMin(),
                params.getAlpha(), params.getShifting(), this.x.length);
        double empiricalDistance = distributionDistance(params.getXMin(), x,
                theoreticalDistr, distType);
        this.parameters.clear();
        Log.println("Empirical distance=" + empiricalDistance);

        for (int i = 0; i < it; i++) {
            int[] samplePowerLaw = sampleZetaDistribution(params.getXMin(),
                    params.getXMinDistribution(), params.getAlpha(),
                    params.getShifting(), ArraysExt.max(this.x), this.x.length);
            DistributionParameterEstimation estimator = new DistributionParameterEstimation(
                    samplePowerLaw);
            ShiftedPowerLawParameters newParams = estimator
                    .estimateShiftedZetaParameters();
            System.out.println(newParams);
            double[] sampleDistr = toDistribution(samplePowerLaw);
            ArraysExt.print(sampleDistr);
            double[] newTheoreticalDistribution = generateZetaDistribution(
                    newParams.getXMin(), new double[newParams.getXMin()],
                    newParams.getAlpha(), newParams.getShifting(),
                    samplePowerLaw.length);
            ArraysExt.print(newTheoreticalDistribution);
            double syntheticDistance = distributionDistance(
                    newParams.getXMin(), sampleDistr,
                    newTheoreticalDistribution, distType);
            // Log.println(newParams);
            Log.println(syntheticDistance);
            if (syntheticDistance >= empiricalDistance) {
                sigCount++;
            }
            this.parameters.add(newParams);
            p = sigCount / (double) (i + 1);
        }
        return p;
    }

    public double calculateStudentsTSignificance(StudentsTParameters params) {
        int it = 1000, sigCount = 0;
        double p = 0.0;

        DISTANCE_TYPE distType = DISTANCE_TYPE.KS_DISTANCE;

        this.x = ArraysExt.filter(
                this.x,
                FilterFactory.createInstance(FILTER_TYPE.LARGER_THAN,
                        params.getXMin()));
        double[] x = toDistribution(this.x);
        double[] empiricalCdf = cdf(x);
        empiricalCdf = ArraysExt.scaleBy(empiricalCdf,
                empiricalCdf[empiricalCdf.length - 1]);
        ArraysExt.print(empiricalCdf);
        double[] theoreticalCdf = cdfStudentsT(params.getDf(),
                ArraysExt.max(this.x), params.getScaling(),
                params.getShifting());
        double diff = theoreticalCdf[theoreticalCdf.length / 2
                + params.getXMin() - 1];
        theoreticalCdf = Arrays.copyOfRange(theoreticalCdf,
                theoreticalCdf.length / 2 + params.getXMin(),
                theoreticalCdf.length);
        theoreticalCdf = ArraysExt.subtract(theoreticalCdf, diff);
        theoreticalCdf = ArraysExt.scaleBy(theoreticalCdf,
                theoreticalCdf[theoreticalCdf.length - 1]);
        ArraysExt.print(theoreticalCdf);

        double empiricalDistance = cdfDistance(params.getXMin(), empiricalCdf,
                theoreticalCdf, distType);

        this.parameters.clear();
        Log.println("Empirical distance=" + empiricalDistance);

        for (int i = 0; i < it; i++) {
            double[] sampleStudentsT = sampleStudentsTDistribution(
                    params.getDf(), ArraysExt.max(this.x), params.getScaling(),
                    params.getShifting(), this.x.length);

            int[] intSampleStudentsT = new int[sampleStudentsT.length];
            for (int j = 0; j < sampleStudentsT.length; j++) {
                intSampleStudentsT[j] = (int) sampleStudentsT[j];
            }
            intSampleStudentsT = ArraysExt.filter(intSampleStudentsT,
                    FilterFactory.createInstance(FILTER_TYPE.LARGER_THAN,
                            params.getXMin()));

            DistributionParameterEstimation estimator = new DistributionParameterEstimation(
                    intSampleStudentsT);
            try {
                StudentsTParameters newParams = estimator
                        .estimateStudentsTParameters();

                if (newParams.getScaling() == 0.0) {
                    throw new IllegalArgumentException();
                }
                System.out.println(newParams);

                double[] sampleDistr = toDistribution(intSampleStudentsT);
                double[] sampleCdf = cdf(sampleDistr);
                sampleCdf = ArraysExt.subtract(sampleCdf[0], sampleCdf);
                sampleCdf = ArraysExt.scaleBy(sampleCdf,
                        sampleCdf[sampleCdf.length - 1]);

                double[] newTheoreticalCdf = cdfStudentsT(newParams.getDf(),
                        ArraysExt.max(this.x), newParams.getScaling(),
                        newParams.getShifting());
                newTheoreticalCdf = Arrays.copyOfRange(newTheoreticalCdf,
                        newTheoreticalCdf.length / 2 + newParams.getXMin(),
                        newTheoreticalCdf.length);
                newTheoreticalCdf = ArraysExt.subtract(newTheoreticalCdf[0],
                        newTheoreticalCdf);
                newTheoreticalCdf = ArraysExt.scaleBy(newTheoreticalCdf,
                        newTheoreticalCdf[newTheoreticalCdf.length - 1]);
                ArraysExt.print(sampleCdf);
                ArraysExt.print(newTheoreticalCdf);

                double syntheticDistance = cdfDistance(newParams.getXMin(),
                        sampleCdf, newTheoreticalCdf, distType);
                // Log.println(syntheticDistance);
                if (syntheticDistance >= empiricalDistance) {
                    sigCount++;
                }
                this.parameters.add(newParams);
                p = sigCount / (double) (i + 1);
            } catch (Exception e) {
                i--;
                continue;
            }
        }

        return p;
    }

    public static double hurwitzZetaFunction(double alpha, double xmin) {
        return hurwitzZetaFunction(alpha, xmin, 1.0);
    }

    public static double hurwitzZetaFunction(double alpha, double xmin,
            double scaling) {
        double result = 0;

        for (int i = 0; i < 10000 - xmin; i++) {
            result += Math.pow(scaling * (i + xmin), -alpha);
        }

        return result;
    }

    public static double zetaFunction(double s) {
        double result = 0;

        for (int i = 1; i < 10000; i++) {
            result += (Math.pow(i, -s));
        }

        return result;
    }

    public static double[] generatePoissonDistribution(int xmin,
            double[] xMinDistribution, double lambda, int maxX) {
        double[] result = new double[maxX];

        double xMinProb = 0.0;
        for (int i = 0; i < xmin; i++) {
            xMinProb += result[i] = xMinDistribution[i];
        }

        for (int i = 0; i < result.length - xmin; i++) {
            if (i == 0) {
                result[xmin + i] = Math.exp(-lambda);
            } else {
                result[xmin + i] = lambda / i * result[xmin + i - 1];
            }
        }

        for (int i = xmin; i < result.length; i++) {
            result[i] *= (1.0 - xMinProb);
        }
        return result;
    }

    public static double[] generateZetaDistribution(int xmin, double alpha,
            int maxX) {
        double[] xMinDistr = new double[xmin];
        return generateZetaDistribution(xmin, xMinDistr, alpha, maxX);
    }

    public static double[] generateZetaDistribution(int xmin, double alpha,
            int shifting, int maxX) {
        double[] xMinDistr = new double[xmin];
        return generateZetaDistribution(xmin, xMinDistr, alpha, shifting, maxX);
    }

    public static double[] generateZetaDistribution(int xmin,
            double[] xMinDistribution, double alpha, int maxX) {
        return generateZetaDistribution(xmin, xMinDistribution, alpha, 0, maxX);
    }

    public static double[] generateZetaDistribution(int xmin,
            double[] xMinDistribution, double alpha, int shifting, int maxX) {
        return generateZetaDistribution(xmin, xMinDistribution, alpha,
                shifting, 1.0, maxX);
    }

    public static double[] generateZetaDistribution(int xmin,
            double[] xMinDistribution, double alpha, int shifting,
            double scaling, int maxX) {
        if (xmin < 1) {
            throw new IllegalArgumentException(
                    "Power-Law diverges at f(0). Please set xmin > 0");
        }
        if (shifting >= xmin) {
            throw new IllegalArgumentException("Shifting has to be < xmin");
        }
        double[] result = new double[maxX + 1];

        // double xMinProb = ArraysExt.sum(xMinDistribution);
        // for (int i = 1; i < xmin; i++)
        // result[i] = xMinProb / (xmin - 1);
        double xMinProb = ArraysExt.sum(xMinDistribution);
        for (int i = 1; i < xmin; i++) {
            result[i] = xMinDistribution[i];
        }

        double c = 1 / hurwitzZetaFunction(alpha, xmin - shifting, 1.0);

        for (int i = xmin; i < maxX; i++) {
            result[i] = c * Math.pow(scaling * (i - shifting), -alpha);
        }

        if (xMinProb > 0.0) {
            for (int i = xmin; i < maxX; i++) {
                result[i] *= (1.0 - xMinProb);
            }
        }
        return result;
    }

    public static double[] generateParetoDistribution(int xmin, double alpha,
            int maxX) {
        double[] xMinDistr = new double[xmin];
        return generateParetoDistribution(xmin, xMinDistr, alpha, maxX);
    }

    public static double[] generateParetoDistribution(int xmin, double alpha,
            int shifting, int maxX) {
        double[] xMinDistr = new double[xmin];
        return generateParetoDistribution(xmin, xMinDistr, alpha, shifting,
                maxX);
    }

    public static double[] generateParetoDistribution(int xmin,
            double[] xMinDistribution, double alpha, int maxX) {
        return generateParetoDistribution(xmin, xMinDistribution, alpha, 0,
                maxX);
    }

    public static double[] generateParetoDistribution(int xmin,
            double[] xMinDistribution, double alpha, int shifting, int maxX) {
        return generateParetoDistribution(xmin, xMinDistribution, alpha, 0,
                maxX, 1.0);
    }

    public static double[] generateParetoDistribution(int xmin,
            double[] xMinDistribution, double alpha, int shifting, int maxX,
            double stepsize) {
        double[] result = new double[(int) (maxX / stepsize) + 1];

        for (int i = xmin; i < result.length; i++) {
            result[i] = alpha * Math.pow((xmin - shifting) * stepsize, alpha)
                    / Math.pow((i - shifting) * stepsize, alpha + 1);
        }

        return result;
    }

    public static double[] cdfPareto(int xmin, double[] xMinDistribution,
            double alpha, int shifting, int maxX, double stepsize) {
        double[] result = new double[(int) (maxX / stepsize) + 1];
        for (int i = xmin; i <= result.length; i++) {
            result[i - 1] = 1.0 - Math.pow(xmin / (i * stepsize), alpha);
        }
        result = ArraysExt.scaleBy(result, result[result.length - 1]);

        return result;
    }

    public static double[] generateStudentsTDistribution(int n, int maxX,
            double stepsize) {
        int s = (int) (maxX / stepsize);
        double[] result = new double[2 * s + 1];

        double C = gamma((n + 1) / 2.0)
                / (Math.sqrt(n * Math.PI) * gamma(n / 2.0));

        for (int i = -s; i < s + 1; i++) {
            result[i + s] = C
                    * Math.pow(1 + Math.pow(i * stepsize, 2.0) / n,
                            -(n + 1) / 2.0);
        }

        return result;
    }

    public static double gamma(double z) {
        double result = 1 / z;
        for (int n = 1; n < 10000; n++) {
            result *= Math.pow(1 + 1.0 / n, z) / (1 + z / n);
        }
        return result;
    }

    public static int[] sampleBinomialDistribution(double[] distribution,
            int sampleSize) {
        double[] cdf = cdf(distribution);

        Random rand = new Random();

        int[] sample = new int[sampleSize];
        for (int i = 0; i < sampleSize; i++) {
            double r = rand.nextDouble();
            int pos = 0;
            while (pos < cdf.length && cdf[pos] < r) {
                pos++;
            }
            sample[i] = pos;
        }

        return sample;
    }

    public static double[] sampleParetoDistribution(int xmin,
            double[] xMinDistribution, double alpha, int shifting, int maxX,
            int sampleSize, double stepsize) {
        double[] cdf = cdfPareto(xmin, xMinDistribution, alpha, shifting, maxX,
                stepsize);

        Random rand = new Random();

        double[] sample = new double[sampleSize];
        for (int i = 0; i < sampleSize; i++) {
            double r = rand.nextDouble();
            int pos = 0;
            while (pos < cdf.length && cdf[pos] < r) {
                pos++;
            }
            sample[i] = pos * stepsize;
        }

        return sample;
    }

    public static int[] samplePoissonDistribution(int xmin,
            double[] xMinDistribution, double lambda, int maxX, int sampleSize) {
        double[] poisson = generatePoissonDistribution(xmin, xMinDistribution,
                lambda, maxX);
        double[] cdi = cdf(poisson);

        Random rand = new Random();

        int[] sample = new int[sampleSize];
        for (int i = 0; i < sampleSize; i++) {
            double r = rand.nextDouble();
            int pos = 0;
            while (pos < cdi.length && cdi[pos] < r) {
                pos++;
            }
            sample[i] = pos;
        }

        return sample;
    }

    public static int[] sampleZetaDistribution(int xmin, double alpha,
            int maxX, int sampleSize) {
        return sampleZetaDistribution(xmin, alpha, 0, maxX, sampleSize);
    }

    public static int[] sampleZetaDistribution(int xmin, double alpha,
            int shifting, int maxX, int sampleSize) {
        return sampleZetaDistribution(xmin, new double[xmin], alpha, shifting,
                maxX, sampleSize);
    }

    public static int[] sampleZetaDistribution(int xmin,
            double[] xMinDistribution, double alpha, int maxX, int sampleSize) {
        return sampleZetaDistribution(xmin, xMinDistribution, alpha, 0, maxX,
                sampleSize);
    }

    public static int[] sampleZetaDistribution(int xmin,
            double[] xMinDistribution, double alpha, int shifting, int maxX,
            int sampleSize) {
        double[] powerLaw = generateZetaDistribution(xmin, xMinDistribution,
                alpha, shifting, maxX);
        double[] cdi = cdf(powerLaw);
        cdi = ArraysExt.scaleBy(cdi, ArraysExt.max(cdi));
        // ArraysExt.print(cdi);

        Random rand = new Random();

        double[] normedXminCdf = xMinDistribution;
        normedXminCdf = ArraysExt.scaleBy(normedXminCdf,
                ArraysExt.sum(normedXminCdf));
        normedXminCdf = cdf(normedXminCdf);
        double[] normedCdf = Arrays.copyOfRange(cdi, xmin, cdi.length);
        // normedCdf = cdf(normedCdf);
        normedCdf = ArraysExt.subtract(normedCdf, cdi[xmin - 1]);
        normedCdf = ArraysExt.scaleBy(normedCdf,
                normedCdf[normedCdf.length - 1]);

        int[] sample = new int[sampleSize];

        // CLAUSET SAMPLING
        // int n1 = 0;
        // for (int i : ArraysExt.range(0, sampleSize))
        // if (rand.nextDouble() <= cdi[xmin - 1])
        // n1++;
        //
        // int pos = 0;
        //
        // for (int i : ArraysExt.range(0, n1)) {
        // double r = rand.nextDouble();
        // int t = 0;
        // while (t < normedXminCdf.length && normedXminCdf[t] < r)
        // t++;
        // sample[pos++] = t;
        // }
        // int n2 = sampleSize - n1;
        // double[] r2 = new double[n2];
        // for (int i : ArraysExt.range(0, n2))
        // r2[i] = rand.nextDouble();
        // Arrays.sort(r2);
        // int c = 0;
        // int k = 0;
        // for (int i : ArraysExt.range(xmin, cdi.length)) {
        // while (c < n2 && r2[c] < normedCdf[i-xmin])
        // c++;
        // for (int d : ArraysExt.range(k, c))
        // sample[pos++] = i;
        // k = c;
        // if (k >= n2)
        // break;
        // }
        for (int i = 0; i < sampleSize; i++) {
            // if (rand.nextDouble() <= cdi[xmin - 1]) {
            // double r = rand.nextDouble();
            // int pos = 0;
            // while (pos < cdi.length && normedXminCdf[pos] < r)
            // pos++;
            // sample[i] = pos;
            // } else {
            // double r = rand.nextDouble();
            // int pos = 0;
            // while (pos < cdi.length && normedCdf[pos] < r)
            // pos++;
            // sample[i] = pos+xmin;
            // }
            double r = rand.nextDouble();
            int pos = 0;
            while (pos < cdi.length && cdi[pos] < r) {
                pos++;
            }
            sample[i] = pos;
        }
        // ArraysExt.print(sample);
        // ArraysExt.print(cdf(toDistribution(sample)));
        return sample;
    }

    public static double[] sampleStudentsTDistribution(int n, int maxX,
            double stepsize, int shifting, int sampleSize) {
        double[] cdf = cdfStudentsT(n, maxX, stepsize, shifting);

        // ArraysExt.print(cdf);
        Random rand = new Random();

        double[] sample = new double[sampleSize];
        for (int i = 0; i < sampleSize; i++) {
            double r = rand.nextDouble();
            int pos = 0;
            while (pos < cdf.length && cdf[pos] < r) {
                pos++;
            }
            sample[i] = pos - cdf.length / 2;
        }

        return sample;
    }

    public static double[] cdf(double[] distribution) {
        double[] cdf = new double[distribution.length];
        // TODO: check if it still works
        double sum = 0.0;
        for (int i = 0; i < cdf.length; i++) {
            sum += distribution[i];
            cdf[i] = sum;
        }
        return cdf;
    }

    public static double[] cdfc(double[] distribution) {
        double[] cdf = DistributionParameterEstimation.cdf(distribution);
        return ArraysExt.subtract(1.0, cdf);
    }

    public static double regularizedIncompleteBeta(double x, double a, double b) {
        return incompleteBeta(x, a, b) / beta(a, b);
    }

    public static double[] cdfStudentsT(int n, int maxX, double stepsize) {
        return cdfStudentsT(n, maxX, stepsize, 0);
    }

    public static double[] cdfStudentsT(int n, int maxX, double stepsize,
            int shifting) {
        int count = (int) (maxX / stepsize);
        double[] result = new double[2 * count + 1];

        for (int i = 0; i < result.length; i++) {
            double x = (i - count - shifting) * stepsize;
            if (n == 1) {
                result[i] = 0.5 + 1 / Math.PI * Math.atan(x);
            } else if (n == 2) {
                result[i] = 0.5 * (1 + (x) / (Math.sqrt(2 + Math.pow(x, 2.0))));
            } else {
                result[i] = regularizedIncompleteBeta(
                        (x + Math.sqrt(Math.pow(x, 2.0) + n))
                        / (2 * Math.sqrt(Math.pow(x, 2.0) + n)),
                        n / 2.0, n / 2.0);
            }
        }

        result = ArraysExt.subtract(result[0], result);
        result = ArraysExt.scaleBy(result, result[result.length - 1]);

        return result;
    }

    private static Map<Pair<Integer, Integer>, BigDecimal> bds = new HashMap<Pair<Integer, Integer>, BigDecimal>();

    public static double[] generateBinomialDistribution(double p, int n,
            int shifting) {
        double[] result = new double[n + 1];

        for (int k = -shifting; k < result.length - shifting; k++) {
            BigDecimal bd = new BigDecimal(1.0D);
            if (!bds.containsKey(new Pair<Integer, Integer>(n, k))) {
                for (int j = k + 1; j <= n; j++) {
                    bd = bd.multiply(new BigDecimal(j));
                }
                bds.put(new Pair<Integer, Integer>(n, k), bd);
            }
            bd = bds.get(new Pair<Integer, Integer>(n, k));
            bd = bd.divide(fak(n - k));
            result[k + shifting] = bd.doubleValue();
            result[k + shifting] *= Math.pow(p, k) * Math.pow(1 - p, n - k);
        }

        result = ArraysExt.scaleBy(result, ArraysExt.sum(result) > 0
                                           ? ArraysExt.sum(result)
                                           : 1.0);

        return result;
    }

    public static double beta(double a, double b) {
        double stepsize = 0.01;
        double result = 0.0;

        for (double i = 0.0; i + stepsize <= 1.0; i += stepsize) {
            result += Math.pow(i, a - 1) * Math.pow(1.0 - i, b - 1.0);
        }

        return result;
    }

    public static double incompleteBeta(double x, double a, double b) {
        double stepsize = 0.01;
        double result = 0.0;

        for (double i = 0; i < x; i += stepsize) {
            result += Math.pow(i, a - 1) * Math.pow(1 - i, b - 1);
        }
        return result;
    }

    public static double[] toDistribution(int[] xSeries) {
        double[] distribution = new double[ArraysExt.max(xSeries) + 1];

        for (int i : xSeries) {
            distribution[i]++;
        }

        distribution = ArraysExt.scaleBy(distribution,
                ArraysExt.sum(distribution));
        return distribution;
    }

    public BinomialParameters estimateBinomialParameters() {
        double[] x = toDistribution(this.x);
        // int[] xmins = ArraysExt.unique(this.x);
        // Arrays.sort(xmins);
        // xmins = Arrays.copyOf(xmins, xmins.length - 1);
        int[] xmins = new int[]{0, 1};
        int[] shiftings = ArraysExt.range(-ArraysExt.max(this.x), 1);
        // int[] shiftings = new int[1];

        double[] ps = new double[100];
        for (int i = 1; i < ps.length; i++) {
            ps[i - 1] = i / 100.0;
        }

        // int[] ns = new int[100];
        // for (int i = 0; i < ns.length; i++)
        // ns[i] = i + 1;
        // int[] ns = ArraysExt.range(10,ArraysExt.max(this.x)/2);
        // int[] ns = ArraysExt.range(10,ArraysExt.max(this.x));
        int[] ns = ArraysExt.range(10, 21);

        double minDist = Double.MAX_VALUE;
        int minXmin = -1;
        double[] xMinDistribution = null;
        double minP = -1;
        int minN = -1;
        int minShifting = -1;

        for (double p : ps) {
            for (int n : ns) {
                for (int shifting : shiftings) {
                    if (Math.abs(shifting) >= n - 2) {
                        continue;
                    }
                    double[] binomial = generateBinomialDistribution(p, n,
                            shifting);
                    for (int xmin : xmins) {
                        double[] xMinDistr = Arrays.copyOf(x, xmin);
                        double[] binomialExt = ArraysExt.merge(binomial,
                                ArraysExt.rep(0.0, x.length - binomial.length));

                        double dist = distributionDistance(xmin, x,
                                binomialExt, DISTANCE_TYPE.KS_DISTANCE);
                        if (dist < minDist) {
                            minDist = dist;
                            minXmin = xmin;
                            xMinDistribution = xMinDistr;
                            minP = p;
                            minN = n;
                            minShifting = shifting;
                        }
                    }
                }
            }
        }
        // double[] minBinomial = generateBinomialDistribution(minP, minN,
        // minShifting);
        // minBinomial = Arrays.copyOfRange(minBinomial, minXmin,
        // minBinomial.length);
        // minBinomial = ArraysExt
        // .scaleBy(minBinomial, ArraysExt.sum(minBinomial));
        // ArraysExt.print(cdf(minBinomial));
        // double[] empirical = Arrays.copyOfRange(x, minXmin, x.length);
        // empirical = ArraysExt.scaleBy(empirical, ArraysExt.sum(empirical));
        // ArraysExt.print(cdf(empirical));
        return new BinomialParameters(minXmin, xMinDistribution, minP, minN,
                minShifting, minDist);
    }

    public PoissonParameters estimatePoissonParameters() {
        double[] x = toDistribution(this.x);
        int[] xmins = new int[ArraysExt.max(this.x)];
        for (int i = 0; i < xmins.length; i++) {
            xmins[i] = i;
        }

        double[] lambdas = new double[200];
        for (int i = 0; i < 200; i++) {
            lambdas[i] = (i + 1) / 10.0;
        }

        double minDist = Double.MAX_VALUE;
        int minXmin = -1;
        double[] xMinDistribution = null;
        double minLambda = -1;

        for (int xmin : xmins) {
            double[] xMinDistr = Arrays.copyOf(x, xmin);
            double[] xDistr = Arrays.copyOfRange(x, xmin, x.length);
            xDistr = ArraysExt.scaleBy(xDistr, ArraysExt.sum(xDistr));
            for (double lambda : lambdas) {
                double[] poisson = generatePoissonDistribution(0,
                        new double[0], lambda, x.length);

                double dist = distributionDistance(0, xDistr, poisson,
                        DISTANCE_TYPE.RSS);
                if (dist < minDist) {
                    minLambda = lambda;
                    minDist = dist;
                    minXmin = xmin;
                    xMinDistribution = xMinDistr;
                }
            }
        }
        return new PoissonParameters(minLambda, minXmin, xMinDistribution,
                minDist);
    }

    public double calculatePoissonSignificance(PoissonParameters params) {
        return this.calculatePoissonSignificance(params.getLambda(),
                params.getXMin(), params.getXMinDistribution());
    }

    public double calculatePoissonSignificance(double lambda, int xmin,
            double[] xMinDistr) {
        int it = 1000, sigCount = 0;
        double p = 0.0;

        double[] x = toDistribution(this.x);

        double[] theoreticalDistr = generatePoissonDistribution(xmin,
                new double[xMinDistr.length], lambda, this.x.length);
        double empiricalDistance = distributionDistance(xmin, x,
                theoreticalDistr, DISTANCE_TYPE.RSS);
        Log.println("Empirical distance=" + empiricalDistance);

        for (int i = 0; i < it; i++) {
            int[] samplePoisson = samplePoissonDistribution(xmin, xMinDistr,
                    lambda, ArraysExt.max(this.x), this.x.length);
            DistributionParameterEstimation newBla = new DistributionParameterEstimation(
                    samplePoisson);
            PoissonParameters params = newBla.estimatePoissonParameters();

            double[] sampleDistr = toDistribution(samplePoisson);
            double syntheticDistance = distributionDistance(
                    params.getXMin(),
                    sampleDistr,
                    generatePoissonDistribution(params.getXMin(),
                            new double[params.getXMin()], params.getLambda(),
                            samplePoisson.length), DISTANCE_TYPE.RSS);
            if (syntheticDistance > empiricalDistance) {
                sigCount++;
                p = sigCount / (double) it;
            }
            p = sigCount / (double) (i + 1);
        }
        return p;
    }

    public double calculateBinomialSignificance(BinomialParameters params) {
        int it = 1000, sigCount = 0;
        double p = 0.0;

        double[] x = toDistribution(this.x);

        double[] theoreticalDistr = generateBinomialDistribution(params.getP(),
                params.getN(), params.getShifting());
        double empiricalDistance = distributionDistance(params.getXMin(), x,
                theoreticalDistr, DISTANCE_TYPE.KS_DISTANCE);
        Log.println("Empirical distance=" + empiricalDistance);

        // theoreticalDistr = Arrays.copyOfRange(theoreticalDistr,
        // params.getXMin(), theoreticalDistr.length);
        // theoreticalDistr = ArraysExt.scaleBy(theoreticalDistr,
        // ArraysExt.sum(theoreticalDistr));
        for (int i = 0; i < it; i++) {
            int[] sampleBinomial = sampleBinomialDistribution(theoreticalDistr,
                    this.x.length);
            DistributionParameterEstimation newBla = new DistributionParameterEstimation(
                    sampleBinomial);
            BinomialParameters newParams = newBla.estimateBinomialParameters();
            System.out.println(newParams);
            double[] sampleDistr = toDistribution(sampleBinomial);
            double syntheticDistance = distributionDistance(
                    newParams.getXMin(),
                    sampleDistr,
                    generateBinomialDistribution(newParams.getP(),
                            newParams.getN(), newParams.getShifting()),
                    DISTANCE_TYPE.KS_DISTANCE);
            if (syntheticDistance > empiricalDistance) {
                sigCount++;
                p = sigCount / (double) it;
            }
            p = sigCount / (double) (i + 1);
        }
        return p;
    }

    public enum DISTANCE_TYPE {
        KS_DISTANCE, RSS
    }

    public static double[] discretizeParetoDistribution(int xmin,
            double[] xMinDistribution, double alpha, int shifting, int maxX,
            double stepsize) {
        double[] result = new double[(int) (maxX / stepsize) + 1];

        for (int i = xmin; i < result.length; i++) {
            double y1 = alpha * 1
                    / Math.pow((i - 0.01 - shifting) * stepsize, alpha + 1);
            double y2 = alpha * 1
                    / Math.pow((i + 0.01 - shifting) * stepsize, alpha + 1);
            result[i] = (y1 + y2) / 2;
            // result[i] = alpha * Math.pow((xmin - shifting) * stepsize, alpha)
            // / Math.pow((i - shifting) * stepsize, alpha + 1);
        }
        // result = ArraysExt.scaleBy(result, ArraysExt.sum(result));

        return result;
    }

    public static double distributionDistance(int xmin, double[] dist1,
            double[] dist2, DISTANCE_TYPE distType) {
        return distributionDistance(xmin, dist1, dist2, distType, false);
    }

    public static double distributionDistance(int xmin, double[] dist1,
            double[] dist2, DISTANCE_TYPE distType, boolean inputCdf) {

        if (!inputCdf) {
            dist1 = Arrays.copyOfRange(dist1, xmin, dist1.length);
            dist2 = Arrays.copyOfRange(dist2, xmin, dist2.length);

            dist1 = ArraysExt.scaleBy(dist1, ArraysExt.sum(dist1));
//			dist2 = ArraysExt.scaleBy(dist2, ArraysExt.sum(dist2));

            // take CDI
            dist1 = cdf(dist1);
            dist2 = cdf(dist2);
            // ArraysExt.print(dist1);
            // ArraysExt.print(dist2);
        }
        double[] diff = ArraysExt.abs(ArraysExt.subtract(dist1, dist2, true));

        if (distType.equals(DISTANCE_TYPE.KS_DISTANCE)) {
            return ArraysExt.max(diff);
        } else if (distType.equals(DISTANCE_TYPE.RSS)) {
            return ArraysExt.sum(ArraysExt.pow(diff, 2.0));
        } else {
            return Double.MAX_VALUE;
        }
    }

    public static double cdfDistance(int xmin, double[] dist1, double[] dist2,
            DISTANCE_TYPE distType) {
        return distributionDistance(xmin, dist1, dist2, distType, true);
    }

    public static long fak(long n) {
        long result = 1;

        for (int i = 2; i <= n; i++) {
            result *= i;
        }

        return result;
    }

    private static Map<Integer, BigDecimal> fakMap = new HashMap<Integer, BigDecimal>();

    public static BigDecimal fak(int n) {
        if (!(fakMap.containsKey(n))) {
            BigDecimal result = new BigDecimal(1.0D);
            if (n >= 2) {
                result = result.multiply(new BigDecimal(n));
                result = result.multiply(fak(n - 1));
            }
            fakMap.put(n, result);
        }
        return fakMap.get(n);
    }

    public static void main(String[] args) {
        ArraysExt.print(toDistribution(new int[]{5, 5, 9, 6, 5, 5, 5, 5, 7, 5,
            8, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 5, 5, 5, 5, 5, 5, 5,
            5, 7, 5, 7, 10, 5, 5, 7, 9, 10, 5, 5, 6, 5, 5, 5, 5, 5, 5, 5,
            5, 6, 5, 7, 7, 5, 5, 6, 5, 3, 5, 5, 5, 6, 7, 5, 5, 5, 5, 5, 5,
            5, 5, 5, 5, 6, 5, 5, 5, 5, 5, 7, 5, 6, 5, 5, 5, 5, 5, 6, 5, 6,
            5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 5, 5, 5, 5, 5, 5, 5, 5, 5,
            5, 5, 5, 6, 5, 5, 7, 5, 5, 7, 5, 5, 6, 5, 5, 5, 5, 6, 5, 6, 7,
            6, 5, 5, 8, 5, 5, 5, 3, 6, 5, 5, 5, 5, 5, 5, 5, 6, 5, 9, 5, 5,
            5, 5, 5, 3, 6, 5, 5, 5, 5, 7, 5, 5, 5, 5, 6, 5, 5, 6, 5, 6, 5,
            5, 5, 5, 5, 5, 5, 7, 5, 6, 6, 5, 5, 5, 6, 5, 5, 5, 5, 5, 5, 5,
            5, 5, 5, 5, 7, 5, 5, 6, 5, 3, 5, 5, 5, 6, 5, 5, 5, 5, 5, 7, 5,
            5, 5, 5, 5, 5, 5, 7, 5, 5, 6, 5, 6, 5, 7, 5, 6, 5, 5, 6, 5, 5,
            5, 6, 5, 6, 7, 8, 5, 5, 6, 5}));
        // double[] pl = cdf(generateZetaDistribution(3, 2.51, 453));
        // ArraysExt.print(ArraysExt.scaleBy(pl,pl[pl.length-1]));
        // double[] bla = generateBinomialDistribution(0.01, 20, -3);
        // // bla = ArraysExt.scaleBy(bla, ArraysExt.sum(bla));
        // int xmin = 0;
        // ArraysExt.print(Arrays.copyOfRange(bla, xmin, bla.length));
        // int[] sample = sampleBinomialDistribution(bla, 100000);
        // ArraysExt.print(sample);
        // ArraysExt.print(toDistribution(sample));
        // System.out.println(ArraysExt.max(sample));
        // DistributionParameterEstimation est = new
        // DistributionParameterEstimation(
        // sample);
        // System.out.println(est.estimateBinomialParameters());

        // int[] degrees = new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
        // 4,
        // 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
        // 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 8, 8, 8, 8, 9, 9,
        // 9, 9, 9, 9, 9, 10, 12, 13, 15, 16, 17, 20, 21, 25, 29, 56};
        // // double[] hist = ArraysExt.hist(degrees);
        // // ArraysExt.print(ArraysExt.scaleBy(hist, ArraysExt.sum(hist)));
        // DistributionParameterEstimation est = new
        // DistributionParameterEstimation(
        // degrees);
        // // est.doStudentsTAnalysis();
        // est.doZetaAnalysis();
    }
}
