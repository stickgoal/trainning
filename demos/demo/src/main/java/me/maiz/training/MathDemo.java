package me.maiz.training;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.descriptive.moment.*;
import org.apache.commons.math3.stat.descriptive.rank.Max;
import org.apache.commons.math3.stat.descriptive.rank.Min;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.apache.commons.math3.stat.descriptive.summary.Product;
import org.apache.commons.math3.stat.descriptive.summary.Sum;
import org.apache.commons.math3.stat.descriptive.summary.SumOfSquares;

/**
 * Created by Lucas on 2018-03-29.
 */
public class MathDemo {
    public static void main(String[] args) {
        double[] values = new double[] { 0.33, 1.33,0.27333, 0.3, 0.501,
                0.444, 0.44, 0.34496, 0.33,0.3, 0.292, 0.667 };
        Min min = new Min();
        Max max = new Max();

        Mean mean = new Mean(); // 算术平均值
        Product product = new Product();//乘积
        Sum sum = new Sum();
        Variance variance = new Variance();//方差
        System.out.println("min: " +min.evaluate(values));
        System.out.println("max: " +max.evaluate(values));
        System.out.println("mean: " +mean.evaluate(values));
        System.out.println("product:" + product.evaluate(values));
        System.out.println("sum: " +sum.evaluate(values));
        System.out.println("variance:" + variance.evaluate(values));

        Percentile percentile = new Percentile(); // 百分位数
        GeometricMean geoMean = new GeometricMean(); // 几何平均数,n个正数的连乘积的n次算术根叫做这n个数的几何平均数
        Skewness skewness = new Skewness(); //Skewness();
        Kurtosis kurtosis = new Kurtosis(); //Kurtosis,峰度
        SumOfSquares sumOfSquares = new SumOfSquares(); // 平方和
        StandardDeviation StandardDeviation =new StandardDeviation();//标准差
        System.out.println("80 percentilevalue: "
                + percentile.evaluate(values,80.0));
        System.out.println("geometricmean: " + geoMean.evaluate(values));
        System.out.println("skewness:" + skewness.evaluate(values));
        System.out.println("kurtosis:" + kurtosis.evaluate(values));
        System.out.println("sumOfSquares:" + sumOfSquares.evaluate(values));
        System.out.println("StandardDeviation: " +StandardDeviation.evaluate(values));

        System.out.println("-------------------------------------");
        // Create a real matrix with two rowsand three columns
        double[][] matrixData = { {1d,2d,3d},{2d,5d,3d}};
        RealMatrix m = new Array2DRowRealMatrix(matrixData);
        System.out.println(m);
        // One more with three rows, twocolumns
        double[][] matrixData2 = { {1d,2d},{2d,5d}, {1d, 7d}};
        RealMatrix n = new Array2DRowRealMatrix(matrixData2);
        // Note: The constructor copies  the input double[][] array.
        // Now multiply m by n
        RealMatrix p = m.multiply(n);
        System.out.println("p:"+p);
        System.out.println(p.getRowDimension());    // 2
        System.out.println(p.getColumnDimension()); // 2
        // Invert p, using LUdecomposition
        RealMatrix pInverse = new LUDecomposition(p).getSolver().getInverse();
        System.out.println(pInverse);
    }


}
