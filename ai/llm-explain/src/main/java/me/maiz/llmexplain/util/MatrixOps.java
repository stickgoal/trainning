package me.maiz.llmexplain.util;

import java.util.Arrays;

/**
 * 简单的矩阵运算工具 — 帮助理解神经网络中的基础数学操作。
 * <p>
 * 面试重点：矩阵运算是一切深度学习的基础，LLM中的每个步骤
 * (注意力、前馈网络)本质上都是矩阵乘法 + 非线性变换。
 */
public class MatrixOps {

    // ========== 向量运算 ==========

    /**
     * 点积 (Dot Product): 两个向量对应元素相乘再求和。
     * 意义：衡量两个向量的相似度/相关性。值越大，方向越一致。
     * <br>
     * 面试考点：Attention中的 score = Q·K^T 就是点积运算。
     */
    public static double dotProduct(double[] a, double[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("向量维度不匹配: " + a.length + " vs " + b.length);
        }
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    /**
     * 向量加法 (Element-wise Add)
     * 意义：残差连接 (Residual Connection) 的基础操作。
     * 面试考点：Transformer中的 Add & Norm 指的就是残差连接+层归一化。
     */
    public static double[] add(double[] a, double[] b) {
        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] + b[i];
        }
        return result;
    }

    /**
     * 向量逐元素乘法
     */
    public static double[] elementWiseMultiply(double[] a, double[] b) {
        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] * b[i];
        }
        return result;
    }

    /**
     * 标量乘法: vec = vec * scalar
     */
    public static double[] scale(double[] vec, double scalar) {
        double[] result = new double[vec.length];
        for (int i = 0; i < vec.length; i++) {
            result[i] = vec[i] * scalar;
        }
        return result;
    }

    // ========== 矩阵运算 ==========

    /**
     * 矩阵乘法: A (m×n) × B (n×p) = C (m×p)
     * 这是神经网络中最核心的运算，没有之一。
     * <p>
     * 面试考点：整个Transformer就是矩阵乘法堆叠起来的 —
     * W_Q、W_K、W_V 投影矩阵、FFN的W1/W2，全都是矩阵乘法。
     */
    public static double[][] matMul(double[][] A, double[][] B) {
        int m = A.length, n = A[0].length, p = B[0].length;
        if (n != B.length) {
            throw new IllegalArgumentException("矩阵维度不匹配: A列=" + n + ", B行=" + B.length);
        }
        double[][] C = new double[m][p];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < p; j++) {
                for (int k = 0; k < n; k++) {
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return C;
    }

    /**
     * 矩阵 × 向量: A (m×n) × v (n) = result (m)
     */
    public static double[] matVecMul(double[][] A, double[] v) {
        int m = A.length, n = A[0].length;
        if (n != v.length) {
            throw new IllegalArgumentException("维度不匹配: 矩阵列=" + n + ", 向量长度=" + v.length);
        }
        double[] result = new double[m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                result[i] += A[i][j] * v[j];
            }
        }
        return result;
    }

    /**
     * 矩阵转置: A (m×n) → A^T (n×m)
     */
    public static double[][] transpose(double[][] A) {
        int m = A.length, n = A[0].length;
        double[][] T = new double[n][m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                T[j][i] = A[i][j];
            }
        }
        return T;
    }

    // ========== 激活函数 ==========

    /**
     * Softmax: 将任意实数向量转换为概率分布。
     * 公式: softmax(x)_i = exp(x_i) / Σexp(x_j)
     * <p>
     * 面试考点：Attention中的归一化、输出层的概率分布计算，都用softmax。
     * 面试常问：softmax的数值稳定性问题（减去最大值）。
     */
    public static double[] softmax(double[] x) {
        double max = Arrays.stream(x).max().orElse(0);
        double[] exp = new double[x.length];
        double sum = 0;
        for (int i = 0; i < x.length; i++) {
            exp[i] = Math.exp(x[i] - max); // 减去最大值保证数值稳定性
            sum += exp[i];
        }
        for (int i = 0; i < x.length; i++) {
            exp[i] /= sum;
        }
        return exp;
    }

    /**
     * ReLU: max(0, x) — 最常用的激活函数。
     * 面试考点：为什么比sigmoid/tanh好？缓解梯度消失。
     */
    public static double[] relu(double[] x) {
        double[] result = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            result[i] = Math.max(0, x[i]);
        }
        return result;
    }

    /**
     * GELU (近似): 在Transformer中广泛使用的激活函数。
     * GELU ≈ 0.5x(1 + tanh(√(2/π)(x + 0.044715x³)))
     * 面试考点：BERT、GPT都用了GELU而不是ReLU。
     */
    public static double[] gelu(double[] x) {
        double[] result = new double[x.length];
        double sqrt2pi = Math.sqrt(2.0 / Math.PI);
        for (int i = 0; i < x.length; i++) {
            double x3 = x[i] * x[i] * x[i];
            result[i] = 0.5 * x[i] * (1 + Math.tanh(sqrt2pi * (x[i] + 0.044715 * x3)));
        }
        return result;
    }

    // ========== 归一化 ==========

    /**
     * Layer Normalization: 层归一化
     * 对每个样本的所有特征做归一化: x' = (x - μ) / σ * γ + β
     * <p>
     * 面试考点：LayerNorm vs BatchNorm 的区别。Transformer用LayerNorm
     * 是因为序列长度可变，BatchNorm在NLP中效果不好。
     */
    public static double[] layerNorm(double[] x, double[] gamma, double[] beta) {
        double mean = Arrays.stream(x).average().orElse(0);
        double var = Arrays.stream(x).map(v -> Math.pow(v - mean, 2)).average().orElse(0);
        double std = Math.sqrt(var + 1e-6); // 加小常数防止除零

        double[] result = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            result[i] = gamma[i] * (x[i] - mean) / std + beta[i];
        }
        return result;
    }

    // ========== 工具 ==========

    /**
     * 打印向量
     */
    public static void printVec(String label, double[] v) {
        System.out.print(label + " [");
        for (int i = 0; i < v.length; i++) {
            System.out.printf("%7.4f", v[i]);
            if (i < v.length - 1) System.out.print(", ");
        }
        System.out.println("]");
    }

    /**
     * 打印矩阵
     */
    public static void printMat(String label, double[][] m) {
        System.out.println(label + ":");
        for (double[] row : m) {
            System.out.print("  [");
            for (int j = 0; j < row.length; j++) {
                System.out.printf("%8.4f", row[j]);
                if (j < row.length - 1) System.out.print(", ");
            }
            System.out.println("]");
        }
    }

    /**
     * 打印整数向量
     */
    public static void printIntVec(String label, int[] v) {
        System.out.print(label + " [");
        for (int i = 0; i < v.length; i++) {
            System.out.print(v[i]);
            if (i < v.length - 1) System.out.print(", ");
        }
        System.out.println("]");
    }
}
