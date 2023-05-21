package com.example.behealthy_java;

import java.util.*;
import java.io.*;

class SimplexMethod {

    static float[][] table;
    static String[] row_variables;        //все переменные в строке
    static String[] basic_variables;       //базисные переменные в строке
    static boolean problemType = false;      //тип проблемы (максимизация/минимизация)
    static boolean canPrint = true;
    User user;
    public Double caloriesNorm, proteinsNorm,
            fatsNorm, carboNorm, b_ccal, b_proteins, b_fats, b_carbo,
            l_ccal, l_proteins, l_fats, l_carbo, d_ccal, d_proteins, d_fats, d_carbo;
    public double[] ccal, proteins, fats, carbo;
    //b = breakfast, l = lunch, d = dinner

    SimplexMethod(){
        this.ccal = new double[3];
        this.proteins = new double[3];
        this.fats = new double[3];
        this.carbo = new double[3];
    }

    public void initialize() {

        int type = 1;
        /*if (user.PurposeNum==1){
            type = 1;
        }
        else {
            type = 2;
        }*/

        problemType = type == 1;

        int n = 3; //количество переменных (вес каждого блюда)
        //int m = 6; //количество ограничений (БЖУ + ккал на каждый прием пищи)
        int m = 3;

        table = new float[m+1][n+m+2];

        //целевая функция
        table[0][0] = 1;
        double[] objective_arr = new double[n];


        //коэффиценты целевой функции
        for (int i=0; i<n; i++) {objective_arr[i] = problemType ? this.proteins[i] : -this.proteins[i];}

        for (int i=0; i<objective_arr.length; i++) {table[0][i+1] = (float) -objective_arr[i];}

        //коэффиценты ограничений
        for (int j=0; j<m; j++) {
            int choice = 3; //2 = ">="; 1 = "<="; 3 = "="
            double b_val = 1; //что после знака неравенства
            table[j+1][0] = 0;
            if (j==0){
                for (int i=0; i<n; i++) {
                    table[j+1][i+1] = (float) this.ccal[i];
                }
                choice = 1;
                b_val = (this.caloriesNorm);
                System.out.println("Кейс 0: " + Arrays.toString(this.proteins) + "|" + b_val );
            } else if (j==1) {
                for (int i=0; i<n; i++) {
                    table[j+1][i+1] = (float) this.fats[i];
                }
                choice = 1;
                if (type==1){b_val = this.fatsNorm;}
                else {b_val = this.fatsNorm;}
                System.out.println("Кейс 1: " + Arrays.toString(this.fats) + "|" + b_val );
            } else if (j==2) {
                for (int i=0; i<n; i++) {
                    table[j+1][i+1] = (float) this.carbo[i];
                }
                choice = 1;
                if (type==1){b_val = this.carboNorm;}
                else {b_val = this.carboNorm;}
                System.out.println("Кейс 2: " + Arrays.toString(this.carbo) + "|" + b_val );
            } /*else if (j==3) {
                table[j+1][1] = (float) this.ccal[0];
                table[j+1][2] = 0;
                table[j+1][3] = 0;
                choice = 3;
                b_val = this.caloriesNorm*0.3;
                System.out.println("Кейс 3: " + this.ccal[0] + "|" + b_val );
            } else if (j==4) {
                table[j+1][1] = 0;
                table[j+1][2] = (float) this.ccal[1];
                table[j+1][3] = 0;
                choice = 3;
                b_val = this.caloriesNorm*0.45;
                System.out.println("Кейс 4: " + this.ccal[1] + "|" + b_val );
            } else if (j==5) {
                table[j+1][1] = 0;
                table[j+1][2] = 0;
                table[j+1][3] = (float) this.ccal[2];
                b_val = this.caloriesNorm*0.25;
                choice = 3;
                System.out.println("Кейс 5: " + this.ccal[2] + "|" + b_val );
            }*/

            if(choice == 1) {
                table[j+1][n+j+1] = 1;
            } else if(choice == 2) {
                table[j+1][n+j+1] = -1;
            } else {
                table[j+1][n+j+1] = 0;
            }
        }

        System.out.println("Составленная таблица:");
        System.out.println(Arrays.deepToString(table));
        System.out.println();

        fill_variables(n, m);

        System.out.println(Arrays.toString(row_variables));
        System.out.println(Arrays.toString(basic_variables));
        System.out.println();

        optimize_table();

        System.out.println();
        System.out.println("Final table: " + Arrays.deepToString(table));
        System.out.println();

        if(canPrint) {
            print_solution();
        }
    }


