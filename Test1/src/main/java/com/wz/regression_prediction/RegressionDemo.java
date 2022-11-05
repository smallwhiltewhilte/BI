package com.wz.regression_prediction;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import au.com.bytecode.opencsv.CSVReader;

/***
 *
 * @author miaoyibo
 *
 */
public class RegressionDemo {

    public static void main(String[] args) throws IOException {
        CSVReader reader = new CSVReader(new FileReader(System.getProperty("user.dir")+"/Test1/src/main/resources/data.csv")); // 数据源
        FileWriter writer = new FileWriter(System.getProperty("user.dir")+"/Test1/src/main/resources/out.csv");
        System.out.println("请输入月租金值");
        Scanner scanner = new Scanner(System.in);
        int rent = scanner.nextInt();
        List<String[]> myEntries = reader.readAll();
        List<Double> coordinate_x = new ArrayList<>();
        List<Double> coordinate_y = new ArrayList<>();
        for (String[] entry : myEntries) {
            coordinate_x.add(Double.parseDouble(entry[0]));
            coordinate_y.add(Double.parseDouble(entry[1]));
        }
        Double[] doubles_x = coordinate_x.toArray(new Double[coordinate_x.size()]);
        Double[] doubles_y = coordinate_y.toArray(new Double[coordinate_y.size()]);

        double[] independentValues = Arrays.stream(doubles_x).mapToDouble(Double::valueOf).toArray();
        double[] dependentValues = Arrays.stream(doubles_y).mapToDouble(Double::valueOf).toArray();

        RegressionEquation re = new RegressionEquation(dependentValues, independentValues);
        RegressionModel regressionModel = re.getRegressionModel();
        writer.write("a=" + regressionModel.getA() + "\n");
        writer.write("b=" + regressionModel.getB() + "\n");
        writer.write("coefficient of determination==" + regressionModel.getR() + "\n");
        writer.write("T test==" + new BigDecimal(+regressionModel.getP()) + "\n");
        double result = new BigDecimal((regressionModel.getA() + regressionModel.getB() * rent)).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        writer.write("当每平方米月租金" + rent + "元时，出租率为" + result + "%\n");
        writer.close();
    }

}