import java.io.File;

public class Main {

    public static void main(String[] args) {
	// write your code here

        try {


            String[][] data = new String[5][2];

            data[0][0] = "data/car-s-91.crs";
            data[0][1] = "data/car-s-91.stu";

            data[1][0] = "data/car-f-92.crs";
            data[1][1] = "data/car-f-92.stu";


            data[2][0] = "data/kfu-s-93.crs";
            data[2][1] = "data/kfu-s-93.stu";

            data[3][0] = "data/tre-s-92.crs";
            data[3][1] = "data/tre-s-92.stu";

            data[4][0] = "data/yor-f-83.crs";
            data[4][1] = "data/yor-f-83.stu";

            Graph g = new Graph(data[4][0], data[4][1]);




            for(int i = 0; i < 8; i++) {
                for(int j = 0; j < data.length; j++) {

                    String dataName = "undefined";

                    switch(j) {
                        case 0:
                            dataName = "CAR91";
                            System.out.println("CAR91");
                            break;

                        case 1:
                            dataName = "CAR92";
                            System.out.println("CAR92");
                            break;

                        case 2:
                            dataName = "KFU93";
                            System.out.println("KFU93");
                            break;

                        case 3:
                            dataName = "TRE92";
                            System.out.println("TRE92");
                            break;

                        case 4:
                            dataName = "YOR83";
                            System.out.println("YOR83");
                            break;


                    }




                    switch(i) {
                        case 0:
                            scheme1(data[j], dataName);
                            break;
                        case 1:
                            scheme2(data[j], dataName);
                            break;
                        case 2:
                            scheme3(data[j], dataName);
                            break;
                        case 3:
                            scheme4(data[j], dataName);
                            break;

                        //cases 4-7 are without SWO

                        case 4:
                            scheme5(data[j], dataName);
                            break;

                        case 5:
                            scheme6(data[j], dataName);
                            break;

                        case 6:
                            scheme7(data[j], dataName);
                            break;

                        case 7:
                            scheme8(data[j], dataName);
                            break;


                    }

                }
            }

        }

        catch(Exception e) {

            System.out.println(e);



        }




    }


    //my output has schemes 1-4 with hillClimbRandom(100, 20, false)

    static void scheme1(String[] data, String dataName) {

        System.out.println("degree");


        Graph g = new Graph(data[0], data[1]);

        g.largestVertexHeuristicColoring("degree");
        g.squeakyWheelOptimization(400);

//        g.hillClimb(20, true  );

        g.hillClimbRandom(100, 50, false);

        g.printStats();

        g.outputSolution(dataName + "-scheme-1");





    }

    static void scheme2(String[] data, String dataName) {

        System.out.println("weighted-degree");


        Graph g = new Graph(data[0], data[1]);

        g.largestVertexHeuristicColoring("weighted-degree");
        g.squeakyWheelOptimization(400);
//        g.hillClimb(20, false);
        g.hillClimbRandom(100, 50, false);

        g.printStats();

        g.outputSolution(dataName + "-scheme-2");





    }

    static void scheme3(String[] data, String dataName) {

        System.out.println("enrollment");


        Graph g = new Graph(data[0], data[1]);

        g.largestVertexHeuristicColoring("enrollment");
        g.squeakyWheelOptimization(400);
//        g.hillClimb(20, false);
        g.hillClimbRandom(100, 50, false);

        g.printStats();
        g.outputSolution(dataName + "-scheme-3");




    }

    static void scheme4(String[] data, String dataName) {

        System.out.println("dSatur");


        Graph g = new Graph(data[0], data[1]);

        g.dSaturColoring();
        g.squeakyWheelOptimization(400);
//        g.hillClimb(100, true);
        g.hillClimbRandom(100, 50, false);

        g.printStats();

        g.outputSolution(dataName + "-scheme-4");

    }


    //schemes 5-8 are without SWO


    static void scheme5(String[] data, String dataName) {

        System.out.println("degree");


        Graph g = new Graph(data[0], data[1]);

        g.largestVertexHeuristicColoring("degree");
        g.hillClimbRandom(100, 50, false);

        g.printStats();

        g.outputSolution(dataName + "-scheme-5");

    }

    static void scheme6(String[] data, String dataName) {

        System.out.println("weighted-degree");


        Graph g = new Graph(data[0], data[1]);

        g.largestVertexHeuristicColoring("weighted-degree");
        g.hillClimbRandom(100, 50, false);

        g.printStats();

        g.outputSolution(dataName + "-scheme-6");

    }

    static void scheme7(String[] data, String dataName) {

        System.out.println("enrollment");


        Graph g = new Graph(data[0], data[1]);

        g.largestVertexHeuristicColoring("enrollment");
        g.hillClimbRandom(100, 50, false);

        g.printStats();

        g.outputSolution(dataName + "-scheme-7");

    }

    static void scheme8(String[] data, String dataName) {

        System.out.println("dSatur");


        Graph g = new Graph(data[0], data[1]);

        g.dSaturColoring();
        g.hillClimbRandom(100, 50, false);

        g.printStats();

        g.outputSolution(dataName + "-scheme-8");

    }






}