    public static void optimize_table() {

        int iter = 1;

        while (ifminExists()) {
            int index = min_index();

            float min_ratio = Float.MAX_VALUE;
            int min_index = 0;

            boolean state = false;

            for (int j=1; j<table.length; j++) {

                if (table[j][index] > 0) {           // must be >= 0
                    state = true;
                    float ratio = table[j][table[0].length-1] / table[j][index];

                    if(ratio < min_ratio) {
                        min_ratio = ratio;
                        min_index = j;
                    }
                }
            }

            if(!state) {
                System.out.println("******* This system has unbounded solution *******");
                canPrint = false;
                break;

            } else {

                System.out.println("************ Iteration - " + iter + " ************");
                System.out.println("Incoming Variable is: " + row_variables[index]);
                System.out.println("Outgoing Variable is: " + basic_variables[min_index]);
                System.out.println();

                basic_variables[min_index] = row_variables[index];   // swap basic variables...

                row_operation(index, min_index);       // row operation in table...

                iter++;
            }

        }
    }


    public static void row_operation(int index, int min_index) {

        float num = table[min_index][index];

        for (int i=0; i<table[0].length; i++) {
            table[min_index][i] = table[min_index][i] / num;
        }

//        System.out.println(Arrays.deepToString(table));

        for (int i=0; i<table.length; i++) {
            if (i != min_index) {
                float cal = -table[i][index];

                for (int j=0; j<table[0].length; j++) {
                    table[i][j] = cal*table[min_index][j] + table[i][j];
                }

            }
        }

//        System.out.println(Arrays.deepToString(table));


    }


    public static void print_solution() {

        System.out.println("*************** Optimal Solution: *********************");

        for (int i=1; i<row_variables.length-1; i++) {
            boolean state = false;

            for (int j=1; j<basic_variables.length; j++) {
                if(row_variables[i].equals(basic_variables[j])) {
                    System.out.println("The value of " + row_variables[i] + " is: " + table[j][table[0].length-1]);
                    state = true;
                    break;
                }

            }

            if(!state) {
                System.out.println("The value of " + row_variables[i] + " is: " + 0);
            }
        }

        if (problemType) {
            System.out.println("The value of Z_max is: " + table[0][table[0].length-1]);
        } else {
            System.out.println("The value of Z_min is: " + -table[0][table[0].length-1]);
        }


    }


    public static void fill_variables(int n, int m) {

        basic_variables = new String[m+1];
        basic_variables[0] = "c";

        for (int i=0; i<m; i++) {
            basic_variables[i+1] = "s" + (i+1);
        }

        row_variables = new String[n+m+2];
        row_variables[0] = "z";
        for (int i=0; i<n; i++) {
            row_variables[i+1] = "x" + (i+1);
        }

        for (int i=0; i<m; i++) {
            row_variables[n+i+1] = "s" + (i+1);
        }

        row_variables[n+m+1] = "b";

    }


    public static boolean ifminExists() {
        boolean state = false;

        for (int i=0; i<table[0].length; i++) {
            if (table[0][i] < 0) {
                state = true;
                break;
            }
        }

        return state;
    }

    public static int min_index() {

        int index = 0;
        float min = Float.MAX_VALUE;

        for (int i=0; i<table[0].length; i++) {
            if (table[0][i] < min) {
                index = i;
                min = table[0][i];
            }
        }

        return index;
    }
}